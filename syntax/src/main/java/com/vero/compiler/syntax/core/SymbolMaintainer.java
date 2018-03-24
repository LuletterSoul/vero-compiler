package com.vero.compiler.syntax.core;


import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.vero.compiler.exception.SyntaxGrammarException;
import com.vero.compiler.syntax.production.GrammarProductionManager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by XiangDe Liu on 2018/3/10.
 */
@Data
@Slf4j
public class SymbolMaintainer
{
    private String acceptLeft;

    private String acceptRight;

    private ProgramItem I0;

    private GrammarProductionManager productionManager;

    private Set<String> leftRecord = new HashSet<>();

    public SymbolMaintainer(GrammarProductionManager productionManager)
    {
        this.productionManager = productionManager;
    }

    public boolean isTerminal(String str)
    {
        if (this.productionManager.getNoTerminalSet().contains(str))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public List<List<String>> getRights(String symbol)
    {
        List<List<String>> rights = this.productionManager.getProductCutMap().get(symbol);
//        if (Objects.isNull(rights) || rights.isEmpty())
//        {
//            String message = String.format(
//                "Grammar production left part :[%s] must own the right parts."
//                                           + "Validate the production definition if  accord with the regulation or not .",
//                symbol);
//            throw new SyntaxGrammarException(message);
//        }
        return rights;
    }

    public boolean hasEmpty(List<List<String>> rights)
    {
        for (List<String> symbols : rights)
        {
            if (symbols.get(0).equals("ε"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isRecursion(List<String> rights, String left)
    {
        return rights.get(0).equals(left);
    }

    public boolean isRecursion(String left)
    {
        this.leftRecord.clear();
        return isRecursiveRightPart(left);
    }

    private boolean isRecursiveRightPart(String left)
    {
        List<List<String>> rightParts = this.productionManager.getRightParts(left);
        if (rightParts == null || rightParts.isEmpty())
        {
            return false;
        }
        boolean[] marked = new boolean[rightParts.size()];
        for (int h = 0; h < marked.length; h++ )
        {
            marked[h] = false;
        }
        for (int i = 0; i < rightParts.size(); i++ )
        {
            List<String> part = rightParts.get(i);
            if (part.isEmpty()) {
                return false;
            }
            this.leftRecord.add(left);
            String subLeft = part.get(0);
            List<List<String>> subRightParts = this.productionManager.getRightParts(subLeft);
            if (subRightParts != null)
            {
                marked[i] = this.leftRecord.contains(subLeft) || isRecursiveRightPart(subLeft);
            }
            else
            {
                marked[i] = false;
            }
        }
        int counter = 0;
        for (int i = 0; i < marked.length; i++ )
        {
            if (!marked[i])
            {
                ++counter;
            }
        }
        return counter != marked.length;
    }
}
