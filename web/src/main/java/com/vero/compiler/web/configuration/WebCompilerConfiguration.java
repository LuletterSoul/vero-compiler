package com.vero.compiler.web.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.syntax.result.AnalysisProcessor;
import com.vero.compiler.syntax.result.AnalysisProcessorImpl;


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
