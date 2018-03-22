package com.vero.compiler.exception;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:22 2018/3/22.
 * @since vero-compiler
 */

public class LexerTokenFoundException extends SyntaxException
{
    public LexerTokenFoundException(String message)
    {
        super(message);
    }
}
