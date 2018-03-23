package com.vero.compiler.syntax.core;


import java.util.List;

import com.vero.compiler.syntax.production.GrammarProductionManager;

import lombok.Data;


/**
 * Created by XiangDe Liu on 2018/3/10.
 */
@Data
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
        return this.productionManager.getProductCutMap().get(symbol);
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
