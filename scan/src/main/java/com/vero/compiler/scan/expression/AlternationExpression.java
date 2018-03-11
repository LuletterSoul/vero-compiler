package com.vero.compiler.scan.expression;


import com.vero.compiler.scan.converter.ExpressionConverter;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.generator.NFAModel;
import lombok.Data;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:41 2018/3/10.
 * @since vero-compiler
 */

@Data
public class AlternationExpression extends RegularExpression
{
    public AlternationExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    @Override
    public NFAModel accept(ExpressionConverter<NFAModel> expressionConverter) {
        return null;
    }


    private RegularExpression expression1;

    private RegularExpression expression2;


}
