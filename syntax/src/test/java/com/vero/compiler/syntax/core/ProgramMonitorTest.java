package com.vero.compiler.syntax.core;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.production.GrammarProductionManager;
import com.vero.compiler.syntax.reader.SyntaxContentDefiner;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 10:53 2018/3/23.
 * @since vero-compiler
 */

public class ProgramMonitorTest
{

    private File lexerDefinition;

    private File sourceFile;

    private SyntaxGrammarParser parser;

    private LexiconContent lexerLexiconContent;

    private Map<String, List<List<String>>> productionCutMap = new HashMap<>();

    private List<String> rowProductions = new ArrayList<>();

    @Before
    public void setUp()
        throws Exception
    {
        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
            "regular_grammar3.txt");
        URL f2 = Thread.currentThread().getContextClassLoader().getResource(
            "syntax_grammar_test5.txt");
        this.lexerDefinition = new File(Objects.requireNonNull(f1).getFile());
        this.sourceFile = new File(Objects.requireNonNull(f2).getFile());
        RegularGrammarFileParser parser = new RegularGrammarFileParser();
        try
        {
            RegularExpression[] tokenExpressions = parser.parse(lexerDefinition);

            this.lexerLexiconContent = new TokenLexiconContent(tokenExpressions);

            SyntaxContentDefiner lexiconBase = new SyntaxContentDefiner(
                lexerLexiconContent.getLexicon());

            LexiconContent syntaxLexiconContent = lexiconBase.getSyntaxLexcionContent();

            Assert.assertNotNull(syntaxLexiconContent);

            SyntaxGrammarParser grammarParser = new SyntaxGrammarParser(syntaxLexiconContent,
                sourceFile);

            this.parser = grammarParser;

            this.rowProductions = grammarParser.parseProductionRows();

            this.productionCutMap = grammarParser.parseProduction();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void monitor()
    {
        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
                "lexer_syntax_test1.txt");
        ProgramMonitor monitor = new ProgramMonitor(
                new GrammarProductionManager(this.rowProductions, this.productionCutMap));
        LexemeCollector lexemeCollector =  this.lexerLexiconContent.buildCollector();
        lexemeCollector.collect(new File(Objects.requireNonNull(f1).getFile()));

        List<Lexeme> lexemes = lexemeCollector.getLexemeStream();

        monitor.monitor(lexemes);
    }
}