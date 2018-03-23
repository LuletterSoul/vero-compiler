package com.vero.compiler.scan.core;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.common.error.CompilationErrorInfo;
import com.vero.compiler.common.error.CompilationStage;
import com.vero.compiler.common.location.SourceLocation;
import com.vero.compiler.common.location.SourceSpan;
import com.vero.compiler.exception.ScanException;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.source.SourceReader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:46 2018/3/18.
 * @since vero-compiler
 */

@Getter
@Slf4j
public class Scanner
{
    private final Integer c_skip = 1;

    private FiniteAutomationMachineEngine engine;

    private List<Lexeme> fullHistory = new ArrayList<>();

    private HistoryList historyList;

    private Integer lastNotSkippedLexemeIndex;

    private Integer lastState;

    private SourceLocation lastTokenStart;

    private StringBuilder lexemeValueBuilder;

    private LexerTransitionInfo lexerTransitionInfo;

    private SourceReader sourceReader;

    private Integer[] tokenAttributes;

    private Integer valueableCursor;

    private List<Integer> valueableHistory;

    private boolean recoveryErrors = true;

    private boolean throwAtReadingAfterEndOfStream = false;

    private List<CompilationError> errorsList = new ArrayList<>();

    private Integer currentErrorId = 0;

    public Scanner(LexerTransitionInfo lexerTransitionInfo)
    {
        this.lexerTransitionInfo = lexerTransitionInfo;
        this.engine = new FiniteAutomationMachineEngine(lexerTransitionInfo.getCharClassTable(),
            lexerTransitionInfo.getTransitionTable());
        this.lexemeValueBuilder = new StringBuilder();
        this.tokenAttributes = new Integer[lexerTransitionInfo.getTokenNum()];
        for (int i = 0; i < tokenAttributes.length; i++ )
        {
            tokenAttributes[i] = 0;
        }
        initialize();
    }

    public Scanner(LexerTransitionInfo lexerTransitionInfo, SourceReader sourceReader)
    {
        this(lexerTransitionInfo);
        this.sourceReader = sourceReader;
    }

    private void initialize()
    {
        engine.resetMachineState();
        this.lastState = 0;
        this.fullHistory = new ArrayList<>();
        this.valueableHistory = new ArrayList<>();
        this.historyList = new HistoryList(fullHistory, valueableHistory);
        this.valueableCursor = 0;
        this.lastNotSkippedLexemeIndex = 0;
    }

    public Lexeme read()
    {
        if (getValueableCursor() < getValueableHistory().size())
        {
            Integer fullCursor = this.valueableHistory.get(this.valueableCursor++ );
            return this.fullHistory.get(fullCursor);
        }
        while (true)
        {
            this.engine.resetMachineState();
            this.lastTokenStart = this.sourceReader.getPreLocation();
            this.lastState = 0;
            lexemeValueBuilder = new StringBuilder();

            if (this.sourceReader.peekChar() == Character.MAX_VALUE)
            {
                if (isThrowAtReadingAfterEndOfStream() && isReachedEndOfStream())
                {
                    throw new ScanException("The scanner has reached the end of stream");
                }
                addHistory(new Lexeme(this.lexerTransitionInfo,
                    this.lexerTransitionInfo.getEndOfStreamState(),
                    new SourceSpan(this.lastTokenStart, this.lastTokenStart), null), true);
                break;
            }
            while (true)
            {
                Integer inputCharValue = sourceReader.peekChar();

                if (inputCharValue < 0)
                {
                    break;
                }
                char inputChar = (char)inputCharValue.intValue();
                this.engine.input(inputChar);
                if (engine.isAtStoppedState())
                {
                    break;
                }
                sourceReader.readChar();
                this.lastState = this.engine.getCurrentStateIndex();
                this.lexemeValueBuilder.append(inputChar);
            }
            if (isLastTokenSkippable())
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Skip the token [{}].", lexemeValueBuilder.toString());
                }
                addHistory(new Lexeme(this.lexerTransitionInfo, this.lastState,
                    new SourceSpan(this.lastTokenStart, sourceReader.getPreLocation()),
                    lexemeValueBuilder.toString()), false);
            }
            else
            {
                addHistory(new Lexeme(this.lexerTransitionInfo, this.lastState,
                    new SourceSpan(this.lastTokenStart, sourceReader.getPreLocation()),
                    lexemeValueBuilder.toString()), true);
                break;
            }
        }
        Integer lastTokenFullCursor = getValueableHistory().get(this.valueableCursor - 1);
        return this.fullHistory.get(lastTokenFullCursor);
    }

    private boolean isLastTokenSkippable()
    {
        int acceptTokenIndex = this.getLexerTransitionInfo().getTokenIndex(getLastState());

        if (acceptTokenIndex < 0)
        {
            // eat one char to continue
            lexemeValueBuilder.append(sourceReader.readChar());
            if (this.errorsList != null)
            {
                Integer level = getLexerTransitionInfo().getLexerState();
                CompilationErrorInfo info = new CompilationErrorInfo(getCurrentErrorId(), level,
                    CompilationStage.Scanning);
                SourceSpan errorPosition = new SourceSpan(getLastTokenStart(),
                    sourceReader.getLocation());
                CompilationError error = new CompilationError(info, lexemeValueBuilder.toString(),
                    errorPosition);
                errorsList.add(error);
            }
            return true;
        }

        return getTokenAttributes()[acceptTokenIndex].equals(c_skip);
    }

    public void setTriviaTokens(Integer[] triviaTokenIndices)
    {
        for (int i = 0; i < tokenAttributes.length; i++ )
        {
            tokenAttributes[i] = 0;
        }
        for (int i = 0; i < triviaTokenIndices.length; i++ )
        {
            int skipIndex = triviaTokenIndices[i];
            if (skipIndex >= 0 && skipIndex < tokenAttributes.length)
            {
                tokenAttributes[skipIndex] = c_skip;
            }
        }
    }

    private void addHistory(Lexeme lexeme, boolean setTrivia)
    {
        this.fullHistory.add(lexeme);
        Integer fullCursor = this.fullHistory.size();
        if (setTrivia)
        {
            this.lastNotSkippedLexemeIndex = fullCursor - 1;
            this.valueableHistory.add(fullCursor - 1);
            this.valueableCursor = this.valueableHistory.size();
        }

    }

    private boolean isReachedEndOfStream()
    {
        if (getFullHistory().size() == 0)
        {
            return false;
        }
        Lexeme lastLexeme = this.fullHistory.get(this.fullHistory.size() - 1);
        return lastLexeme.isEndOfStream();
    }

    public void changeSourceFile(File file)
    {
        this.sourceReader.loadFileSource(file);
    }
}
