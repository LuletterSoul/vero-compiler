package com.vero.compiler.scan.expression;


import java.util.HashSet;
import java.util.List;

import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.NFAModel;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:28 2018/3/10.
 * @since vero-compiler
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class KleeneStarExpression extends RegularExpression
{
    private RegularExpression innerExpression;

    public KleeneStarExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    public KleeneStarExpression(RegularExpressionType expressionType,
                                RegularExpression innerExpression)
    {
        super(expressionType);
        this.innerExpression = innerExpression;
    }

    @Override
    public NFAModel accept(RegularExpressionConverter converter)
    {
        return converter.convertKleeneStar(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        return getInnerExpression().getUnCompressibleCharSet();
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return getInnerExpression().getCompressibleCharSets();
    }

    @Override
    public String toString()
    {
        return '(' + getInnerExpression().toString() + ")*";
    }
}
