package com.vero.compiler.syntax.reader;


import java.util.List;
import java.util.Objects;

import com.vero.compiler.exception.LexerTokenFoundException;
import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.scan.core.TokenLexiconContent;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:16 2018/3/22.
 * @since vero-compiler
 */

@Getter
public class SyntaxLexiconBase
{
    private Lexicon syntaxLexicon;

    private Lexicon lexerLexicon;

    private Lexer syntaxLexer;

    private List<RegularExpression> tokenExpressions;

    private SyntaxTokenDefinitions syntaxTokenDefinitions;

    private RegularExpression[] syntaxDefinitions;

    public SyntaxLexiconBase(Lexicon lexerLexicon)
    {
        this.syntaxLexicon = new Lexicon();
        this.lexerLexicon = lexerLexicon;
        buildSyntaxTokenDefinitions(lexerLexicon);
    }

    private void buildSyntaxTokenDefinitions(Lexicon lexerLexicon) {
        this.tokenExpressions = lexerLexicon.extractExpressions();
        if (Objects.isNull(this.tokenExpressions) || this.tokenExpressions.isEmpty())
        {
            throw new LexerTokenFoundException("No definition from lexer lexicon.");
        }
        this.syntaxTokenDefinitions = new SyntaxTokenDefinitions(this.syntaxLexicon, this.tokenExpressions);
    }

    public TokenLexiconContent buidLexionContent()
    {
        return new TokenLexiconContent(getSyntaxDefinitions());
    }
}
