package com.vero.compiler.syntax.core;


import java.util.ArrayList;


/**
 * Created by XiangDe Liu on 2018/3/10.
 */
public class Utils
{
    public static String ACC_LEFT;

    public static String ACC_RIGHT;

    public static boolean isTerminal(String str)
    {
        if (GrammarProductionManager.noTerminalSet.contains(str))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static ArrayList<ArrayList<String>> getRights(String symbol)
    {
        return GrammarProductionManager.PRODUCTION_MAP.get(symbol);
    }

    public static boolean hasEmpty(ArrayList<ArrayList<String>> rights)
    {
        for (ArrayList<String> symbols : rights)
        {
            if (symbols.get(0).equals("ε"))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isRecursion(ArrayList<String> rights, String left)
    {
        return rights.get(0).equals(left);
    }
}
