//package com.vero.compiler.scan.core;
//
//import com.vero.compiler.lexer.core.Lexicon;
//import com.vero.compiler.lexer.expression.RegularExpression;
//import com.vero.compiler.lexer.token.Token;
//
///**
// * @author XiangDe Liu qq313700046@icloud.com .
// * @version 1.5
// * created in  19:27 2018/3/23.
// * @since vero-compiler
// */
//
//public class LexerTokenGrammarDefinition extends DefaultTokenDefinitions {
//
//    private Token QUOTE;
//
//    private Token DELIMITER;
//
//    private Token UNION;
//
//
//
//    @Override
//    public void initTokenDefinitions(Lexicon lexicon, RegularExpression[] tokenExpressions) {
//        // 关键字
//        this.KEY_WORDS = lexer.defineToken(tokenExpressions[TokenType.KEY_WORDS.getPriority()]);
//        mapTokenIndexToTokenType(this.KEY_WORDS, TokenType.KEY_WORDS);
//
//        // 变量名
//        this.VAR_NAME = lexer.defineToken(tokenExpressions[TokenType.VAR.getPriority()],
//                "identifier");
//        mapTokenIndexToTokenType(this.VAR_NAME, TokenType.VAR);
//
//        // 空格
//        this.WHITESPACE = lexer.defineToken(Symbol(' ').Many());
//        mapTokenIndexToTokenType(this.WHITESPACE, TokenType.WHITESPACE);
//
//        RegularExpression ex = tokenExpressions[TokenType.UNSIGNED_NUMBER.getPriority()];
////
//        // 无符号实数
//        this.UNSIGNED_NUMBER = lexer.defineToken(ex);
//        mapTokenIndexToTokenType(this.UNSIGNED_NUMBER, TokenType.UNSIGNED_NUMBER);
//
//        // 虚数
//        this.COMPLEX = lexer.defineToken(ex.Concat(Symbol('+')).Concat(ex).Concat(Symbol('i')));
//        mapTokenIndexToTokenType(this.COMPLEX, TokenType.COMPLEX);
//
//        // 运算符
//        this.OPERATOR = lexer.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);
//        mapTokenIndexToTokenType(this.OPERATOR, TokenType.OPERATOR);
//
//        // 界符
//        this.DELIMITER = lexer.defineToken(tokenExpressions[TokenType.DELIMITER.getPriority()]);
//        mapTokenIndexToTokenType(this.DELIMITER, TokenType.OPERATOR);
//
//        // 回车换行
//        this.LINE_BREAKER = lexer.defineToken(Literal("\r\n"));
//        mapTokenIndexToTokenType(this.LINE_BREAKER, TokenType.LINE_BREAKER);
//
//        this.EMPTY = lexer.defineToken(Symbol('ε'));
//        mapTokenIndexToTokenType(this.EMPTY, TokenType.EMPTY);
//    }
//}
