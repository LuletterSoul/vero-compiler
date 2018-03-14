package com.vero.compiler.scan.expression;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.scan.converter.RegularExpressionConverter;
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
    private RegularExpression expression1;

    private RegularExpression expression2;

    public AlternationExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    public AlternationExpression(RegularExpressionType expressionType,
                                 RegularExpression expression1, RegularExpression expression2)
    {
        super(expressionType);
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public NFAModel accept(RegularExpressionConverter converter)
    {
        return converter.convertAlternation(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        HashSet<Character> exp1Clone = new HashSet<>(getExpression1().getUnCompressibleCharSet());

        HashSet<Character> exp2Clone = new HashSet<>(getExpression2().getUnCompressibleCharSet());

        exp1Clone.addAll(exp2Clone);

        return exp2Clone;
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        List<HashSet> exp1Clone = new ArrayList<>(getExpression1().getCompressibleCharSets());

        List<HashSet> exp2Clone = new ArrayList<>(getExpression2().getCompressibleCharSets());

        exp1Clone.addAll(exp2Clone);
        return exp1Clone;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("(").append(getExpression1().toString()).append('|').append(
            getExpression2().toString()).append(")").toString();
    }
}
