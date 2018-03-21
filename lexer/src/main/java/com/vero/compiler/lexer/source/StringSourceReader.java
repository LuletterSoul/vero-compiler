package com.vero.compiler.lexer.source;


import java.io.File;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:58 2018/3/18.
 * @since vero-compiler
 */

public class StringSourceReader extends SourceReader
{
    @Override
    public Integer peekChar()
    {
        return null;
    }

    @Override
    public void loadFileSource(File file)
    {

    }

    @Override
    protected char internalReadChar()
    {
        return 0;
    }
}
