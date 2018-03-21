package com.vero.compiler.scan.core;


import static com.vero.compiler.lexer.expression.RegularExpression.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.token.Token;
import com.vero.compiler.lexer.token.TokenType;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 16:43 2018/3/21.
 * @since vero-compiler
 */

@Getter
public class TokenDefinitions
{

    // 关键字

    private Token KEY_WORDS;

    // 变量名
    private Token VAR_NAME;

    // 空格
    private Token WHITESPACE;

    // 无符号实数
    private Token UNSIGNED_NUMBER;

    // 虚数
    private Token COMPLEX;

    // 运算符
    Token OPERATOR;

    //界符
    Token DELIMITER;

    private List<Map<Integer, TokenType>> tokenIndex2TokenTypeList = new ArrayList<>();

    public TokenDefinitions(Lexicon lexicon, RegularExpression[] tokenExpressions)
    {

        Lexer lexer = lexicon.getDefaultLexer();

        initMapping(lexicon);

        // 关键字
        this.KEY_WORDS = lexer.defineToken(tokenExpressions[TokenType.KEY_WORDS.getPriority()]);
        mapTokenIndexToTokenType(this.KEY_WORDS,TokenType.KEY_WORDS);
        // 变量名
        this.VAR_NAME = lexer.defineToken(tokenExpressions[TokenType.VAR.getPriority()]);
        mapTokenIndexToTokenType(this.VAR_NAME,TokenType.VAR);

        // 空格
        this.WHITESPACE = lexer.defineToken(Symbol(' ').Many());
        mapTokenIndexToTokenType(this.WHITESPACE, TokenType.WHITESPACE);

        RegularExpression ex = tokenExpressions[TokenType.UNSIGNED_NUMBER.getPriority()];

        // 无符号实数
        this.UNSIGNED_NUMBER = lexer.defineToken(ex);
        mapTokenIndexToTokenType(this.UNSIGNED_NUMBER,TokenType.UNSIGNED_NUMBER);

        // 虚数
        this.COMPLEX = lexer.defineToken(ex.Concat(Symbol('+')).Concat(ex).Concat(Symbol('i')));
        mapTokenIndexToTokenType(this.COMPLEX, TokenType.COMPLEX);

        // 运算符
        this.OPERATOR = lexer.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);
        mapTokenIndexToTokenType(this.OPERATOR, TokenType.OPERATOR);

        //界符
        this.DELIMITER = lexer.defineToken(tokenExpressions[TokenType.DELIMITER.getPriority()]);
        mapTokenIndexToTokenType(this.DELIMITER, TokenType.OPERATOR);

    }

    private void initMapping(Lexicon lexicon)
    {
        Integer size = lexicon.getLexerStates().size();

        for (Integer i = 0; i < size; i++ )
        {
            tokenIndex2TokenTypeList.add(new HashMap<>());
        }
    }

    private void mapTokenIndexToTokenType(Token token,TokenType tokenType)
    {
        Map<Integer, TokenType> tokenIndex2TokenType = tokenIndex2TokenTypeList.get(
            token.getLexerIndex());
        tokenIndex2TokenType.put(token.getIndex(), tokenType);
    }
}
