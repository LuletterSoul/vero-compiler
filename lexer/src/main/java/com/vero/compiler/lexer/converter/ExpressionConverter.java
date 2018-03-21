package com.vero.compiler.lexer.converter;

import com.vero.compiler.lexer.expression.*;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  20:44 2018/3/10.
 * @since vero-compiler
 */

public interface ExpressionConverter<T> {

    T convertAlternation(AlternationExpression exp);

    T convertSymbol(SymbolExpression exp);

    T convertEmpty(EmptyExpression exp);

    T convertConcatenation(ConcatenationExpression exp);

    T convertAlternationCharSet(AlternationCharSetExpression exp);

    T convertStringLiteral(StringLiteralExpression exp);

    T convertKleeneStar(KleeneStarExpression exp);

}
