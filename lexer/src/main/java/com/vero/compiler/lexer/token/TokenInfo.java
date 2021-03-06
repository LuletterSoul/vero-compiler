package com.vero.compiler.lexer.token;


import com.google.common.base.Objects;
import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.exception.NFAModalInValidException;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.generator.NFAModel;
import com.vero.compiler.lexer.core.Lexer;
import com.vero.compiler.lexer.core.Lexicon;

import lombok.Data;


/**
 * 封装了每个Token的词典描述{@link Lexicon}
 * 
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:08 2018/3/11.
 * @since vero-compiler
 */

@Data
public class TokenInfo
{

    private Token tag;

    /**
     * 所处的词典上下文环境
     */
    private Lexicon lexicon;

    /**
     * 词法分析器信息
     */
    private Lexer lexerState;

    /**
     * 正则表达式的定义
     */
    private RegularExpression definition;

    public TokenInfo(RegularExpression definition, Lexicon lexicon, Lexer lexerState, Token tag)
    {
        this.tag = tag;
        this.lexicon = lexicon;
        this.lexerState = lexerState;
        this.definition = definition;
    }

    public NFAModel createFiniteAutomationModel(RegularExpressionConverter converter)
    {
        NFAModel nfaModel = converter.convert(getDefinition());

        if (nfaModel == null)
        {
            throw new NFAModalInValidException("NFA modal could not be null.");
        }

        nfaModel.getTailState().setIndex(getTag().getIndex());
        return nfaModel;
    }

    public Integer getTagIndex()
    {
        return this.getTag().getIndex();
    }

    public Integer getLexerStateIndex()
    {
        return this.getLexerState().getIndex();
    }

    @Override
    public String toString()
    {
        return definition.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TokenInfo tokenInfo = (TokenInfo)o;
        return Objects.equal(tag.getIndex(), tokenInfo.tag.getIndex());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(tag.getIndex(),lexerState.getIndex());
    }
}
