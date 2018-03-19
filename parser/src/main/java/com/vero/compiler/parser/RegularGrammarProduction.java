package com.vero.compiler.parser;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:16 2018/3/18.
 * @since vero-compiler
 */

@Setter
@Getter
public class RegularGrammarProduction
{
    private String leftPart;

    private List<List<String>> rightPart;

    public RegularGrammarProduction()
    {
        rightPart = new ArrayList<>();
    }
}
