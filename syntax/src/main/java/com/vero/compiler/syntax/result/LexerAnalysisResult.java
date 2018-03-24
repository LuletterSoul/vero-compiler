package com.vero.compiler.syntax.result;


import java.util.List;

import com.vero.compiler.common.error.CompilationErrorDto;
import com.vero.compiler.parser.LexemeDto;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 5:58 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class LexerAnalysisResult
{
    private List<LexemeDto> lexemeDtos;

    private List<CompilationErrorDto> errorDtos;
}
