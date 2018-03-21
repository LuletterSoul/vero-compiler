package com.vero.compiler.lexer.info;


import static com.vero.compiler.lexer.expression.RegularExpression.*;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.lexer.compress.CompressedTransitionTable;
import com.vero.compiler.lexer.converter.NFAConverter;
import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.expression.RegularExpressionType;
import com.vero.compiler.lexer.generator.DFAModel;
import com.vero.compiler.lexer.source.FileSourceReader;
import com.vero.compiler.lexer.source.SourceReader;
import com.vero.compiler.lexer.token.Token;
import com.vero.compiler.lexer.token.TokenType;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.FiniteAutomationMachineEngine;
import com.vero.compiler.scan.core.Scanner;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 17:16 2018/3/18.
 * @since vero-compiler
 */

public class ScannerTest
{

    @Test
    public void read()
    {
        Lexicon lexicon = new Lexicon();
        Lexer global = lexicon.getDefaultLexer();
        Lexer keywords = global.createSubLexer();
        Lexer xml = keywords.createSubLexer();

        Token ID = global.defineToken(
            Range('a', 'z').Concat((Range('a', 'z').Union(Range('0', '9'))).Many()));
        Token NUM = global.defineToken(Range('0', '9').Many1());
        Token WHITESPACE = global.defineToken(Symbol(' ').Many());
        Token ERROR = global.defineToken(Range(Character.MIN_VALUE, (char)255));

        Token IF = keywords.defineToken(Literal("if"));
        Token ELSE = keywords.defineToken(Literal("else"));

        Token XMLNS = xml.defineToken(Literal("xmlns"));

        File scannedFile = new File(
            "F:\\GitHup\\vero-compiler\\scan\\src\\test\\resource\\scannedTest1.txt");

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());

        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        LexerTransitionInfo info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        SourceReader sourceReader = new FileSourceReader(scannedFile);
        Scanner scanner = new Scanner(info, sourceReader);

        Lexeme lexeme1 = scanner.read();
        Assert.assertEquals(ID.getIndex(), lexeme1.getTokenIndex());
        Assert.assertEquals("asdf04a", lexeme1.getContent());
        Assert.assertEquals(new Integer(0),
            lexeme1.getSourceSpan().getStartLocation().getColumn());
        Assert.assertEquals(new Integer(6), lexeme1.getSourceSpan().getEndLocation().getColumn());

        Lexeme lexeme2 = scanner.read();
        Assert.assertEquals(WHITESPACE.getIndex(), lexeme2.getTokenIndex());
        Assert.assertEquals(" ", lexeme2.getContent());

        Lexeme lexeme3 = scanner.read();
        Assert.assertEquals(NUM.getIndex(), lexeme3.getTokenIndex());
        Assert.assertEquals("1107", lexeme3.getContent());

        Lexeme eof = scanner.read();
        Assert.assertEquals(info.getEndOfStreamTokenIndex(), eof.getTokenIndex());
        Assert.assertEquals(eof.getSourceSpan().getStartLocation().getCharIndex(),
            eof.getSourceSpan().getEndLocation().getCharIndex());
        Assert.assertEquals(new Integer(11),
            eof.getSourceSpan().getStartLocation().getCharIndex());

