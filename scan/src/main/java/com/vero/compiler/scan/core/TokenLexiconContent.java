package com.vero.compiler.scan.core;


import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 17:22 2018/3/21.
 * @since vero-compiler
 */

public class TokenLexiconContent extends DefaultLexiconContent
{

    protected TokenDefinitions definitions;

    public TokenLexiconContent(RegularExpression[] regularExpressions)
    {
        super(regularExpressions);
    }

    @Override
    public void handleExpressions(RegularExpression[] regularExpressions)
    {
        this.definitions = new LexerTokenDefinitions(this.lexicon, regularExpressions);
    }

    @Override
    public LexemeCollector buildCollector()
    {
        return new TokenLexemeCollector(this.scanner, this.info, this.definitions);
    }

    @Override
    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

}
