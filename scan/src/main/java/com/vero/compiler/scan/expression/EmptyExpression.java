package com.vero.compiler.scan.expression;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.NFAModel;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:27 2018/3/10.
 * @since vero-compiler
 */

@Data
public class EmptyExpression extends RegularExpression
{
    public EmptyExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    @Override
    public NFAModel accept(RegularExpressionConverter converter)
    {
        return converter.convertEmpty(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        return new HashSet<>();
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return "ε";
    }
}
