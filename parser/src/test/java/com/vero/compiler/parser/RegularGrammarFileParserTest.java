package com.vero.compiler.parser;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.expression.RegularExpressionType;
import com.vero.compiler.scan.token.TokenType;


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
            List<List<String>> rightPart = production.getRightPart();
            List<String> first = rightPart.get(0);
            Assert.assertEquals("<alphanumeric>", first.get(0));
            List<String> second = rightPart.get(1);
            Assert.assertEquals("<alphanumeric>", second.get(0));
            Assert.assertEquals("<num>", second.get(1));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void group()
    {
        File testGrammarFile = new File(
            "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar2.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try
        {
            parser.parse();
            parser.getGrammarProductionDefinitions().forEach(System.out::println);
            parser.parseDefinitions();
            List<RegularGrammarProduction> noContainNoTerminalSymbols = parser.group().getNoContainTerminalSymbolProductions();
            noContainNoTerminalSymbols.forEach(nt -> {
                Assert.assertNotEquals(nt.getLeftPart(), "symbol");
                Assert.assertNotEquals(nt.getLeftPart(), "n1");
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void transfer()
    {
        File testGrammarFile = new File(
            "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar3.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try
        {
            parser.parse();
            parser.getGrammarProductionDefinitions().forEach(System.out::println);
            parser.parseDefinitions();
            parser.transfer();
            RegularExpression[] tokenExpressions = parser.getTokenExpressions();
            Assert.assertNotEquals(tokenExpressions[TokenType.DELIMITER.getPriority()],
                RegularExpressionType.Alternation);
            Assert.assertNotEquals(tokenExpressions[TokenType.ALPHABET.getPriority()],
                RegularExpressionType.Alternation);
            Assert.assertNotEquals(tokenExpressions[TokenType.NUM.getPriority()],
                RegularExpressionType.Alternation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}