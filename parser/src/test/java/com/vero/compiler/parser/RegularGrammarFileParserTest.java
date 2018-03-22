package com.vero.compiler.parser;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.expression.RegularExpressionType;
import com.vero.compiler.lexer.token.TokenType;


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
        URL f1=Thread.currentThread().getContextClassLoader().getResource("regular_grammar1.txt");
        File testGrammarFile = new File(Objects.requireNonNull(f1).getFile());
        RegularGrammarFileParser parser = new RegularGrammarFileParser();
        try
        {
            parser.parse(testGrammarFile);
            Map<String, RegularGrammarProduction> grammarProductionMap = parser.getGrammarProductionMap();
            RegularGrammarProduction production = grammarProductionMap.get("<var>");
            Assert.assertEquals("<var>", production.getLeftPart());
            List<List<String>> rightPart = production.getRightPart();
            List<String> first = rightPart.get(0);
            Assert.assertEquals("<alphabet>", first.get(0));
            List<String> second = rightPart.get(1);
            Assert.assertEquals("<alphabet>", second.get(0));
            Assert.assertEquals("<number>", second.get(1));
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
}