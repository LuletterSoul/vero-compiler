package com.vero.compiler.syntax.table;


import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 2:25 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class GotoCell

{
    private Integer currentStatus;

    private Integer gotoStatus = -1;

    private String noTerminal;

    public GotoCell(Integer currentStatus, String noTerminal)
    {
        this.currentStatus = currentStatus;
        this.noTerminal = noTerminal;
    }
}
