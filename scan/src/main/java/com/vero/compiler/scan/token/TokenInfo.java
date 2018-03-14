package com.vero.compiler.scan.token;


import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.exception.NFAModalInValidException;
import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.generator.NFAModel;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;

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
    public String toString() {
        return definition.toString();
    }
}
