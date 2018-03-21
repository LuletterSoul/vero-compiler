package com.vero.compiler.lexer.source;


import lombok.Getter;

import java.io.File;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:48 2018/3/18.
 * @since vero-compiler
 */

@Getter
public abstract class SourceReader
{
    public static Integer DEFAULT_TABLE_SIZE = 1;

    private Integer tableSize;

    // reverting service
    // private StringBuilderReader m_backupReader;
    private StringBuilder backupStreamBuilder;

    private SourceLocation preLocation;

    private SourceLocation location;

    private int m_revertPointKeySeed;

    public abstract Integer peekChar();

    public SourceReader(Integer tableSize)
    {
        this.tableSize = tableSize;
    }

    public abstract void  loadFileSource(File file);

    public SourceReader()
    {
        this(DEFAULT_TABLE_SIZE);
        this.location = new SourceLocation();
        this.preLocation = new SourceLocation();
        location.setLine(1);
        preLocation.setLine(1);
    }

    public char readChar()
    {
        char charValue = internalReadChar();
        this.savePreLocation(location);
        postHandleReadChar(charValue);
        return charValue;
    }

    private void savePreLocation(SourceLocation location)
    {
        this.preLocation = new SourceLocation();
        this.preLocation.setLine(location.getLine());
        this.preLocation.setColumn(location.getColumn());
        this.preLocation.setCharIndex(location.getCharIndex());
    }

    public void postHandleReadChar(char charValue)
    {
        this.location.incrementCharIndex();
        if (charValue == '\t')
        {
            this.location.incrementColumn(getTableSize());
        }
        else
        {
            this.location.incrementColumn(1);
        }
        if (charValue == '\n')
        {
            this.location.increamentLine();
            this.location.resetColumn();
        }
    }

    protected abstract char internalReadChar();

}
