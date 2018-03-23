package com.vero.compiler.lexer.source;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import com.vero.compiler.exception.CharBufferOverBoundException;
import com.vero.compiler.exception.FilleNotReadable;
import com.vero.compiler.exception.SourceFileNotFound;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:58 2018/3/18.
 * @since vero-compiler
 */

@Getter
@Slf4j
public class FileSourceReader extends SourceReader
{
    private FileReader fileReader;

    private File fileSource;

    private char[] charBuffer;

    private Integer charBufferSize = 1024;

    private Integer currentBufferPointer = charBufferSize-1;

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
            this.fileSource = sourceFile;
            init();
        }
        catch (FileNotFoundException e)
        {
            throw new SourceFileNotFound("Source file not found.");
        }
    }

    public FileSourceReader()
    {
        this.charBuffer = new char[charBufferSize];
        this.initCharBuffer();
    }

    private void init()
    {
        this.charBuffer = new char[this.charBufferSize];
        this.initCharBuffer();
        this.currentBufferPointer = charBufferSize -1;
    }

    @Override
    public Integer peekChar()
    {
        if (isPeekEndOfBuffer())
        {
            log.debug("Buffer over flow.");
            if (handleOverflow())
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

    private boolean handleOverflow()
    {
        char current = getCharBuffer()[this.currentBufferPointer];
        boolean succces = readCharsToBuffer();
        if (succces)
        {
            if (current == Character.MAX_VALUE) {
                return succces;
            }
            System.arraycopy(charBuffer, 0, charBuffer, 1, this.charBuffer.length - 1);
            charBuffer[0] = current;
        }
        return succces;

    }

    @Override
    public void loadFileSource(File file)
    {
        try
        {
            this.fileReader = new FileReader(file);
            this.fileSource = file;
            init();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected char internalReadChar()
    {
        validateFileSource();
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

    private void validateFileSource()
    {
        if (Objects.isNull(this.fileSource))
        {
            throw new FilleNotReadable("File is not readable.Because source is null.");
        }
    }

    private boolean readCharsToBuffer()
    {
        try
        {
            this.initCharBuffer();
            if (getFileReader().read(this.charBuffer, 0, charBuffer.length - 1) > 0)
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
        for (int i = 0; i < this.charBuffer.length; i++ )
        {
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

    private boolean isPeekEndOfBuffer()
    {
        return getCharBufferSize().equals(getCurrentBufferPointer() + 1);
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
