package com.vero.compiler.scan.core;


import static com.vero.compiler.lexer.expression.RegularExpression.Literal;
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
public class LexerTokenDefinitions extends DefaultTokenDefinitions
{

    // 关键字

    private Token KEY_WORDS;

    // 变量名
    private Token VAR_NAME;

    // 空格
    private Token WHITESPACE;

    // 回车换行
    private Token LINE_BREAKER;

    // 制表符
    private Token TABLE;

    // 无符号实数
    private Token UNSIGNED_NUMBER;

    // 虚数
    private Token COMPLEX;

    // 运算符
    private Token OPERATOR;

    // 界符
    private Token DELIMITER;

    // 空符
    private Token EMPTY;

    // 可忽略的字符
    private Token SKIPPABLE;

    private Token RE_DelimitedCommentSection;

    private Token COMMENT;

    private List<Map<Integer, TokenType>> tokenIndex2TokenTypeList = new ArrayList<>();

    public LexerTokenDefinitions(Lexicon lexicon, RegularExpression[] tokenExpressions)
    {

        initTokenDefinitions(lexicon, tokenExpressions);
    }

    @Override
    public void initTokenDefinitions(Lexicon lexicon, RegularExpression[] tokenExpressions)
    {
        Lexer lexer = lexicon.getDefaultLexer();

        initMapping(lexicon);

        // 关键字
        this.KEY_WORDS = lexer.defineToken(tokenExpressions[TokenType.KEY_WORDS.getPriority()]);
        mapTokenIndexToTokenType(this.KEY_WORDS, TokenType.KEY_WORDS);

        // 变量名
        this.VAR_NAME = lexer.defineToken(tokenExpressions[TokenType.VAR.getPriority()],
            "identifier");
        mapTokenIndexToTokenType(this.VAR_NAME, TokenType.VAR);

        this.SKIPPABLE = lexer.defineToken(
                Symbol(' ').Union(Literal("\r\n")).Union(
                        Symbol('\t')).Many());
        mapTokenIndexToTokenType(this.SKIPPABLE, TokenType.SKIPPABLE);

//        // 空格
//        this.WHITESPACE = lexer.defineToken(Symbol(' ').Many());
//        mapTokenIndexToTokenType(this.WHITESPACE, TokenType.WHITESPACE);
//
//        this.LINE_BREAKER = lexer.defineToken(Literal("\r\n").Many());
//        mapTokenIndexToTokenType(this.LINE_BREAKER, TokenType.LINE_BREAKER);
//
//        this.TABLE = lexer.defineToken(Symbol('\t'));
//        mapTokenIndexToTokenType(this.TABLE, TokenType.T);


        RegularExpression ex = tokenExpressions[TokenType.UNSIGNED_NUMBER.getPriority()];

        // 无符号实数
        this.UNSIGNED_NUMBER = lexer.defineToken(ex);
        mapTokenIndexToTokenType(this.UNSIGNED_NUMBER, TokenType.UNSIGNED_NUMBER);

        // 虚数
        this.COMPLEX = lexer.defineToken(ex.Concat(Symbol('+')).Concat(ex).Concat(Symbol('i')));
        mapTokenIndexToTokenType(this.COMPLEX, TokenType.COMPLEX);

        // 运算符
        this.OPERATOR = lexer.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);
        mapTokenIndexToTokenType(this.OPERATOR, TokenType.OPERATOR);

        // 界符
        this.DELIMITER = lexer.defineToken(tokenExpressions[TokenType.DELIMITER.getPriority()]);
        mapTokenIndexToTokenType(this.DELIMITER, TokenType.OPERATOR);

        // 回车换行

        this.EMPTY = lexer.defineToken(Symbol('ε'));
        mapTokenIndexToTokenType(this.EMPTY, TokenType.EMPTY);
    }

    private void initMapping(Lexicon lexicon)
    {
        Integer size = lexicon.getLexerStates().size();

        for (Integer i = 0; i < size; i++ )
        {
            tokenIndex2TokenTypeList.add(new HashMap<>());
        }
    }

    private void mapTokenIndexToTokenType(Token token, TokenType tokenType)
    {
        Map<Integer, TokenType> tokenIndex2TokenType = tokenIndex2TokenTypeList.get(
            token.getLexerIndex());
        tokenIndex2TokenType.put(token.getIndex(), tokenType);
    }
}
