package com.vero.compiler.syntax.core;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.vero.compiler.syntax.production.GrammarProductionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.reader.SyntaxContentDefiner;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 23:13 2018/3/22.
 * @since vero-compiler
 */

public class GrammarProductionManagerTest
{

    private File lexerDefinition;

    private File sourceFile;

    private Map<String, List<List<String>>> productionCutMap = new HashMap<>();

    private List<String> rowProductions = new ArrayList<>();

    @Before
    public void setUp()
        throws Exception
    {
        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
            "regular_grammar3.txt");
        URL f2 = Thread.currentThread().getContextClassLoader().getResource(
            "syntax_grammar_test2.txt");
        this.lexerDefinition = new File(Objects.requireNonNull(f1).getFile());
        this.sourceFile = new File(Objects.requireNonNull(f2).getFile());
        RegularGrammarFileParser parser = new RegularGrammarFileParser();
        try
        {
            RegularExpression[] tokenExpressions = parser.parse(lexerDefinition);

            LexiconContent lexerLexiconContent = new TokenLexiconContent(tokenExpressions);

            SyntaxContentDefiner lexiconBase = new SyntaxContentDefiner(
                lexerLexiconContent.getLexicon());

            LexiconContent syntaxLexiconContent = lexiconBase.getSyntaxLexcionContent();

            Assert.assertNotNull(syntaxLexiconContent);

            SyntaxGrammarParser grammarParser = new SyntaxGrammarParser(syntaxLexiconContent,
                sourceFile);

            this.rowProductions = grammarParser.parseProductionRows();

            this.productionCutMap = grammarParser.parseProduction();

            Assert.assertNotNull(productionCutMap);

            Assert.assertNotEquals(0, this.productionCutMap.size());

            Assert.assertEquals("<statement_list> ::= <statement>|<statement_list>;<statement>",
                rowProductions.get(0));

            Assert.assertEquals("<condition>::=odd<expression>|<expression><relation><expression>",
                rowProductions.get(1));
            Assert.assertEquals(
                "<expression>::=<term>|<adding_operator><term>|<expression><adding_operator><term>",
                rowProductions.get(2));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Test
    public void getNoTerminalSet()
    {
        GrammarProductionManager productionManager = new GrammarProductionManager(
                this.rowProductions, this.productionCutMap);
        Set<String> noTerminalSet = productionManager.getNoTerminalSet();
        Assert.assertTrue(noTerminalSet.contains("<statement_list>"));
        Assert.assertTrue(noTerminalSet.contains("<condition>"));
        Assert.assertTrue(noTerminalSet.contains("<expression>"));
        Assert.assertTrue(noTerminalSet.contains("<adding_operator>"));
        Assert.assertTrue(noTerminalSet.contains("<term>"));
    }

    @Test
    public void getTerminalSet()
    {
        GrammarProductionManager productionManager = new GrammarProductionManager(
                this.rowProductions, this.productionCutMap);
        Set<String> terminalSet = productionManager.getTerminalSet();
        Assert.assertTrue(terminalSet.contains("odd"));
        Assert.assertTrue(terminalSet.contains("+"));
        Assert.assertTrue(terminalSet.contains("-"));
        Assert.assertTrue(terminalSet.contains(";"));
        Assert.assertTrue(terminalSet.contains("*"));
        Assert.assertTrue(terminalSet.contains("/"));
    }

}