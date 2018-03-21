package com.vero.compiler.lexer.core;


import java.util.ArrayList;
import java.util.List;

import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.token.Token;
import com.vero.compiler.lexer.token.TokenInfo;

import lombok.Data;


/**
 * 词法分析器,可以使用{@link #defineToken(RegularExpression, String)} 定义一系列正规表达式(Token转化而来)。
 * 词法分析器在同一个词典上下文一般只有一个,但也可以有多个 需要让某些词素在不同环境下展示为不同的类型，就可以定义新的Lexer。
 * 比如说“get”这个词素通常应该是一个标识符，而在定义属性的上下文环境下，它就变成了一个关键字。 Lexer允许派生子状态来支持这种场景。
 *
 * @see Lexicon {@link Lexicon #defineLexer}用于定义新的词法分析器,
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:09 2018/3/11.
 * @since vero-compiler
 */

@Data
public class Lexer
{
    private List<TokenInfo> tokenInfos;

    // 当前词法分析器所在的词典上下文;
    private Lexicon lexiconContent;

    private Lexer baseLexer;

    private Integer index;

    // 当前词法分析器的层级
    private Integer level;

    // 统领的子词法分析器
    private List<Lexer> children;

    public Lexer(Lexicon lexiconContent, Integer index)
    {
        this(lexiconContent, index, null);
    }

    /**
     * @param lexiconContent
     *            词法分析器所属的词典上下文环境
     * @param index
     *            词法分析器在分析LA表中下标顺序
     * @param baseLexer
     *            基础分析器
     */
    public Lexer(Lexicon lexiconContent, Integer index, Lexer baseLexer)
    {
        this.children = new ArrayList<>();
        this.lexiconContent = lexiconContent;
        this.baseLexer = baseLexer;
        this.tokenInfos = new ArrayList<>();
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

    public Token defineToken(RegularExpression regex)
    {
        return this.defineToken(regex, null);
    }

    public Token defineToken(RegularExpression regex, String description)
    {
        int indexInState = getTokenInfos().size();
        TokenInfo token = getLexiconContent().addToken(regex, this, indexInState, description);
        getTokenInfos().add(token);
        return token.getTag();
    }

    public Lexer createSubLexer()
    {
        return getLexiconContent().defineLexer(this);
    }

    @Override
    public String toString()
    {
        return "Lexer{" + "tokenInfos=" + tokenInfos + '}';
    }
}
