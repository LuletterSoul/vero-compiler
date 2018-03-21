package com.vero.compiler.lexer.builder;


import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.lexer.expression.*;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:16 2018/3/14.
 * @since vero-compiler
 */

public class RegularExpressionBuilder
{
    public RegularExpression Symbol(char c)
    {
        return new SymbolExpression(RegularExpressionType.Symbol, c);
    }

    public RegularExpression Many(RegularExpression regularExpression)
    {
        if (regularExpression.getExpressionType().equals(RegularExpressionType.KleeneStar))
        {
            return regularExpression;
        }

        return new KleeneStarExpression(RegularExpressionType.KleeneStar, regularExpression);
    }

    public RegularExpression Concat(RegularExpression left, RegularExpression right)
    {
        return new ConcatenationExpression(RegularExpressionType.Concatenation, left, right);
    }

    public RegularExpression Union(RegularExpression expression1, RegularExpression expression2)
    {
        if (expression1.toString().equals(expression2.toString()))
        {
            return expression1;
        }
        return new AlternationExpression(RegularExpressionType.Alternation, expression1,
            expression2);
    }

    public RegularExpression Literal(String literal)
    {
        return new StringLiteralExpression(RegularExpressionType.StringLiteral, literal);
    }

    public RegularExpression CharSet(List<Character> charSet)
    {
        return new AlternationCharSetExpression(RegularExpressionType.AlternationCharSet, charSet);
    }

    public RegularExpression Empty()
    {
        return new EmptyExpression(RegularExpressionType.Empty);
    }

    // extended operations

    public RegularExpression Many1(RegularExpression base)
    {
        return Concat(base, Many(base));
    }

    public RegularExpression Optional(RegularExpression base)
    {
        return Union(base, Empty());
    }

    public RegularExpression Range(Character min, Character max)
    {
        List<Character> rangeCharSet = new ArrayList<>();
        for (char c = min; c <= max; c++ )
        {
            rangeCharSet.add(c);
        }

        return new AlternationCharSetExpression(RegularExpressionType.AlternationCharSet,
            rangeCharSet);
    }

    public RegularExpression Repeat(int number, RegularExpression expression)
    {
        if (number <= 0)
        {
            return Empty();
        }

        RegularExpression result = expression;

        for (int i = 1; i < number; i++ )
        {
            result = Concat(result, expression);
        }

        return result;
    }
}
