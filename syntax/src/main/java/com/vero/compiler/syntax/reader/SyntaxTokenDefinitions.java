package com.vero.compiler.syntax.reader;


import static com.vero.compiler.lexer.expression.RegularExpression.*;

import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.expression.RegularExpressionType;
import com.vero.compiler.lexer.token.Token;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:41 2018/3/22.
 * @since vero-compiler
 */

public class SyntaxTokenDefinitions
{

    private Token SYTAX_NO_TEMINAL;

    private Token SYNTAX_TOKEN;

    private Token BANF_DELIMITER;

    private List<RegularExpression> tokenExpressions;

    private Lexicon syntaxLexicon;

    public SyntaxTokenDefinitions(Lexicon syntaxLexicon, List<RegularExpression> tokenExpressions)
    {
        this.syntaxLexicon = syntaxLexicon;
        this.tokenExpressions = tokenExpressions;
    }

    private RegularExpression[] buildSyntaxRegularExpressions()
    {
        Lexer syntaxLexer = this.syntaxLexicon.getDefaultLexer();

        List<RegularExpression> syntaxRegularExpressionList = new ArrayList<>();

        RegularExpression tokenRegularExpression = Empty();
        for (RegularExpression t : this.tokenExpressions)
        {
            if (tokenRegularExpression.getExpressionType().equals(RegularExpressionType.Empty))
            {
                tokenRegularExpression = t;
            }
            else
            {
                tokenRegularExpression = tokenRegularExpression.Union(t);
            }
        }
        RegularExpression syntaxNoTerminal = RegularExpression.Symbol('<').Concat(
            Range('A', 'Z').Union(Symbol('_')).Many1()).Concat(Symbol('>'));
        Token SYNTAX_NO_TEMINAL = syntaxLexer.defineToken(syntaxNoTerminal);
        syntaxRegularExpressionList.add(syntaxNoTerminal);

        Token SYNTAX_TOKEN = syntaxLexer.defineToken(tokenRegularExpression);
        syntaxRegularExpressionList.add(tokenRegularExpression);

        RegularExpression BANF_delimiter = RegularExpression.Literal("::=");
        Token BANF_DELIMITER = syntaxLexer.defineToken(BANF_delimiter);
        syntaxRegularExpressionList.add(BANF_delimiter);
        return (RegularExpression[])syntaxRegularExpressionList.toArray();
    }
}
