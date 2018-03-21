package com.vero.compiler.lexer.expression;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.vero.compiler.exception.ConverterNotAvailableException;
import com.vero.compiler.lexer.builder.RegularExpressionBuilder;
import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.generator.NFAModel;

import lombok.Data;


/**
 * 正则表达式的基类 定义了一系列DFA的转化方法 转换的算法委托
 *
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:05 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class RegularExpression
{
    protected RegularExpressionType expressionType;

    protected List<HashSet> compressibleCharSets = new LinkedList<>();

    protected HashSet<Character> unCompressibleCharSet = new HashSet<>();

    private static RegularExpressionBuilder regularExpressionBuilder = new RegularExpressionBuilder();

    public RegularExpression()
    {}

    public RegularExpression(RegularExpressionType expressionType)
    {
        this.expressionType = expressionType;
    }

    public NFAModel accept(RegularExpressionConverter converter)
    {
        if (converter == null)
        {
            throw new ConverterNotAvailableException(
                "Regular expression transfer to NFA state required corresponding converter.");
        }
        return converter.convert(this);
    }

    public abstract HashSet<Character> getUnCompressibleCharSet();

    public abstract List<HashSet> getCompressibleCharSets();

    public static RegularExpression Symbol(char c)
    {
        return regularExpressionBuilder.Symbol(c);
    }

    public RegularExpression Many()
    {
        return regularExpressionBuilder.Many(this);
    }

    public static RegularExpression Empty()
    {
        return regularExpressionBuilder.Empty();
    }

    public RegularExpression Many1()
    {
        return this.Concat(this.Many());
    }

    public RegularExpression Optional()
    {
        return this.Union(Empty());
    }

    public static RegularExpression Range(char min, char max)
    {
        return regularExpressionBuilder.Range(min, max);
    }

    public RegularExpression Repeat(int number)
    {
        return regularExpressionBuilder.Repeat(number, this);
    }

    public RegularExpression Concat(RegularExpression follow)
    {
        return regularExpressionBuilder.Concat(this, follow);
    }

    public RegularExpression Union(RegularExpression other)
    {
        return regularExpressionBuilder.Union(this, other);
    }

    public static RegularExpression Literal(String literal)
    {
        return regularExpressionBuilder.Literal(literal);
    }

    public static RegularExpression CharSet(List<Character> charSet)
    {
        return regularExpressionBuilder.CharSet(charSet);
    }

    public static RegularExpression CharSet(char[] chars)
    {
        List<Character> characters = new ArrayList<>();
        for (int i = 0; i < chars.length; i++ )
        {
            characters.add(chars[i]);
        }
        return regularExpressionBuilder.CharSet(characters);
    }

}
