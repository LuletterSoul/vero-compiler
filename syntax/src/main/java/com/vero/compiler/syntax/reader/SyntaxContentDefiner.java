package com.vero.compiler.syntax.reader;


import java.util.List;
import java.util.Objects;

import com.vero.compiler.exception.LexerTokenFoundException;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;

import com.vero.compiler.lexer.expression.RegularExpressionType;
import com.vero.compiler.scan.core.LexiconContent;
import lombok.Getter;

import static com.vero.compiler.lexer.expression.RegularExpression.Empty;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:16 2018/3/22.
 * @since vero-compiler
 */

@Getter
public class SyntaxContentDefiner
{
    private Lexicon syntaxLexicon;

    private Lexicon lexerLexicon;

    private List<RegularExpression> lexerTokenExpression;

    private SyntaxTokenDefinitions syntaxTokenDefinitions;

    private LexiconContent syntaxLexcionContent;



    public SyntaxContentDefiner(Lexicon lexerLexicon)
    {
        this.syntaxLexicon = new Lexicon();
        this.lexerLexicon = lexerLexicon;
        buildSyntaxTokenDefinitions(lexerLexicon);
    }


    private void buildSyntaxTokenDefinitions(Lexicon lexerLexicon)
    {
        this.syntaxLexicon = new Lexicon();
        RegularExpression union = unionLexerTokenExpressions(lexerLexicon);
        this.syntaxTokenDefinitions = new SyntaxTokenDefinitions(this.syntaxLexicon,union);
        this.syntaxLexcionContent = new SyntaxLexcionContent(this.syntaxTokenDefinitions);
        this.syntaxLexcionContent.setLexicon(syntaxLexicon);
    }

    private RegularExpression unionLexerTokenExpressions(Lexicon lexerLexicon) {
        this.lexerTokenExpression = lexerLexicon.extractExpressions();
        if (Objects.isNull(this.lexerTokenExpression) || this.lexerTokenExpression.isEmpty())
        {
            throw new LexerTokenFoundException("No definition from lexer lexicon.");
        }
        RegularExpression union = Empty();
        for (RegularExpression t : this.lexerTokenExpression)
        {
            if (union.getExpressionType().equals(RegularExpressionType.Empty))
            {
                union = t;
            }
            else
            {
                union = union.Union(t);
            }
        }
        return union;
    }
}
