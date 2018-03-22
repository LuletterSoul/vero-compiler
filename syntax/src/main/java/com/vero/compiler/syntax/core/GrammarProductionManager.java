package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by XiangDe Liu on 2018/3/6.
 */
public class GrammarProductionManager
{
    public static ArrayList<String> rawProduction = new ArrayList<>();

    /**
     * 非终结符集合
     */
    public static ArrayList<String> noTerminalSet = new ArrayList<>();

    /**
     * 终结符集合
     */
    public static ArrayList<String> TERMINAL_SET = new ArrayList<>();


    /**
     * 切割后的文法产生式
     */
    public static HashMap<String, ArrayList<ArrayList<String>>> PRODUCTION_MAP = new HashMap<>();


    public static void noTerminal()
    {
        for (String item : rawProduction)
        {
            String left = item.split(" -> ")[0];
            // System.out.println(left);
            if (!noTerminalSet.contains(left))
            {
                noTerminalSet.add(left);
            }
        }
    }

    public static void terminal()
    {
        for (String item : rawProduction)
        {
            String right = item.split(" -> ")[1];

            for (String terminal : right.split(" "))
            {
                if (!TERMINAL_SET.contains(terminal) && !noTerminalSet.contains(terminal)
                    && !terminal.equals("ε"))
                {
                    TERMINAL_SET.add(terminal);
                }
            }
        }
    }

    public static void directory()
    {
        for (String item : rawProduction)
        {
            String[] temp = item.split(" -> ");

            ArrayList<String> right = new ArrayList<>();

            for (String i : temp[1].split(" "))
            {
                right.add(i);
            }

            if (PRODUCTION_MAP.containsKey(temp[0]))
            {
                ArrayList<ArrayList<String>> set = PRODUCTION_MAP.get(temp[0]);
                set.add(right);
                PRODUCTION_MAP.put(temp[0], set);
            }
            else
            {
                ArrayList<ArrayList<String>> set = new ArrayList<>();
                set.add(right);
                PRODUCTION_MAP.put(temp[0], set);
            }
        }
    }

    public static void productions()
    {

    }

    public static void addProduction(String production)
    {
        rawProduction.add(production);
    }

    public static ArrayList<ArrayList<String>> getRights(String key)
    {
        ArrayList<ArrayList<String>> rights = new ArrayList<>();
        rights.addAll(PRODUCTION_MAP.get(key));
        return rights;
    }
}
