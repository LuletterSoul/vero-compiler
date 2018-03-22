package com.vero.compiler.syntax.core;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


public class Main
{


    public static void main(String[] args)
    {
         GrammarProductionManager productionManager;
        try
        {
            URL f1 = Thread.currentThread().getContextClassLoader().getResource("prod.txt");

            Files.readLines(new File(Objects.requireNonNull(f1).getFile()),
                Charsets.UTF_8).stream().filter(line -> line.length() > 1).forEach(
                    productionManager::addProduction);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            URL f2 = Thread.currentThread().getContextClassLoader().getResource("input.txt");
            Files.readLines(new File(Objects.requireNonNull(f2).getFile()),
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
        for (HashMap<String, Integer> gt : SyntaxAnalysisTableGenerator.GOTO_TABLE)
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
        SymbolMaintainer.ACC_LEFT = item.left;
        SymbolMaintainer.ACC_RIGHT = item.right.get(0);
        SyntaxAnalysisTableGenerator.initGrammar(item);
        SyntaxAnalysisTableGenerator.createAT();
        SyntaxAnalysisTableGenerator.createGT();
    }

    public static void printActionTable()
    {
        println("ACTION:");
        int i = 0;
        for (HashMap<String, ArrayList<ActionItem>> actions : SyntaxAnalysisTableGenerator.ACTION_TABLE)
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
        productionManager.noTerminalSet.forEach(System.out::println);
        print("\n终结符:\n");
        productionManager.terminalSet.forEach(System.out::println);
        print("\nMap:\n");
        Set<String> keys = productionManager.productCutMap.keySet();
        for (String key : keys)
        {
            print(key + ": ");
            println(productionManager.productCutMap.get(key));
        }
        println("");
        println("");
    }

    public static void initProductionSet()
    {
        productionManager.computeNoTerminals();
        productionManager.computeTerminals();
        productionManager.directory();
    }

    public static void printItems()
    {
        int i = 0;
        for (ProgramItemSet itemSet : ProgramMonitor.family)
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