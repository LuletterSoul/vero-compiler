package com.vero.compiler.parser;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.experimental.Tolerate;
import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.scan.FiniteAutomationMachineEngine;
import com.vero.compiler.scan.ScannerInfo;
import com.vero.compiler.scan.compress.CompressedTransitionTable;
import com.vero.compiler.scan.converter.NFAConverter;
import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.expression.RegularExpressionType;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;
import com.vero.compiler.scan.token.Token;
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
        transferDefinition2Token();
    }

    @Test
    public void rexToDFA()
    {

        RegularExpression[] tokenExpressions = transferDefinition2Token();;
        Lexicon lexicon = new Lexicon();
        Lexer global = lexicon.getDefaultLexer();
        Lexer keywords = global.createSubLexer();
        Lexer numberAlphabet = global.createSubLexer();
        Lexer unsigned_integer = global.createSubLexer();
        Lexer xml = keywords.createSubLexer();

        Token KEY_WORDS = keywords.defineToken(tokenExpressions[TokenType.KEY_WORDS.getPriority()]);

//        Token ALPHABET = numberAlphabet.defineToken(tokenExpressions[TokenType.ALPHABET.getPriority()]);
//
//        Token NUMBER = numberAlphabet.defineToken(tokenExpressions[TokenType.NUMBER.getPriority()]);
//
//        Token NUM_ALPHABET = numberAlphabet.defineToken(tokenExpressions[TokenType.NUMBER_ALPHABET.getPriority()]);


        Lexer ids = numberAlphabet.createSubLexer();

        Token VAR = ids.defineToken(tokenExpressions[TokenType.VAR.getPriority()]);

        Token UNSIGNED_INTEGER = global.defineToken(
                tokenExpressions[TokenType.UNSIGNED_INTEGER.getPriority()]);



        Token OPERATOR = global.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        FiniteAutomationMachineEngine engine = new FiniteAutomationMachineEngine(
            tc.getCharClassTable(), tc.getRealCompressedTransitionTable());
        ScannerInfo info = new ScannerInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        // Token IF = keywords.defineToken(Literal("if"));
        //
        // Token ERROR = global.defineToken(Range(Character.MIN_VALUE, (char)255));
        //
        // Token ELSE = keywords.defineToken(Literal("else"));
        //
        // Token XMLNS = xml.defineToken(Literal("xmlns"));

        // 默认上下文Lexer,环境为global
        // if应该被识别为标识符
        info.setLexerState(ids.getIndex());
        engine.inputString("if");
        Assert.assertEquals(VAR.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("asa245asas24asaasasasa");
        Assert.assertEquals(VAR.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));


        engine.resetMachineState();
        engine.inputString("a1q23abrefw");
        Assert.assertEquals(VAR.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));


        engine.resetMachineState();
        engine.inputString("31234dewdewdew");
        Assert.assertTrue(engine.isAtStoppedState());

        engine.resetMachineState();
        engine.inputString("gbg");
        Assert.assertEquals(VAR.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));


        info.setLexerState(keywords.getIndex());
        engine.resetMachineState();
        engine.resetAndInputString("Float");
        Assert.assertEquals(KEY_WORDS.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("Integer");
        Assert.assertEquals(KEY_WORDS.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("const");
        Assert.assertEquals(KEY_WORDS.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("+");
        Assert.assertEquals(OPERATOR.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("-");
        Assert.assertEquals(OPERATOR.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("*");
        Assert.assertEquals(OPERATOR.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("/");
        Assert.assertEquals(OPERATOR.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));


        engine.resetMachineState();
        info.setLexerState(global.getIndex());
        engine.inputString("1234567");
        Assert.assertEquals(UNSIGNED_INTEGER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

    }

    private RegularExpression[] transferDefinition2Token()
    {
        File testGrammarFile = new File(
            "C:\\Users\\31370\\IdeaProjects\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar3.txt");
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
            Assert.assertNotEquals(tokenExpressions[TokenType.NUMBER.getPriority()],
                RegularExpressionType.Alternation);
            return tokenExpressions;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}