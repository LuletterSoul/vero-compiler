package com.vero.compiler.exception;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 1:07 2018/3/20.
 * @since vero-compiler
 */

public class TokenMatchLostException extends ParseException
{

    public TokenMatchLostException(String message)
    {
        super(message);
    }
}
