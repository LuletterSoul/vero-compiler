package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


/**
 * 记录每一步分析过程的状态/符号/输入
 * 
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 10:52 2018/3/24.
 * @since vero-compiler
 */
@Getter
public class AnalysisProcessorImpl implements AnalysisProcessor
{
    private List<List<String>> inputHistory = new ArrayList<>();

    private List<List<Integer>> statusHistory = new ArrayList<>();

    private List<List<String>> historySymbol = new ArrayList<>();

    private List<AnalysisHistory> histories = new ArrayList<>();

    @Override
    public void processInputStack(List<String> inputStack)
    {
        List<String> history = new ArrayList<>(inputStack);
        this.inputHistory.add(history);
    }

    @Override
    public void processStatusStack(List<Integer> statusStack)
    {
        List<Integer> history = new ArrayList<>(statusStack);
        this.statusHistory.add(history);
    }

    @Override
    public void processSymbolStack(List<String> symbolStack)
    {
        List<String> history = new ArrayList<>(symbolStack);
        this.historySymbol.add(history);
    }

    @Override
    public List<AnalysisHistory> process(List<String> inputStack, List<Integer> statusStack,
                                         List<String> symbolStack)
    {
        List<String> inputHistory = new ArrayList<>(inputStack);
        List<Integer> statusHistory = new ArrayList<>(statusStack);
        List<String> symbolHistory = new ArrayList<>(symbolStack);
        AnalysisHistory history = new AnalysisHistory(inputHistory, statusHistory, symbolHistory);
        this.histories.add(history);
        return histories;
    }
}
