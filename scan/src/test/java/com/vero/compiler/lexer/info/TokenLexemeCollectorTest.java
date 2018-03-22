package com.vero.compiler.lexer.info;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.token.TokenType;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.TokenLexemeCollector;
import com.vero.compiler.scan.core.TokenLexiconContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.parser.RegularGrammarFileParser;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  17:38 2018/3/21.
 * @since vero-compiler
 */

@Slf4j
public class TokenLexemeCollectorTest {

    @Test
    public void collect() {
        URL f1=Thread.currentThread().getContextClassLoader().getResource("regular_grammar3.txt");
        URL f2=Thread.currentThread().getContextClassLoader().getResource("scannedTest4.txt");
        File tokenDefinitions = new File(Objects.requireNonNull(f1).getFile());
        File sourceFile = new File(Objects.requireNonNull(f2).getFile());

        RegularGrammarFileParser parser = new RegularGrammarFileParser();
        try {
            Map<String, String> typeDetails = TokenType.getTypeDetails();
            RegularExpression[] tokenExpressions = parser.parse(tokenDefinitions);
            TokenLexiconContent content = new TokenLexiconContent(tokenExpressions);
            LexemeCollector collector = content.buildCollector();
            Map<String, String> tokens = collector.collect(sourceFile);
            Assert.assertEquals(tokens.get("Float"),typeDetails.get("<keywords>"));
            Assert.assertEquals(tokens.get("myFloat"),typeDetails.get("<var>"));
            Assert.assertEquals(tokens.get("="),typeDetails.get("<operator>"));
            Assert.assertEquals(tokens.get(" "),typeDetails.get("<whitespace>"));
            List<Lexeme> tokenStream = collector.getLexemeStream();
            List<CompilationError> errors = collector.getErrors();
            tokenStream.forEach(detail->{
                if (!detail.getContent().equals(" ")&&!detail.getContent().equals("\r\n")) {
                    log.debug(detail.toString());
                }
            });
            errors.forEach(e -> log.error(e.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}