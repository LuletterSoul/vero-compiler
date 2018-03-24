package com.vero.compiler.common.error;


import com.vero.compiler.common.location.SourceSpan;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 23:06 2018/3/24.
 * @since vero-compiler
 */
@Data
public class CompilationErrorDto
{
    private String message;

    private SourceSpan errorPosition;

    private String detail;

    public CompilationErrorDto(String message, SourceSpan errorPosition, String detail)
    {
        this.message = message;
        this.errorPosition = errorPosition;
        this.detail = detail;
    }
}
