package com.vero.compiler.syntax.reader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexiconContent;

import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:07 2018/3/22.
 * @since vero-compiler
 */

@Slf4j
public class SyntaxGrammarParserTest
{

    @Test
    public void parseProduction()
    {
        URL f1 = Thread.currentThread().getContextClassLoader().getResource(
            "regular_grammar3.txt");
        URL f2 = Thread.currentThread().getContextClassLoader().getResource(
            "syntax_grammar_test2.txt");
        File tokenDefinitions = new File(Objects.requireNonNull(f1).getFile());
        File sourceFile = new File(Objects.requireNonNull(f2).getFile());
        RegularGrammarFileParser parser = new RegularGrammarFileParser();

        try
        {
            RegularExpression[] tokenExpressions = parser.parse(tokenDefinitions);

            LexiconContent lexerLexiconContent = new TokenLexiconContent(tokenExpressions);

            SyntaxContentDefiner lexiconBase = new SyntaxContentDefiner(
                lexerLexiconContent.getLexicon());

            LexiconContent syntaxLexiconContent = lexiconBase.getSyntaxLexcionContent();

            Assert.assertNotNull(syntaxLexiconContent);

            SyntaxGrammarParser grammarParser = new SyntaxGrammarParser(syntaxLexiconContent,sourceFile);

            Map<String, List<List<String>>> productionCutMap = grammarParser.parseProduction();

            Set<String> keys = productionCutMap.keySet();
            for (String key : keys)
            {
                System.out.print(key + ": ");
                System.out.println(productionCutMap.get(key));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}