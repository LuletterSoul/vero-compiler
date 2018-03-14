package com.vero.compiler.scan.converter;


import com.vero.compiler.scan.expression.*;
import com.vero.compiler.scan.generator.NFAModel;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:43 2018/3/10.
 * @since vero-compiler
 */

public abstract class RegularExpressionConverter implements ExpressionConverter<NFAModel>
{

    public NFAModel convert(RegularExpression expression)
    {
        if (expression == null) {
            return null;
        }
        return expression.accept();
    }

    @Override
    public NFAModel convertAlternation(AlternationExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertSymbol(SymbolExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertEmpty(EmptyExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertConcatenation(ConcatenationExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertAlternationCharSet(AlternationCharSetExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertStringLiteral(StringLiteralExpression exp)
    {
        return null;
    }

    @Override
    public NFAModel convertKleeneStar(KleeneStarExpression exp)
    {
        return null;
    }
}
