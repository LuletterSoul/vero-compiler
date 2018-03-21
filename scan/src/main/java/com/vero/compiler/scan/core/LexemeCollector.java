package com.vero.compiler.scan.core;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.token.TokenType;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 15:58 2018/3/21.
 * @since vero-compiler
 */

@Slf4j
public class LexemeCollector
{
    private Scanner scanner;

    private LexerTransitionInfo lexerTransitionInfo;

    private List<Map<Integer, TokenType>> index2TokenType = new ArrayList<>();

    public LexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo,
                           TokenDefinitions tokenDefinitions)
    {
        this.scanner = scanner;
        this.lexerTransitionInfo = lexerTransitionInfo;
        this.index2TokenType = tokenDefinitions.getTokenIndex2TokenTypeList();
    }

    public Map<String, String> collect(File source)
    {
        scanner.changeSourceFile(source);
        Lexeme lexeme = scanner.read();
        Map<String, String> lexeme2TokenType = new HashMap<>();
        addLexemeEntry(lexeme, lexeme2TokenType);
        log.debug("Gain a lexeme:[{}]",lexeme.getContent());
        while (!lexeme.getTokenIndex().equals(lexerTransitionInfo.getEndOfStreamTokenIndex()))
        {
            lexeme = scanner.read();
            addLexemeEntry(lexeme, lexeme2TokenType);
            log.debug("Gain a lexeme:[{}]",lexeme.getContent());
        }
        return lexeme2TokenType;
    }

    private void addLexemeEntry(Lexeme lexeme, Map<String, String> lexeme2TokenType) {
        if (lexeme.isEndOfStream()) {
            return;
        }
        TokenType tokenType = index2TokenType.get(lexerTransitionInfo.getLexerState()).get(
            lexeme.getTokenIndex());
        lexeme2TokenType.put(lexeme.getContent(), tokenType.getTypeDetail());
    }
}
