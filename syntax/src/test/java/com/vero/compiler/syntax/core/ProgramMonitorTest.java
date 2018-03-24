package com.vero.compiler.syntax.core;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexemeCollector;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.production.GrammarProductionManager;
import com.vero.compiler.syntax.reader.SyntaxContentDefiner;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 10:53 2018/3/23.
 * @since vero-compiler
 */

@Slf4j
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

    }

    @Test
    public void testMonitor_C_Grammar1()
    {

        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
            "regular_grammar3.txt");
        URL f2 = Thread.currentThread().getContextClassLoader().getResource("c_grammar_test1.txt");
        buildGrammar(f1, f2);
        URL f3 = Thread.currentThread().getContextClassLoader().getResource("input_1.txt");

        AnalysisProcessor processor = new AnalysisProcessorImpl();
        GrammarProductionManager manager = new GrammarProductionManager(this.rowProductions,
            this.productionCutMap);
        ProgramMonitor monitor = new ProgramMonitor(manager, processor);
        LexemeCollector collector = this.lexerLexiconContent.buildCollector();
        TokenLexemeCollector lexemeCollector = null;
        if (collector instanceof TokenLexemeCollector)
        {
            lexemeCollector = (TokenLexemeCollector)collector;
        }
        lexemeCollector.collect(new File(Objects.requireNonNull(f3).getFile()));
        List<String> tokenStream = lexemeCollector.getTokenTypeStream();
        monitor.monitor(tokenStream);

        List<AnalysisHistory> histories = processor.getHistories();

        Assert.assertNotEquals(0, histories.size());

    }

    @Test
    public void testMonitor_C_Grammar_2()
    {

        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
            "regular_grammar3.txt");
        URL f2 = Thread.currentThread().getContextClassLoader().getResource("c_grammar_test2.txt");
        buildGrammar(f1, f2);
        URL f3 = Thread.currentThread().getContextClassLoader().getResource("input_2.txt");

        AnalysisProcessor processor = new AnalysisProcessorImpl();

        GrammarProductionManager manager = new GrammarProductionManager(this.rowProductions,
            this.productionCutMap);
        ProgramMonitor monitor = new ProgramMonitor(manager, processor);

        LexemeCollector collector = this.lexerLexiconContent.buildCollector();
        TokenLexemeCollector lexemeCollector = null;
        if (collector instanceof TokenLexemeCollector)
        {
            lexemeCollector = (TokenLexemeCollector)collector;
        }
        lexemeCollector.collect(new File(Objects.requireNonNull(f3).getFile()));
        List<String> tokenStream = lexemeCollector.getTokenTypeStream();
        monitor.monitor(tokenStream);

        List<AnalysisHistory> histories = processor.getHistories();

        for (AnalysisHistory history : histories) {
            List<String> input = history.getInput();
            List<String> symbol = history.getSymbol();
            log.debug("INPUT : ＊ {}",input.toString());
            log.debug("SYMBOL : ＊ {}",symbol.toString());
        }
    }

    private void buildGrammar(URL f1, URL f2)
    {
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

}