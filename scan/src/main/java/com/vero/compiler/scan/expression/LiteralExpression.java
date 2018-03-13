package com.vero.compiler.scan.expression;


import com.vero.compiler.scan.converter.ExpressionConverter;
import com.vero.compiler.scan.generator.NFAModel;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 19:02 2018/3/12.
 * @since vero-compiler
 */

public class LiteralExpression extends RegularExpression
{
    public LiteralExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    @Override
    public NFAModel accept(ExpressionConverter<NFAModel> expressionConverter)
    {
        return null;
    }
}
