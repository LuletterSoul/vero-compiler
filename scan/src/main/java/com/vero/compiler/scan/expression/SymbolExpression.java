package com.vero.compiler.scan.expression;

import com.vero.compiler.scan.converter.ExpressionConverter;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.generator.NFAModel;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:27 2018/3/10.
 * @since vero-compiler
 */

public class SymbolExpression extends RegularExpression
{
    public SymbolExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    @Override
    public NFAModel accept(ExpressionConverter<NFAModel> expressionConverter) {
        return null;
    }


}
