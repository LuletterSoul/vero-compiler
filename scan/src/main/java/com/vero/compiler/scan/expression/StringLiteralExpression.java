package com.vero.compiler.scan.expression;


import java.util.ArrayList;
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
public class StringLiteralExpression extends RegularExpression
{
    private String literal;

    public StringLiteralExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    public StringLiteralExpression(RegularExpressionType expressionType, String literal)
    {
        super(expressionType);
        this.literal = literal;
    }

    public StringLiteralExpression(RegularExpressionType expressionType,
                                   RegularExpressionConverter regularExpressionConverter,
                                   String literal)
    {
        super(expressionType, regularExpressionConverter);
        this.literal = literal;
    }

    @Override
    public NFAModel accept()
    {
        return null;
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        HashSet<Character> characters = new HashSet<>();
        for (char c : getLiteral().toCharArray())
        {
            characters.add(c);
        }
        return characters;
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return getLiteral();
    }
}
