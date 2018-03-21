package com.vero.compiler.common.error;


import com.vero.compiler.common.location.SourceSpan;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:11 2018/3/21.
 * @since vero-compiler
 */

@Data
public class CompilationError
{
    private CompilationErrorInfo info;

    private String message;

    private SourceSpan errorPosition;

    public CompilationError(CompilationErrorInfo info, String message, SourceSpan errorPosition)
    {
        this.info = info;
        this.message = message;
        this.errorPosition = errorPosition;
    }

    @Override
    public String toString()
    {
        return String.format("Error:%s : %s  Line: %s Column: %s", info.getId(), message,
            getErrorPosition().getStartLocation().getLine(),
            getErrorPosition().getStartLocation().getColumn());
    }
}
