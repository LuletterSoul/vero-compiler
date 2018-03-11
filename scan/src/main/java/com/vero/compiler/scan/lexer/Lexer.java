package com.vero.compiler.scan.lexer;


import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.token.Token;
import com.vero.compiler.scan.token.TokenInfo;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:09 2018/3/11.
 * @since vero-compiler
 */

@Data
public class Lexer
{
    private List<TokenInfo> tokenInfos;

    private Lexicon lexicon;

    private Lexer baseLexer;

    private Integer index;

    private Integer level;

    private List<Lexer> children;

    public Lexer(Lexicon lexicon, Integer index)
    {
        this(lexicon, index, null);
    }

    public Lexer(Lexicon lexicon, Integer index, Lexer baseLexer)
    {
        this.children = new ArrayList<Lexer>();
        this.lexicon = lexicon;
        this.baseLexer = baseLexer;
        this.tokenInfos = new ArrayList<TokenInfo>();
        this.index = index;
        if (baseLexer == null)
        {
            this.level = 0;
        }
        else
        {
            this.level = this.baseLexer.getLevel() + 1;
            baseLexer.getChildren().add(this);
        }
    }

    public Token defineToken(RegularExpression regex, String description)
    {
        int indexInState = getTokenInfos().size();
        TokenInfo token = getLexicon().addToken(regex, this, indexInState, description);
        getTokenInfos().add(token);
        return token.getTag();
    }
}
