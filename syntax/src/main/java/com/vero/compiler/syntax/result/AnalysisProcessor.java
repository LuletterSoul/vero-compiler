package com.vero.compiler.syntax.result;


import com.vero.compiler.syntax.core.ActionItem;
import com.vero.compiler.syntax.core.AnalysisHistory;

import java.util.List;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 10:48 2018/3/24.
 * @since vero-compiler
 */

public interface AnalysisProcessor
{
    void processInputStack(List<String> inputStack);

    void processStatusStack(List<Integer> statusStack);

    void processSymbolStack(List<String> symbolStack);

    void process(List<String> inputStack, List<Integer> statusStack,
                 List<String> symbolStack, ActionItem action);

    void reset();

    List<List<String>> getInputHistory();

    List<List<Integer>> getStatusHistory();

    List<List<String>> getSymbolHistory();

    List<AnalysisHistory> getHistories();
}
