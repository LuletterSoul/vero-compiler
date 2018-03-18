package com.vero.compiler.scan.lexer;


import com.vero.compiler.scan.ScannerInfo;
import com.vero.compiler.scan.source.SourceSpan;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:31 2018/3/18.
 * @since vero-compiler
 */

@Data
public class Lexeme
{
    private ScannerInfo scannerInfo;

    private int stateIndex;

    private String content;

    private SourceSpan sourceSpan;

    public Lexeme(ScannerInfo scannerInfo, int stateIndex, SourceSpan sourceSpan, String content)
    {
        this.scannerInfo = scannerInfo;
        this.stateIndex = stateIndex;
        this.content = content;
        this.sourceSpan = sourceSpan;
    }

    public boolean isEndOfStream()
    {
        return this.stateIndex == scannerInfo.getEndOfStreamState();
    }

    public Integer getTokenIndex()
    {
        return this.scannerInfo.getTokenIndex(getStateIndex());
    }

}
