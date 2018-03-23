package com.vero.compiler.syntax.core;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:25 2018/3/23.
 * @since vero-compiler
 */

public class PrintUtils
{

    public static void printItems(List<ProgramItemSet> family)
    {
        int i = 0;
        for (ProgramItemSet itemSet : family)
        {
            System.out.print("I" + i + ":\n");
            printItemSet(itemSet);
            println(itemSet.status);
            println("");
            println("");
            i++ ;
        }
    }

    public static void printActionTable(List<HashMap<String, List<ActionItem>>> ACTION_TABLE)
    {
        println("ACTION:");
        int i = 0;
        for (Map<String, List<ActionItem>> actions : ACTION_TABLE)
        {
            print("S" + i);
            for (String key : actions.keySet())
            {
                print("  " + key + "->");
                int k = 0;
                for (ActionItem actionItem : actions.get(key))
                {
                    if (k != 0) print("/");
                    print(actionItem.toString());
                    k++ ;
                }
            }
            println("");
            i++ ;
        }
    }

    public static void printGotoTable(List<HashMap<String, Integer>> GOTO_TABLE)
    {
        println("GOTO:");
        int i = 0;
        for (HashMap<String, Integer> gt : GOTO_TABLE)
        {
            print("S" + i);
            for (String key : gt.keySet())
            {
                print("  " + key + "->" + gt.get(key) + " ");
            }
            println("");
            i++ ;
        }
    }

    public static void printItemSet(ProgramItemSet itemSet)
    {
        for (ProgramItem item : itemSet.container)
        {
            print("dot=" + item.dot + " ");
            print(item.left + " -> " + item.right + " ");
            println(item.lookAhead);
        }
    }

    public static void print(Object obj)
    {
        System.out.print(obj);
    }

    public static void println(Object obj)
    {
        System.out.println(obj);
    }
}
