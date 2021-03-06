package com.vero.compiler.lexer.expression;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.generator.NFAModel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:27 2018/3/10.
 * @since vero-compiler
 */
@Data
@Slf4j
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

    @Override
    public NFAModel accept(RegularExpressionConverter converter)
    {
        return converter.convertConcatenation(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        HashSet<Character> exp1Clone = new HashSet<>(getLeft().getUnCompressibleCharSet());
        HashSet<Character> exp2Clone = new HashSet<>(getRight().getUnCompressibleCharSet());
        exp1Clone.addAll(exp2Clone);
        return exp1Clone;
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        List<HashSet> exp1Clone = new ArrayList<>(getLeft().getCompressibleCharSets());
        List<HashSet> exp2Clone = new ArrayList<>(getRight().getCompressibleCharSets());
        if(exp1Clone.toString().equals(exp2Clone.toString())){
            return exp1Clone;
        }
        exp1Clone.addAll(exp2Clone);
        return exp1Clone;
    }

    @Override
    public String toString()
    {
        return getLeft().toString() + getRight().toString();
    }
}
