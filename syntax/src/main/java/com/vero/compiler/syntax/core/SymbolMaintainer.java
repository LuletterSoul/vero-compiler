package com.vero.compiler.syntax.core;


import com.vero.compiler.syntax.production.GrammarProductionManager;

import java.util.List;


/**
 * Created by XiangDe Liu on 2018/3/10.
 */
public class SymbolMaintainer
{
    public static String ACC_LEFT;

    public static String ACC_RIGHT;

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

    public static boolean hasEmpty(List<List<String>> rights)
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

    public static boolean isRecursion(List<String> rights, String left)
    {
        return rights.get(0).equals(left);
    }
}
