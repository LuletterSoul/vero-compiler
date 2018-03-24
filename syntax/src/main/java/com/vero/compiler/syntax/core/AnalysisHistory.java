package com.vero.compiler.syntax.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  11:06 2018/3/24.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
public class AnalysisHistory {

    private List<String> input;

    private List<Integer> status;

    private List<String> symbol;

    private ActionItem action;

}
