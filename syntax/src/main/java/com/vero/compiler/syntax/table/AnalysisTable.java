package com.vero.compiler.syntax.table;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 2:37 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class AnalysisTable
{
    private List<Integer> statusSet;

    private List<String> terminals;

    private List<String> noTerminals;

    private List<AnalysisTableRow> rows;
}
