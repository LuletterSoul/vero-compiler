package com.vero.compiler.parser;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:05 2018/3/18.
 * @since vero-compiler
 */

public class RegularGrammarFileParserTest
{

    @Test
    public void parse()
    {
        File testGrammarFile = new File(
            "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar2.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try
        {
            parser.parse();
            parser.getGrammarProductionDefinitions().forEach(System.out::println);
            parser.parseDefinitions();
            Map<String, RegularGrammarProduction> grammarProductionMap = parser.getGrammarProductionMap();
            RegularGrammarProduction production = grammarProductionMap.get("<symbol>");
            Assert.assertEquals("<symbol>", production.getLeftPart());
            List<List<Object>> rightPart = production.getRightPart();
            List<Object> first = rightPart.get(0);
            Assert.assertEquals("<alphanumeric>", first.get(0));
            List<Object> second = rightPart.get(1);
            Assert.assertEquals("<alphanumeric>", second.get(0));
            Assert.assertEquals("<num>", second.get(1));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void group() {
        File testGrammarFile = new File(
                "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar2.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try
        {
            parser.parse();
            parser.getGrammarProductionDefinitions().forEach(System.out::println);
            parser.parseDefinitions();
            List<RegularGrammarProduction> noContainNoTerminalSymbols = parser.groupNoContainsNoTerminalSymbolProductions();
            noContainNoTerminalSymbols.forEach( nt->{
                Assert.assertNotEquals(nt.getLeftPart(), "symbol");
                Assert.assertNotEquals(nt.getLeftPart(),"n1");
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void transfer() {
        File testGrammarFile = new File(
                "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar2.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try
        {
            parser.parse();
            parser.getGrammarProductionDefinitions().forEach(System.out::println);
            parser.parseDefinitions();
            parser.transfer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}