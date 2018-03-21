package com.vero.compiler.lexer.source;


import com.google.common.base.Objects;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:35 2018/3/18.
 * @since vero-compiler
 */

@Getter
public class SourceSpan
{
    private SourceLocation endLocation;

    private SourceLocation startLocation;

    public SourceSpan(SourceLocation startLocation , SourceLocation endLocation) {
        this.endLocation = endLocation;
        this.startLocation = startLocation;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceSpan that = (SourceSpan)o;
        return Objects.equal(endLocation, that.endLocation)
               && Objects.equal(startLocation, that.startLocation);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(endLocation, startLocation);
    }
}
