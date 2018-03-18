package com.vero.compiler.parser;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:59 2018/3/18.
 * @since vero-compiler
 */

@Slf4j
@Getter
public class RegularGrammarFileParser
{
    private File grammarSource;

    private FileInputStream fileReader;

    private List<String> nonTerminalSymbol;

    private List<Character> terminalSymbol;


    public RegularGrammarFileParser(File grammarSource)
    {
        this.grammarSource = grammarSource;
        try
        {
            this.fileReader = new FileInputStream(grammarSource);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        nonTerminalSymbol = new ArrayList<>();
        terminalSymbol = new ArrayList<>();
    }

    public void parse()
        throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(getFileReader(), "Unicode"));
        String str = null;
        while ((str = bufferedReader.readLine()) != null)
        {
//            List<Character> stack = new LinkedList<>();
//            List<Character> recognitionTerminalSymbol
//            for (int i = 0; i < str.toCharArray().length; i++) {
//                stack.add();
//            }
        }
    }
}
