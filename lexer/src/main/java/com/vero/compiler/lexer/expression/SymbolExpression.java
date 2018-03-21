package com.vero.compiler.lexer.expression;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.generator.NFAModel;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:27 2018/3/10.
 * @since vero-compiler
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SymbolExpression extends RegularExpression
{
    private Character symbol;

    public SymbolExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
    }

    public SymbolExpression(RegularExpressionType expressionType, Character symbol)
    {
        super(expressionType);
        this.symbol = symbol;
    }

    @Override
    public NFAModel accept(RegularExpressionConverter converter)
    {
        return converter.convertSymbol(this);
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        HashSet<Character> characterHashSet = new HashSet<>();
        characterHashSet.add(getSymbol());
        return characterHashSet;
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return new ArrayList<>();
    }

    @Override
    public String toString()
    {
        return symbol.toString();
    }
}
