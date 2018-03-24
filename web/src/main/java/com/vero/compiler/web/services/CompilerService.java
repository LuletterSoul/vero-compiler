package com.vero.compiler.web.services;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexemeCollector;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.core.AnalysisHistory;
import com.vero.compiler.syntax.core.AnalysisProcessor;
import com.vero.compiler.syntax.core.ProgramMonitor;
import com.vero.compiler.syntax.production.GrammarProductionManager;
import com.vero.compiler.syntax.reader.SyntaxContentDefiner;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:49 2018/3/24.
 * @since vero-compiler
 */

@Component
@SuppressWarnings("unchecked")
@Getter
public class CompilerService
{

    private final RegularGrammarFileParser regularGrammarFileParser;

    private SyntaxGrammarParser syntaxGrammarParser;

    private LexiconContent lexerLexiconContent;

    private LexiconContent syntaxLexiconContent;

    private SyntaxContentDefiner lexiconBase;

    private Map<String, List<List<String>>> productionCutMap = new HashMap<>();

    private List<String> rowProductions = new ArrayList<>();

    private AnalysisProcessor processor;

    @Autowired
    public CompilerService(RegularGrammarFileParser regularGrammarFileParser)
    {
        this.regularGrammarFileParser = regularGrammarFileParser;
    }

    @Autowired
    public void setProcessor(AnalysisProcessor processor)
    {
        this.processor = processor;
    }

    private void initCompilerEnvironment(RegularExpression[] tokenExpressions)
    {
        this.lexerLexiconContent = new TokenLexiconContent(tokenExpressions);
        this.lexiconBase = new SyntaxContentDefiner(lexerLexiconContent.getLexicon());
        this.syntaxLexiconContent = lexiconBase.getSyntaxLexcionContent();
    }

    public RegularExpression[] generateTokenLexer(File lexerDefinition)
    {
        try
        {
            RegularExpression[] tokenExpressions = regularGrammarFileParser.parse(lexerDefinition);
            initCompilerEnvironment(tokenExpressions);
            return tokenExpressions;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> generateSyntaxReadLexer(File sourceFile)
    {

        this.syntaxGrammarParser = new SyntaxGrammarParser(syntaxLexiconContent, sourceFile);

        this.rowProductions = syntaxGrammarParser.parseProductionRows();

        this.productionCutMap = syntaxGrammarParser.parseProduction();

        return this.rowProductions;
    }

    public Map<String, String> analysis(File source)
    {
        GrammarProductionManager manager = new GrammarProductionManager(this.rowProductions,
            this.productionCutMap);

        ProgramMonitor monitor = new ProgramMonitor(manager, processor);

        LexemeCollector collector = this.lexerLexiconContent.buildCollector();

        TokenLexemeCollector lexemeCollector = null;
        if (collector instanceof TokenLexemeCollector)
        {
            lexemeCollector = (TokenLexemeCollector)collector;
        }
        lexemeCollector.collect(source);
        List<String> tokenStream = lexemeCollector.getTokenTypeStream();
        monitor.monitor(tokenStream);
        List<AnalysisHistory> histories = processor.getHistories();
        return lexemeCollector.getLexeme2TokenDetail();
    }
}
