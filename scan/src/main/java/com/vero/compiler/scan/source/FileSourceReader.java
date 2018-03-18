package com.vero.compiler.scan.source;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.vero.compiler.scan.exception.CharBufferOverBoundException;
import com.vero.compiler.scan.exception.SourceFileNotFound;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:58 2018/3/18.
 * @since vero-compiler
 */

@Getter
public class FileSourceReader extends SourceReader
{
    private FileReader fileReader;

    private char[] charBuffer;

    private Integer charBufferSize = 64;

    private Integer currentBufferPointer = 64;

    public FileSourceReader(FileReader fileReader, Integer charBufferSize)
    {
        this.charBufferSize = charBufferSize;
        this.fileReader = fileReader;
        this.charBuffer = new char[charBufferSize];
        this.initCharBuffer();
        this.currentBufferPointer = charBufferSize;
    }

    public FileSourceReader(File sourceFile)
    {
        try
        {
            this.fileReader = new FileReader(sourceFile);
            this.charBuffer = new char[this.charBufferSize];
            this.initCharBuffer();
            this.currentBufferPointer = charBufferSize;
        }
        catch (FileNotFoundException e)
        {
            throw new SourceFileNotFound("Source file not found.");
        }
    }

    @Override
    public Integer peekChar()
    {
        if (isEndOfBuffer())
        {
            if (readCharsToBuffer())
            {
                return (int)getCharBuffer()[this.currentBufferPointer + 1];
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return (int)getCharBuffer()[this.currentBufferPointer + 1];
        }
    }

    @Override
    protected char internalReadChar()
    {
        if (isEndOfBuffer())
        {
            if (readCharsToBuffer())
            {
                movePointer();
                return getCharBuffer()[this.currentBufferPointer];
            }
            else
            {
                return (char)-1;
            }
        }
        else
        {
            movePointer();
            return getCharBuffer()[this.currentBufferPointer];
        }
    }

    private boolean readCharsToBuffer()
    {
        try
        {
            this.initCharBuffer();
            if (getFileReader().read(this.charBuffer) > 0)
            {
                this.currentBufferPointer = -1;
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private void initCharBuffer()
    {
        for (int i = 0; i < this.charBuffer.length; i++) {
            charBuffer[i] = Character.MAX_VALUE;
        }
    }

    private void movePointer(Integer integer)
    {
        if (getCurrentBufferPointer() + integer >= getCharBufferSize())
        {
            throw new CharBufferOverBoundException("Char buffer access over bound.");
        }
        this.currentBufferPointer += integer;
    }

    private boolean isEndOfBuffer()
    {
        return getCurrentBufferPointer().equals(getCharBufferSize());
    }

    private void movePointer()
    {
        if (getCurrentBufferPointer() + 1 >= getCharBufferSize())
        {
            throw new CharBufferOverBoundException("Char buffer access over bound.");
        }
        ++this.currentBufferPointer;
    }

}
