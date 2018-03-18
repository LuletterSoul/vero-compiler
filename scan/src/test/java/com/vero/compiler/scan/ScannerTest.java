package com.vero.compiler.scan;


import static com.vero.compiler.scan.expression.RegularExpression.*;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.scan.compress.CompressedTransitionTable;
import com.vero.compiler.scan.converter.NFAConverter;
import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.lexer.Lexeme;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;
import com.vero.compiler.scan.source.FileSourceReader;
import com.vero.compiler.scan.source.SourceReader;
import com.vero.compiler.scan.token.Token;


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
            "C:\\Users\\31370\\IdeaProjects\\vero-compiler\\scan\\src\\test\\resource\\scannedTest1.txt");

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());

        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        ScannerInfo info = new ScannerInfo(tc.getRealCompressedTransitionTable(),
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
}