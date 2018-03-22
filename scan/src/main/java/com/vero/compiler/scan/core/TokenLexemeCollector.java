package com.vero.compiler.scan.core;


import java.util.List;
import java.util.Map;

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
public class TokenLexemeCollector extends DefaultLexemeCollector
{

    private List<Map<Integer, TokenType>> index2TokenType;

    public TokenLexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo,
                                TokenDefinitions tokenDefinitions)
    {
        super(scanner, lexerTransitionInfo);
        this.index2TokenType = tokenDefinitions.getTokenIndex2TokenTypeList();
    }

    @Override
    public void postPerLexemeCollected(Lexeme lexeme)
    {
        super.postPerLexemeCollected(lexeme);
        this.addLexemeEntry(lexeme);
    }

    private void addLexemeEntry(Lexeme lexeme)
    {
        TokenType tokenType = index2TokenType.get(lexerTransitionInfo.getLexerState()).get(
            lexeme.getTokenIndex());
        lexeme2TokenType.put(lexeme.getContent(), tokenType.getTypeDetail());
    }
}
