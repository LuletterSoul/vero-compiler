package com.vero.compiler.syntax.table;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 2:20 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class AnalysisTableRow
{
    private Integer rowIndex;

    private Integer status;

    private List<ActionCell> actionCells;

    private List<GotoCell> gotoCells;

    private Map<String, ActionCell> actionCellMap;

    private Map<String, GotoCell> gotoCellMap;

}
