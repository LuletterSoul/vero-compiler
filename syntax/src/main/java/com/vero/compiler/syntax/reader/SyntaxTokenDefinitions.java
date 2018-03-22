package com.vero.compiler.syntax.reader;


import static com.vero.compiler.lexer.expression.RegularExpression.Range;
import static com.vero.compiler.lexer.expression.RegularExpression.Symbol;

import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.token.Token;
import com.vero.compiler.scan.core.DefaultTokenDefinitions;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:41 2018/3/22.
 * @since vero-compiler
 */

@Getter
public class SyntaxTokenDefinitions extends DefaultTokenDefinitions
{

    private Token SYNTAX_NO_TEMINAL;

    private Token SYNTAX_TOKEN;

    private Token BANF_DELIMITER;

    private RegularExpression lexerTokenExpressionsUnion;

    private Lexicon syntaxLexicon;

    private RegularExpression[] syntaxExpressions;

    public SyntaxTokenDefinitions(Lexicon syntaxLexicon,
                                  RegularExpression lexerTokenExpressionUnion)
    {
        this.lexerTokenExpressionsUnion = lexerTokenExpressionUnion;
        this.syntaxLexicon = syntaxLexicon;
        this.buildSyntaxRegularExpressions();
    }

    private void buildSyntaxRegularExpressions()
    {
        Lexer syntaxLexer = this.syntaxLexicon.getDefaultLexer();

        List<RegularExpression> syntaxRegularExpressionList = new ArrayList<>();

        RegularExpression syntaxNoTerminal = RegularExpression.Symbol('<').Concat(
            Range('A', 'Z').Union(Symbol('_')).Many1()).Concat(Symbol('>'));
        this.SYNTAX_NO_TEMINAL = syntaxLexer.defineToken(syntaxNoTerminal);
        syntaxRegularExpressionList.add(syntaxNoTerminal);

        this.SYNTAX_TOKEN = syntaxLexer.defineToken(this.lexerTokenExpressionsUnion);
        syntaxRegularExpressionList.add(this.lexerTokenExpressionsUnion);

        RegularExpression BANF_delimiter = RegularExpression.Literal("::=");
        this.BANF_DELIMITER = syntaxLexer.defineToken(BANF_delimiter);
        syntaxRegularExpressionList.add(BANF_delimiter);

        this.syntaxExpressions = transferToArray(syntaxRegularExpressionList);
    }

    private RegularExpression[] transferToArray(List<RegularExpression> syntaxRegularExpressionList)
    {
        RegularExpression[] regularExpressions = new RegularExpression[syntaxRegularExpressionList.size()];
        for (int i = 0; i < syntaxRegularExpressionList.size(); i++ )
        {
            regularExpressions[i] = syntaxRegularExpressionList.get(i);
        }
        return regularExpressions;
    }
}
