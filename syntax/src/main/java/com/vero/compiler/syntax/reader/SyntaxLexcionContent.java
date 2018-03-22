package com.vero.compiler.syntax.reader;


import com.vero.compiler.exception.SyntaxException;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.scan.core.DefaultLexiconContent;
import com.vero.compiler.scan.core.LexemeCollector;
import com.vero.compiler.scan.core.TokenDefinitions;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:45 2018/3/22.
 * @since vero-compiler
 */

public class SyntaxLexcionContent extends DefaultLexiconContent
{

    public SyntaxLexcionContent(TokenDefinitions tokenDefinitions)
    {
        super(tokenDefinitions);
    }

    @Override
    public void handleExpressions(RegularExpression[] regularExpressions)
    {
        if (this.tokenDefinitions == null)
        {
            throw new SyntaxException("Syntax lexcion content must be set a token definition.");
        }
    }

    @Override
    public LexemeCollector buildCollector()
    {
        return new SyntaxLexemeCollector(this.scanner, this.info);
    }

    @Override
    public void setLexicon(Lexicon lexicon)
    {
        this.lexicon = lexicon;
        this.initContent();
    }
}
