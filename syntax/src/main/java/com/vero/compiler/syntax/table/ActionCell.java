package com.vero.compiler.syntax.table;


import com.vero.compiler.syntax.core.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 2:22 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class ActionCell
{
    private Integer currentStatus = -1;

    private Integer index = -1;

    private ActionType type = ActionType.INVALID;

    private String terminal;

    public ActionCell(Integer currentStatus,String terminal)
    {
        this.currentStatus = currentStatus;
        this.terminal = terminal;
    }
}
