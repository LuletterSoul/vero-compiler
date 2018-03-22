package com.vero.compiler.syntax.reader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.LexiconContent;
import com.vero.compiler.scan.core.TokenLexiconContent;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:54 2018/3/22.
 * @since vero-compiler
 */

public class SyntaxLexemeCollectorTest
{

    @Test
    public void postPerLexemeCollected()
    {
        URL f1=Thread.currentThread().getContextClassLoader().getResource("regular_grammar3.txt");
        URL f2=Thread.currentThread().getContextClassLoader().getResource("syntax_grammar_test1.txt");
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

            LexemeCollector collector = syntaxLexiconContent.buildCollector();
            collector.collect(sourceFile);
            List<Lexeme> tokenStream = collector.getLexemeStream();
            //过滤空格、回车换行
            List<Lexeme> filterWhiteSpace = tokenStream.stream().filter(
                l -> !l.getContent().equals(" ")&&!l.getContent().equals("\r\n")).collect(Collectors.toList());
            Assert.assertEquals(filterWhiteSpace.get(0).getContent(), "<S>");
            Assert.assertEquals(filterWhiteSpace.get(1).getContent(), "::=");
            Assert.assertEquals(filterWhiteSpace.get(2).getContent(), "<S>");
            Assert.assertEquals(filterWhiteSpace.get(8).getContent(), "Integer");
            Assert.assertEquals(filterWhiteSpace.get(15).getContent(), "Float");
            Assert.assertEquals(filterWhiteSpace.get(16).getContent(), "(");
            // Assert.assertEquals(tokenStream.get(0).getContent(), "<S>");
            // Assert.assertEquals(tokenStream.get(0).getContent(), "<S>");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}