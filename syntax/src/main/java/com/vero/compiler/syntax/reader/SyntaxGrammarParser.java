package com.vero.compiler.syntax.reader;


import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.exception.InValidSyntaxGrammarException;
import com.vero.compiler.exception.ParseException;
import com.vero.compiler.exception.TokenDefinitionsNotFoundException;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.scan.core.*;
import com.vero.compiler.scan.core.Scanner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 语法的文法的解析器 需要利用{@link com.vero.compiler.lexer.core.Lexer}词法分析定义合法的
 * {@link com.vero.compiler.lexer.token.Token} 不符合所有异常
 *
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:07 2018/3/22.
 * @since vero-compiler
 */

@Slf4j
@Getter
public class SyntaxGrammarParser
{
    private LexiconContent syntaxLexiconContent;

    private List<Lexeme> lexemeStream = new ArrayList<>();

    private File grammarSource;

    private FiniteAutomationMachineEngine engine;

    private SyntaxTokenDefinitions tokenDefinitions;

    public SyntaxGrammarParser(LexiconContent syntaxLexiconContent, File grammarSource)
    {
        initParser(syntaxLexiconContent);
        this.grammarSource = grammarSource;
        this.parse(grammarSource);
    }

    private void initParser(LexiconContent syntaxLexiconContent)
    {
        this.syntaxLexiconContent = syntaxLexiconContent;
        this.engine = syntaxLexiconContent.getScanner().getEngine();
        TokenDefinitions tokenDefinitions = syntaxLexiconContent.getDefinitions();
        if (tokenDefinitions instanceof SyntaxTokenDefinitions)
        {
            this.tokenDefinitions = (SyntaxTokenDefinitions)tokenDefinitions;
        }
    }

    public SyntaxGrammarParser(LexiconContent syntaxLexiconContent)
    {
        this.syntaxLexiconContent = syntaxLexiconContent;
        initParser(syntaxLexiconContent);
    }

    public List<Lexeme> parse(File grammarSource)
    {
        Scanner scanner = this.syntaxLexiconContent.getScanner();
        scanner.changeSourceFile(grammarSource);
        LexemeCollector collector = syntaxLexiconContent.buildCollector();
        collector.collect(grammarSource);
        List<CompilationError> errors = collector.getErrors();
        if (!errors.isEmpty())
        {
            if (log.isErrorEnabled())
            {
                errors.forEach(e -> log.error(e.toString()));
            }
            throw new InValidSyntaxGrammarException(
                "Invalid syntax grammar definition.Each computeTerminals symbol of the grammar should match the lexer token pattern.");
        }
        this.lexemeStream = collector.getLexemeStream();
        return this.lexemeStream;
    }

    public List<String> parseProductionRows()
    {
        List<String> productionRows = new ArrayList<>();
        List<Lexeme> lexemeStream = getLexemeStream();
        StringBuilder stringBuilder = new StringBuilder();
        validateLexemeStream(lexemeStream);
        for (Lexeme l : lexemeStream)
        {
            if (!l.getContent().equals("\r\n"))
            {
                stringBuilder.append(l.getContent());
            }
            else
            {
                productionRows.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }
        return productionRows;
    }

    private void validateLexemeStream(List<Lexeme> lexemeStream)
    {
        if (Objects.isNull(lexemeStream) || lexemeStream.isEmpty())
        {
            throw new ParseException("Lexeme stream is empty.Please call parse procedure firstly");
        }
    }

    public Map<String, List<List<String>>> parseProduction()
    {
        SyntaxTokenDefinitions definitions = getTokenDefinitions();
        if (definitions == null)
        {
            throw new TokenDefinitionsNotFoundException(
                "Syntax grammar definition is not defined.");
        }
        // 过滤掉所有空格
        List<Lexeme> lexemeStream = getLexemeStream().stream().filter(
            l -> !l.getContent().equals(" ")).collect(Collectors.toList());
        validateLexemeStream(lexemeStream);
        Map<String, List<List<String>>> productionCutMap = new HashMap<>();
        // Integer BANF_DELIMITER_INDEX = definitions.BANF_DELIMITER.getIndex();
        // Integer SYNTAX_NO_TERMINAL_INDEX = definitions.SYNTAX_NO_TERMINAL.getIndex();
        // Integer SYNTAX_TOKEN_INDEX = definitions.SYNTAX_TOKEN.getIndex();
        Integer UNION_DELIMITER_INDEX = definitions.UNION_DELIMITER.getIndex();
        boolean isNewLine = false;
        String left = "";
        List<List<String>> rightParts = new ArrayList<>();
        Iterator<Lexeme> iterator = lexemeStream.iterator();
        while (iterator.hasNext())
        {
            Lexeme l = iterator.next();
            Integer tokenIndex = l.getTokenIndex();
            if (l.getContent().equals("\r\n")) {
                iterator.next();
                continue;
            }
            // 读取左部
            left = l.getContent();
            // 跳过::=
            iterator.next();
            l = iterator.next();
            boolean isUnion;
            List<String> rightPart = new ArrayList<>();
            rightParts = getRightParts(productionCutMap, left);
            tokenIndex = l.getTokenIndex();
            while (iterator.hasNext())
            {
                isNewLine = l.getContent().equals("\r\n");
                isUnion = tokenIndex.equals(UNION_DELIMITER_INDEX);
                if (isNewLine)
                {
                    break;
                }
                if (isUnion)
                {
                    rightParts.add(rightPart);
                    rightPart = new ArrayList<>();
                }
                else
                {
                    rightPart.add(l.getContent());
                }
                l = iterator.next();
                tokenIndex = l.getTokenIndex();
            }
            if (!iterator.hasNext())
            {
                rightPart.add(l.getContent());
            }
            if (isNewLine || !iterator.hasNext())
            {
                rightParts.add(rightPart);
                productionCutMap.put(left, rightParts);
                if (log.isDebugEnabled())
                {
                    log.debug("Parsed the production: ＊ left:[{}] ::= ＊ right :{}", left,
                        rightPart);
                }
            }
        }
        return productionCutMap;
    }

    private List<List<String>> getRightParts(Map<String, List<List<String>>> productionCutMap,
                                             String left)
    {
        List<List<String>> rightParts;
        rightParts = productionCutMap.get(left);
        if (Objects.isNull(rightParts))
        {
            rightParts = new ArrayList<>();
        }
        return rightParts;
    }
}
