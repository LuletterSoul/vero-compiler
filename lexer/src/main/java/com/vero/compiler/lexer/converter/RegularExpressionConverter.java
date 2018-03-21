package com.vero.compiler.lexer.converter;


import com.vero.compiler.lexer.compress.CompactCharSetManager;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.generator.NFAModel;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:43 2018/3/10.
 * @since vero-compiler
 */

@Getter
public abstract class RegularExpressionConverter implements ExpressionConverter<NFAModel>
{

    protected CompactCharSetManager compactCharSetManager;

    public RegularExpressionConverter(CompactCharSetManager compactCharSetManager)
    {
        this.compactCharSetManager = compactCharSetManager;
    }

    public NFAModel convert(RegularExpression expression)
    {
        if (expression == null)
        {
            return null;
        }
        // 含递归
        return expression.accept(this);
    }
}
