package com.vero.compiler.syntax;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 项目集合
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 0:30 2018/3/22.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class ProgramItem
{
    private String leftPart;

    /**
     * 项目集右部分
     */
    private List<String> rightParts;

    /**
     * 前跟字符集
     */
    private List<String> aheadChars;

    /**
     * 圆点指针
     */
    private Integer _dotPointer;

    /**
     * 在项目集族中的索引
     */
    private Integer index;

}
