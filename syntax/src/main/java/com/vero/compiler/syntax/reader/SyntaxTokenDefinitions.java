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

import jdk.nashorn.internal.ir.Symbol;
import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:41 2018/3/22.
 * @since vero-compiler
 */

@Getter
public class SyntaxTokenDefinitions extends DefaultTokenDefinitions
{

    public Token SYNTAX_NO_TERMINAL;

    public Token SYNTAX_TOKEN;

    public Token BANF_DELIMITER;

    public Token UNION_DELIMITER;

    public Token SYNTAX_TERMINAL;

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
            Range('A', 'Z').Union(Symbol('_').Union(Range('a','z'))).Many1()).Union(Symbol('\'')).Concat(Symbol('>'));
        this.SYNTAX_NO_TERMINAL = syntaxLexer.defineToken(syntaxNoTerminal);
        syntaxRegularExpressionList.add(syntaxNoTerminal);

        RegularExpression terminals = RegularExpression.Range('a', 'z').Union(Range('A', 'Z')).Union(Symbol('_')).Many();
        this.SYNTAX_TERMINAL = syntaxLexer.defineToken(terminals);
        syntaxRegularExpressionList.add(terminals);

        RegularExpression BANF_delimiter = RegularExpression.Literal("::=");
        this.BANF_DELIMITER = syntaxLexer.defineToken(BANF_delimiter);
        syntaxRegularExpressionList.add(BANF_delimiter);

        RegularExpression UNION_delimiter = RegularExpression.Literal("|");
        this.UNION_DELIMITER = syntaxLexer.defineToken(UNION_delimiter);
        syntaxRegularExpressionList.add(UNION_delimiter);

        this.SYNTAX_TOKEN = syntaxLexer.defineToken(this.lexerTokenExpressionsUnion);
        syntaxRegularExpressionList.add(this.lexerTokenExpressionsUnion);


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
