package com.vero.compiler.lexer.info;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        File tokenDefinitions = new File("C:\\Users\\31370\\IdeaProjects\\vero-compiler\\parser\\src\\test\\resources\\regular_grammar3.txt");
        File sourceFile = new File("C:\\Users\\31370\\IdeaProjects\\vero-compiler\\scan\\src\\test\\resource\\scannedTest3.txt");

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