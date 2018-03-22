package com.vero.compiler.syntax.core;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


public class Main
{

    public static void main(String[] args)
    {
        try
        {
            Files.readLines(new File(
                "C:\\Users\\31370\\IdeaProjects\\vero-compiler\\syntax\\src\\main\\resources\\simple1.txt"),
                Charsets.UTF_8).stream().filter(line -> line.length() > 1).forEach(
                    GrammarProductionManager::addProduction);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            Files.readLines(new File(
                "C:\\Users\\31370\\IdeaProjects\\vero-compiler\\syntax\\src\\main\\resources\\input.txt"),
                Charsets.UTF_8).stream().filter(line -> line.length() > 1).forEach(Input::addLine);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        println(Input.result());

        initProductionSet();
        printProductionInfo();
        extendGrammar();
        printItems();
        printActionTable();
        println("");
        printGotoTable();
        Parsering.initStacks();
        Parsering.parsering();
    }

    private static void printGotoTable()
    {
        println("GOTO:");
        int i = 0;
        for (HashMap<String, Integer> gt : ParserTable.GOTO_TABLE)
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

    public static void extendGrammar()
    {
        ArrayList<String> right = new ArrayList<>();
        right.add("E");
        ArrayList<String> lookAhead = new ArrayList<>();
        lookAhead.add("$");
        ProgramItem item = new ProgramItem("E'", right, -1, lookAhead, -1);
        Utils.ACC_LEFT = item.left;
        Utils.ACC_RIGHT = item.right.get(0);
        ParserTable.initGrammar(item);
        ParserTable.createAT();
        ParserTable.createGT();
    }

    public static void printActionTable()
    {
        println("ACTION:");
        int i = 0;
        for (HashMap<String, ArrayList<ActionItem>> actions : ParserTable.ACTION_TABLE)
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

    public static void printProductionInfo()
    {
        print("非终结符:\n");
        GrammarProductionManager.noTerminalSet.forEach(System.out::println);
        print("\n终结符:\n");
        GrammarProductionManager.TERMINAL_SET.forEach(System.out::println);
        print("\nMap:\n");
        Set<String> keys = GrammarProductionManager.PRODUCTION_MAP.keySet();
        for (String key : keys)
        {
            print(key + ": ");
            println(GrammarProductionManager.PRODUCTION_MAP.get(key));
        }
        println("");
        println("");
    }

    public static void initProductionSet()
    {
        GrammarProductionManager.noTerminal();
        GrammarProductionManager.terminal();
        GrammarProductionManager.directory();
    }

    public static void printItems()
    {
        int i = 0;
        for (ProgramItemSet itemSet : ProgramMonitor.setFamily)
        {
            print("I" + i + ":\n");
            printItemSet(itemSet);
            println(itemSet.status);
            println("");
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