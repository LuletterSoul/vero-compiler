package com.vero.compiler.scan.core;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.token.TokenType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 15:58 2018/3/21.
 * @since vero-compiler
 */

@Slf4j
@Getter
public class LexemeCollector
{
    private Scanner scanner;

    private LexerTransitionInfo lexerTransitionInfo;

    private List<Lexeme> tokenStream = new ArrayList<>();

    private List<Map<Integer, TokenType>> index2TokenType = new ArrayList<>();

    private List<CompilationError> errors = new ArrayList<>();

    public LexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo,
                           TokenDefinitions tokenDefinitions)
    {
        this.scanner = scanner;
        this.lexerTransitionInfo = lexerTransitionInfo;
        this.index2TokenType = tokenDefinitions.getTokenIndex2TokenTypeList();
    }

    public Map<String, String> collect(File source)
    {
        this.tokenStream.clear();
        scanner.changeSourceFile(source);
        Lexeme lexeme = scanner.read();
        Map<String, String> lexeme2TokenType = new HashMap<>();
        addLexemeEntry(lexeme, lexeme2TokenType);
        log.debug("Gain a lexeme:[{}]", lexeme.getContent());
        while (!lexeme.getTokenIndex().equals(lexerTransitionInfo.getEndOfStreamTokenIndex()))
        {
            lexeme = scanner.read();
            addLexemeEntry(lexeme, lexeme2TokenType);
            log.debug("Gain a lexeme:[{}]", lexeme.getContent());
        }
        //保存扫描过程的错误
        this.errors = scanner.getErrorsList();
        return lexeme2TokenType;
    }

    private void addLexemeEntry(Lexeme lexeme, Map<String, String> lexeme2TokenType)
    {
        if (lexeme.isEndOfStream())
        {
            return;
        }
        this.tokenStream.add(lexeme);
        TokenType tokenType = index2TokenType.get(lexerTransitionInfo.getLexerState()).get(
            lexeme.getTokenIndex());
        lexeme2TokenType.put(lexeme.getContent(), tokenType.getTypeDetail());
    }
}
