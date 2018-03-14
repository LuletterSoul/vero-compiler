package com.vero.compiler.scan.expression;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.NFAModel;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:28 2018/3/10.
 * @since vero-compiler
 */

@Getter
public class AlternationCharSetExpression extends RegularExpression
{
    List<Character> characters;

    public AlternationCharSetExpression(RegularExpressionType expressionType)
    {
        super(expressionType);
        this.characters = new ArrayList<>();
    }

    public AlternationCharSetExpression(RegularExpressionType expressionType,
                                        RegularExpressionConverter regularExpressionConverter)
    {

        super(expressionType, regularExpressionConverter);
        this.characters = new ArrayList<>();
    }

    public AlternationCharSetExpression(RegularExpressionType expressionType,
                                        List<Character> characters)
    {
        super(expressionType);
        this.characters = characters;
    }

    public AlternationCharSetExpression(RegularExpressionType expressionType,
                                        RegularExpressionConverter regularExpressionConverter,
                                        List<Character> characters)
    {
        super(expressionType, regularExpressionConverter);
        this.characters = characters;
    }

    @Override
    public HashSet<Character> getUnCompressibleCharSet()
    {
        return new HashSet<>();
    }

    @Override
    public List<HashSet> getCompressibleCharSets()
    {
        return Collections.singletonList(new HashSet<>(characters));
    }

    @Override
    public NFAModel accept()
    {
        return regularExpressionConverter.convertAlternationCharSet(this);
    }

    @Override
    public String toString()
    {
        if (getCharacters().isEmpty())
        {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Character character : characters)
        {
            stringBuilder.append(character);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
