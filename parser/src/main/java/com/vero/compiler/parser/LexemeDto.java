package com.vero.compiler.parser;

import lombok.Data;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 22:31 2018/3/24.
 * @since vero-compiler
 */
@Data
public class LexemeDto
{

    private String content;

    private String type;


    public LexemeDto(String content, String type) {
        this.content = content;
        this.type = type;
    }
}
