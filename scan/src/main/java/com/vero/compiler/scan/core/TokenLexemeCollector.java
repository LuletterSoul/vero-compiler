package com.vero.compiler.scan.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.token.TokenType;

import com.vero.compiler.parser.LexemeDto;
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

    private List<String> tokenTypeStream = new ArrayList<>();

    private List<LexemeDto> lexemeDtos = new ArrayList<>();

    public TokenLexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo,
                                TokenDefinitions lexerTokenDefinitions)
    {
        super(scanner, lexerTransitionInfo);
        if (lexerTokenDefinitions instanceof LexerTokenDefinitions)
        {
            LexerTokenDefinitions definitions = (LexerTokenDefinitions)lexerTokenDefinitions;
            this.index2TokenType = definitions.getTokenIndex2TokenTypeList();
        }
    }

    @Override
    public void postPerLexemeCollected(Lexeme lexeme)
    {
        super.postPerLexemeCollected(lexeme);
        this.addLexemeEntry(lexeme);
    }

    private void addLexemeEntry(Lexeme lexeme)
    {
        if (lexeme.isEndOfStream())
        {
            lexeme2TokenDetail.put(lexeme.getContent(), TokenType.EOF.getType());
            return;
        }
        TokenType tokenType = index2TokenType.get(lexerTransitionInfo.getLexerState()).get(
            lexeme.getTokenIndex());
        lexeme2TokenDetail.put(lexeme.getContent(), tokenType.getTypeDetail());
//        lexemeDtos.add(new A);
        if (tokenType == TokenType.VAR || tokenType == TokenType.UNSIGNED_NUMBER
            || tokenType == TokenType.SKIPPABLE)
        {
            tokenTypeStream.add(tokenType.getType());
        }
        else
        {
            tokenTypeStream.add(lexeme.getContent());
        }

    }
}
