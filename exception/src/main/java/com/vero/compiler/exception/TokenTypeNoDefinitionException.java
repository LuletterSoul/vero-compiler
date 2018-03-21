package com.vero.compiler.exception;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 15:40 2018/3/21.
 * @since vero-compiler
 */

public class TokenTypeNoDefinitionException extends ParseException
{
    public TokenTypeNoDefinitionException(String message)
    {
        super(message);
    }
}
