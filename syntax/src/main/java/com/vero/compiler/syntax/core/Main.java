//package com.vero.compiler.syntax.core;
//
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Objects;
//import java.util.Set;
//
//import com.google.common.base.Charsets;
//import com.google.common.io.Files;
//import com.vero.compiler.syntax.production.GrammarProductionManager;
//
//
//public class Main
//{
//
//
//    public static void main(String[] args)
//    {
//         GrammarProductionManager productionManager;
//        try
//        {
//            URL f1 = Thread.currentThread().getContextClassLoader().getResource("prod.txt");
//
//            Files.readLines(new File(Objects.requireNonNull(f1).getFile()),
//                Charsets.UTF_8).stream().filter(line -> line.length() > 1).forEach(
//                    productionManager::addProduction);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        try
//        {
//            URL f2 = Thread.currentThread().getContextClassLoader().getResource("input.txt");
//            Files.readLines(new File(Objects.requireNonNull(f2).getFile()),
//                Charsets.UTF_8).stream().filter(line -> line.length() > 1).forEach(Input::addLine);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        println(Input.result());
//        initProductionSet();
//        printProductionInfo();
//        extendGrammar();
//        printItems();
//        printActionTable();
//        println("");
//        printGotoTable();
//        SyntaxParser.initStacks();
//        SyntaxParser.parse();
//    }
//
//
//    public static void extendGrammar()
//    {
//        ArrayList<String> right = new ArrayList<>();
//        right.add("E");
//        ArrayList<String> lookAhead = new ArrayList<>();
//        lookAhead.add("$");
//        ProgramItem item = new ProgramItem("E'", right, -1, lookAhead, -1);
//        SymbolMaintainer.acceptLeft = item.left;
//        SymbolMaintainer.acceptRight = item.right.get(0);
//        SyntaxAnalysisTableGenerator.initGrammar(item);
//        SyntaxAnalysisTableGenerator.createActionTable();
//        SyntaxAnalysisTableGenerator.createGotoTable();
//    }
//
//
//
//    public static void printProductionInfo()
//    {
//        print("非终结符:\n");
//        productionManager.noTerminalSet.forEach(System.out::println);
//        print("\n终结符:\n");
//        productionManager.terminalSet.forEach(System.out::println);
//        print("\nMap:\n");
//        Set<String> keys = productionManager.productCutMap.keySet();
//        for (String key : keys)
//        {
//            print(key + ": ");
//            println(productionManager.productCutMap.get(key));
//        }
//        println("");
//        println("");
//    }
//
//    public static void initProductionSet()
//    {
//        productionManager.computeNoTerminals();
//        productionManager.computeTerminals();
//        productionManager.directory();
//    }
//
//
//}