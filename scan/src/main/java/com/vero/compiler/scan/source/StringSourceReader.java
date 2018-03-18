package com.vero.compiler.scan.source;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  14:58 2018/3/18.
 * @since vero-compiler
 */

public class StringSourceReader extends SourceReader {
    @Override
    public Integer peekChar() {
        return null;
    }

    @Override
    protected char internalReadChar() {
        return 0;
    }
}
