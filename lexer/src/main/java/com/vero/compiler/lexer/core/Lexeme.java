package com.vero.compiler.lexer.core;


import com.vero.compiler.common.location.SourceSpan;
import com.vero.compiler.lexer.info.LexerTransitionInfo;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:31 2018/3/18.
 * @since vero-compiler
 */

@Data
public class Lexeme
{
    private LexerTransitionInfo lexerTransitionInfo;

    private int stateIndex;

    private String content;

    private SourceSpan sourceSpan;

    public Lexeme(LexerTransitionInfo lexerTransitionInfo, int stateIndex, SourceSpan sourceSpan,
                  String content)
    {
        this.lexerTransitionInfo = lexerTransitionInfo;
        this.stateIndex = stateIndex;
        this.content = content;
        this.sourceSpan = sourceSpan;
    }

    public boolean isEndOfStream()
    {
        return this.stateIndex == lexerTransitionInfo.getEndOfStreamState();
    }

    public Integer getTokenIndex()
    {
        return this.lexerTransitionInfo.getTokenIndex(getStateIndex());
    }

    @Override
    public String toString()
    {
        return "Line:" + sourceSpan.getStartLocation().getLine() + ",Column:"
               + sourceSpan.getStartLocation().getColumn() + ":[" + this.content + "]";
    }
}
