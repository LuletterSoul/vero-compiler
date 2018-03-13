package com.vero.compiler.scan.token;


import com.google.common.base.Objects;

import lombok.Data;


/**
 *
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:05 2018/3/11.
 * @since vero-compiler
 */

@Data
public class Token
{
    private Integer index;

    private String description;

    private Integer lexerIndex;

    public Token(Integer index, String description, Integer lexerIndex)
    {
        this.index = index;
        this.description = description;
        this.lexerIndex = lexerIndex;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Token token = (Token)o;
        return Objects.equal(index, token.index);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(super.hashCode(), index);
    }
}
