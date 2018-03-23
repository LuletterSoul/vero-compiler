package com.vero.compiler.syntax.core;


import java.util.List;
import java.util.Objects;

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
        if (Objects.isNull(rights) || rights.isEmpty())
        {
            String message = String.format(
                "Grammar production left part :[%s] must own the right parts."
                                           + "Validate the production definition if  accord with the regulation or not .",
                symbol);
            throw new SyntaxGrammarException(message);
        }
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
}