        Lexeme eof2 = scanner.read();
        Assert.assertEquals(info.getEndOfStreamTokenIndex(), eof2.getTokenIndex());
        Assert.assertEquals(eof2.getSourceSpan().getStartLocation().getCharIndex(),
            eof2.getSourceSpan().getEndLocation().getCharIndex());

    }

    @Test
    public void transfer()
    {
        transferDefinition2Token();
    }

    @Test
    public void rexToDFA()
    {

        RegularExpression[] tokenExpressions = transferDefinition2Token();

        Lexicon lexicon = new Lexicon();

        Lexer global = lexicon.getDefaultLexer();

        Lexer keywords = global.createSubLexer();

        Lexer unsigned_number = global.createSubLexer();

        Lexer complex_number = global.createSubLexer();

        Token KEY_WORDS = keywords.defineToken(
            tokenExpressions[TokenType.KEY_WORDS.getPriority()]);

        Token VAR_NAME = global.defineToken(tokenExpressions[TokenType.VAR.getPriority()]);

        RegularExpression ex = tokenExpressions[TokenType.UNSIGNED_NUMBER.getPriority()];

        Token UNSIGNED_NUMBER = global.defineToken(ex);

        Token COMPLEX = global.defineToken(ex.Concat(Symbol('+')).Concat(ex).Concat(Symbol('i')));

        Token OPERATOR = global.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        FiniteAutomationMachineEngine engine = new FiniteAutomationMachineEngine(
            tc.getCharClassTable(), tc.getRealCompressedTransitionTable());
        LexerTransitionInfo info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(),
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
        info.setLexerState(global.getIndex());
        engine.inputString("if");
        Assert.assertEquals(VAR_NAME.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("a123saaas123gfgfdhaaaaa123aa");
        Assert.assertEquals(VAR_NAME.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("a1111112465788");
        Assert.assertEquals(VAR_NAME.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("ggwesfrsdgsdg");
        Assert.assertEquals(VAR_NAME.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("刘gb祥德g");
        Assert.assertEquals(VAR_NAME.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("刘gb祥德脑壳疼g");
        Assert.assertTrue(engine.isAtStoppedState());

        info.setLexerState(keywords.getIndex());
        engine.resetMachineState();
        engine.resetAndInputString("Float");
        Assert.assertEquals(KEY_WORDS.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("Integer");
        Assert.assertEquals(KEY_WORDS.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.resetAndInputString("const");
        Assert.assertEquals(KEY_WORDS.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        info.setLexerState(global.getIndex());
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

        // 切换至无符号数上下文
        info.setLexerState(unsigned_number.getIndex());
        engine.resetAndInputString("123456721e+55");
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetAndInputString("25.55e+5");
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetAndInputString("25.55e-15");
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetAndInputString("12.59");
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetAndInputString("6e2");
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(),
            info.getTokenIndex(engine.getCurrentStateIndex()));

        // 复数
        info.setLexerState(global.getIndex());
        engine.resetAndInputString("1111e-123+123i");
        Assert.assertEquals(COMPLEX.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

    }

    private RegularExpression[] transferDefinition2Token()
    {
        File testGrammarFile = new File(
            "F:\\GitHup\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar3.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser();
        try
        {
            RegularExpression[] tokenExpressions = parser.parse(testGrammarFile);
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

    @Test
    public void defineFromFile()
    {
        RegularExpression[] tokenExpressions = transferDefinition2Token();;
        Lexicon lexicon = new Lexicon();
        Lexer global = lexicon.getDefaultLexer();
        Lexer keywords = global.createSubLexer();
        Lexer unsigned_number = global.createSubLexer();
        Lexer complex_number = global.createSubLexer();

        // 关键字
        Token KEY_WORDS = global.defineToken(tokenExpressions[TokenType.KEY_WORDS.getPriority()]);

        // 变量名
        Token VAR_NAME = global.defineToken(tokenExpressions[TokenType.VAR.getPriority()]);

        // 空格
        Token WHITESPACE = global.defineToken(Symbol(' ').Many());

        RegularExpression ex = tokenExpressions[TokenType.UNSIGNED_NUMBER.getPriority()];

        // 无符号实数
        Token UNSIGNED_NUMBER = global.defineToken(ex);

        // 虚数
        Token COMPLEX = global.defineToken(ex.Concat(Symbol('+')).Concat(ex).Concat(Symbol('i')));

        // 运算符
        Token OPERATOR = global.defineToken(tokenExpressions[TokenType.OPERATOR.getPriority()]);

        Token DELIMITER = global.defineToken(tokenExpressions[TokenType.DELIMITER.getPriority()]);

        File scannedFile = new File(
            "F:\\GitHup\\vero-compiler\\scan\\src\\test\\resource\\scannedTest4.txt");

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());

        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        LexerTransitionInfo info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        SourceReader sourceReader = new FileSourceReader(scannedFile);
        Scanner scanner = new Scanner(info, sourceReader);

        Lexeme lexeme1 = scanner.read();
        Assert.assertEquals(KEY_WORDS.getIndex(), lexeme1.getTokenIndex());
        Assert.assertEquals("Float", lexeme1.getContent());
        Assert.assertEquals(new Integer(0),
            lexeme1.getSourceSpan().getStartLocation().getColumn());
        Assert.assertEquals(new Integer(4), lexeme1.getSourceSpan().getEndLocation().getColumn());

        Lexeme lexeme2 = scanner.read();
        Assert.assertEquals(WHITESPACE.getIndex(), lexeme2.getTokenIndex());
        Assert.assertEquals(" ", lexeme2.getContent());

        Lexeme lexeme3 = scanner.read();
        Assert.assertEquals(VAR_NAME.getIndex(), lexeme3.getTokenIndex());
        Assert.assertEquals("myFloat", lexeme3.getContent());

        Lexeme lexeme4 = scanner.read();
        Assert.assertEquals(WHITESPACE.getIndex(), lexeme4.getTokenIndex());
        Assert.assertEquals(" ", lexeme4.getContent());

        Lexeme lexeme5 = scanner.read();
        Assert.assertEquals(OPERATOR.getIndex(), lexeme5.getTokenIndex());
        Assert.assertEquals("=", lexeme5.getContent());

        Lexeme lexeme6 = scanner.read();
        Assert.assertEquals(WHITESPACE.getIndex(), lexeme6.getTokenIndex());
        Assert.assertEquals(" ", lexeme6.getContent());

        Lexeme lexeme7 = scanner.read();
        Assert.assertEquals(UNSIGNED_NUMBER.getIndex(), lexeme7.getTokenIndex());
        Assert.assertEquals("12.23", lexeme7.getContent());

        Lexeme lexeme8 = scanner.read();
        Assert.assertEquals(DELIMITER.getIndex(), lexeme8.getTokenIndex());
        Assert.assertEquals(";", lexeme8.getContent());

        Lexeme eof = scanner.read();
        Assert.assertEquals(info.getEndOfStreamTokenIndex(), eof.getTokenIndex());
        Assert.assertEquals(eof.getSourceSpan().getStartLocation().getCharIndex(),
            eof.getSourceSpan().getEndLocation().getCharIndex());

    }

}