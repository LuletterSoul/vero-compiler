package com.vero.compiler.web.configuration;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexemeCollector;
import com.vero.compiler.scan.core.TokenLexiconContent;
import com.vero.compiler.syntax.core.AnalysisHistory;
import com.vero.compiler.syntax.core.AnalysisProcessor;
import com.vero.compiler.syntax.core.AnalysisProcessorImpl;
import com.vero.compiler.syntax.core.ProgramMonitor;
import com.vero.compiler.syntax.production.GrammarProductionManager;
import com.vero.compiler.syntax.reader.SyntaxContentDefiner;
import com.vero.compiler.syntax.reader.SyntaxGrammarParser;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:32 2018/3/24.
 * @since vero-compiler
 */

@Configuration
public class WebCompilerConfiguration
{
    @Bean
    public RegularGrammarFileParser regularGrammarFileParser()
    {
        return new RegularGrammarFileParser();
    }

    @Bean
    public AnalysisProcessor processor() {
        return new AnalysisProcessorImpl();
    }
}
