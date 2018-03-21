package com.vero.compiler.parser;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Set<String> noTerminalSymbols;

    /**
     * 不含右递归的右部
     */
    private List<List<String>> noRecursiveRightPart;

    private Map<String, RegularGrammarProduction> globalRegularGrammarProductions;

    public RegularGrammarProduction(Set<String> noTerminalSymbols)
    {
        this.noTerminalSymbols = noTerminalSymbols;
        this.rightPart = new ArrayList<>();
    }

    public RegularGrammarProduction(Set<String> noTerminalSymbols, String leftPart,
                                    List<List<String>> rightPart)
    {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.noTerminalSymbols = noTerminalSymbols;
        this.noRecursiveRightPart = new ArrayList<>();
    }

    public boolean isRecursiveRightPart()
    {
        boolean[] marked = new boolean[this.rightPart.size()];
        for (int h = 0; h < marked.length; h++ )
        {
            marked[h] = false;
        }
        for (int i = 0; i < this.rightPart.size(); i++ )
        {
            List<String> part = this.rightPart.get(i);
            for (int j = 0; j < part.size(); j++ )
            {
                RegularGrammarProduction production = globalRegularGrammarProductions.get(part.get(j));
                if (production != null)
                {
                    marked[i] = production.getLeftPart().equals(this.getLeftPart())
                                || production.isRecursiveRightPart();
                }
                else
                {
                    marked[i] = false;
                }
            }
        }
        boolean isRecursive = false;
        for (int i = 0; i < marked.length; i++) {
            isRecursive = marked[i];
        }
        return isRecursive;
    }

}
