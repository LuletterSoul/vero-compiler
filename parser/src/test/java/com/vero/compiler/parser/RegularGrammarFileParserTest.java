package com.vero.compiler.parser;


import java.io.File;
import java.io.IOException;

import org.junit.Test;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:05 2018/3/18.
 * @since vero-compiler
 */

public class RegularGrammarFileParserTest
{

    @Test
    public void parse() {
        File testGrammarFile = new File("C:\\Users\\31370\\IdeaProjects\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar2.txt");
        RegularGrammarFileParser parser = new RegularGrammarFileParser(testGrammarFile);
        try {
            parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}