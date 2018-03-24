package com.vero.compiler.web.services;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vero.compiler.syntax.result.LexerAnalysisResult;
import com.vero.compiler.syntax.result.SyntaxAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.common.error.CompilationErrorDto;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.LexemeDto;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexemeCollector;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.result.AnalysisProcessor;
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

    private List<String> preFileLines = new ArrayList<>();

    private List<CompilationError> errors = new ArrayList<>();

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

    public List<ProductionDto> generateTokenLexer(File lexerDefinition)
    {
        try
        {
            RegularExpression[] tokenExpressions = regularGrammarFileParser.parse(lexerDefinition);
            initCompilerEnvironment(tokenExpressions);
            List<ProductionDto> productionDtos = new ArrayList<>();
            regularGrammarFileParser.getGrammarProductions().forEach(
                p -> productionDtos.add(new ProductionDto(p.getLeftPart(), p.getDetail())));
            return productionDtos;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductionDto> generateSyntaxReadLexer(File sourceFile)
    {

        this.syntaxGrammarParser = new SyntaxGrammarParser(syntaxLexiconContent, sourceFile);

        this.rowProductions = syntaxGrammarParser.parseProductionRows();

        this.productionCutMap = syntaxGrammarParser.parseProduction();

        return buildProductionDtos();
    }

    private List<ProductionDto> buildProductionDtos()
    {
        List<ProductionDto> productionDtos = new ArrayList<>();
        this.syntaxGrammarParser.getProductions().forEach(
            p -> productionDtos.add(new ProductionDto(p.getLeftPart(), p.getDetail())));
        return productionDtos;
    }


    public LexerAnalysisResult getTokens(File source)
    {
        this.errors.clear();
        try
        {
            recordFileLines(source);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LexemeCollector collector = this.lexerLexiconContent.buildCollector();
        TokenLexemeCollector lexemeCollector = null;
        if (collector instanceof TokenLexemeCollector)
        {
            lexemeCollector = (TokenLexemeCollector)collector;
        }
        lexemeCollector.collect(source);
        return buildLexerAnalysisResult(lexemeCollector);
    }

    private void recordFileLines(File source)
        throws IOException
    {
        this.preFileLines.clear();
        Files.readLines(source, Charsets.UTF_8).stream().filter(
            line -> line.length() >= 1).forEach(l -> this.preFileLines.add(l));
    }

    private LexerAnalysisResult buildLexerAnalysisResult(TokenLexemeCollector lexemeCollector)
    {
        List<LexemeDto> lexemeDtoList = new ArrayList<>();
        List<CompilationErrorDto> errorDtos = new ArrayList<>();
        lexemeCollector.getLexeme2TokenDetail().forEach(
            (k, v) -> lexemeDtoList.add(new LexemeDto(k, v)));
        lexemeCollector.getErrors().forEach(e -> errorDtos.add(
                new CompilationErrorDto(e.getMessage(), e.getErrorPosition(), e.toString())));
        return new LexerAnalysisResult(lexemeDtoList,errorDtos);
    }

    public SyntaxAnalysisResult analysis(File source)
    {
        this.processor.reset();
        this.errors.clear();


        try
        {
            recordFileLines(source);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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
        return buildSyntaxAnalysisResult(manager, monitor, lexemeCollector);
    }

    private SyntaxAnalysisResult buildSyntaxAnalysisResult(GrammarProductionManager manager,
                                                           ProgramMonitor monitor,
                                                           TokenLexemeCollector lexemeCollector)
    {
        SyntaxAnalysisResult syntaxAnalysisResult = new SyntaxAnalysisResult();
        syntaxAnalysisResult.setSyntaxGrammarProductions(manager.getRawProductions());
        syntaxAnalysisResult.setAnalysisHistories(processor.getHistories());
        syntaxAnalysisResult.setAnalysisTable(monitor.getDriverInfo().transfer());
        syntaxAnalysisResult.setPreLines(getPreFileLines());
        syntaxAnalysisResult.setTokenStream(lexemeCollector.getTokenTypeStream());
        syntaxAnalysisResult.setLexerAnalysisResult(buildLexerAnalysisResult(lexemeCollector));
        return syntaxAnalysisResult;
    }

}
