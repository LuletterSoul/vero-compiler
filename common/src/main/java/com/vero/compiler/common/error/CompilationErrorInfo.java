package com.vero.compiler.common.error;


import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:11 2018/3/21.
 * @since vero-compiler
 */

@Data
public class CompilationErrorInfo
{
    private Integer id;

    private Integer level;

    private CompilationStage stage;

    private String messageTemplate;

    public CompilationErrorInfo(Integer id, Integer level, CompilationStage stage,
                                String messageTemplate)
    {
        this.id = id;
        this.level = level;
        this.stage = stage;
        this.messageTemplate = messageTemplate;
    }
}
