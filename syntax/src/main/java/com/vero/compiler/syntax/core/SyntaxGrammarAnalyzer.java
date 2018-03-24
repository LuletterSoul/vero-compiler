package com.vero.compiler.syntax.core;


import java.io.File;
import java.util.Objects;

import com.vero.compiler.exception.ParseException;
import com.vero.compiler.syntax.production.GrammarProductionManager;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 9:04 2018/3/23.
 * @since vero-compiler
 */
@Getter
public class SyntaxGrammarAnalyzer
{
    private ProgramMonitor monitor;

    private SyntaxGrammarParser grammarParser;

    private AnalysisProcessor processor;

    public SyntaxGrammarAnalyzer(ProgramMonitor monitor, SyntaxGrammarParser grammarParser,
                                 AnalysisProcessor processor)
    {
        this.monitor = monitor;
        this.grammarParser = grammarParser;
        this.processor = processor;
    }

    public SyntaxGrammarAnalyzer()
    {

    }

    public void analysis(File sourceFile)
    {
        if (Objects.isNull(monitor))
        {
            GrammarProductionManager productionManager = buildProductionManager(sourceFile);
            this.monitor = new ProgramMonitor(productionManager, processor);
        }
        else
        {
            this.monitor.reloadMonitor(buildProductionManager(sourceFile));
        }
        this.monitor.extendGrammar();
    }

    private GrammarProductionManager buildProductionManager(File sourceFile)
    {
        if (Objects.isNull(grammarParser))
        {
            throw new ParseException("Grammar production parser is Empty.");
        }
        grammarParser.parse(sourceFile);
        return new GrammarProductionManager(grammarParser.parseProductionRows(),
            grammarParser.parseProduction());
    }

}
