package com.vero.compiler.scan.core;


import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.generator.DFAModel;
import com.vero.compiler.lexer.info.LexerTransitionInfo;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:37 2018/3/22.
 * @since vero-compiler
 */

public interface LexiconContent
{
    LexemeCollector buildCollector();

    DFAModel getDfaModel();

    Scanner getScanner();

    TokenDefinitions getDefinitions();

    void setLexicon(Lexicon lexicon);

    Lexicon getLexicon();

    LexerTransitionInfo getInfo();

    Lexicon defineLexicon();

    void handleExpressions(RegularExpression[] regularExpressions);

}
