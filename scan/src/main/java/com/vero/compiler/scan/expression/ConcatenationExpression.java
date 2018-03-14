package com.vero.compiler.scan.expression;


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
public class ConcatenationExpression extends RegularExpression
{
    private RegularExpression left;

    private RegularExpression right;

    public ConcatenationExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    public ConcatenationExpression(RegularExpressionType expressionType, RegularExpression left,
                                   RegularExpression right)
    {
        super(expressionType);
        this.left = left;
        this.right = right;
    }

    public ConcatenationExpression(RegularExpressionType expressionType,
                                   RegularExpressionConverter regularExpressionConverter,
                                   RegularExpression left, RegularExpression right)
    {
        super(expressionType, regularExpressionConverter);
        this.left = left;
        this.right = right;
    }

    @Override
    public NFAModel accept()
    {
        return regularExpressionConverter.convertConcatenation(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        return null;
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return getLeft().toString() + getRight().toString();
    }
}
