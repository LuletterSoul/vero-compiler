package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vero.compiler.exception.ParseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;


/**
 * Created by XiangDe Liu on 2018/3/11.
 */
@Getter
@Slf4j
public class SyntaxParser
{
    // 输入符号,栈顶在左
    private List<String> INPUT_STACK = new ArrayList<>();

    // 状态栈,栈顶在右
    private List<Integer> STATUS_STACK = new ArrayList<>();

    // 符号栈,栈顶在右
    private List<String> SYMBOL_STACK = new ArrayList<>();

    private SyntaxDriverInfo driverInfo;

    private SymbolMaintainer maintainer;

    private AnalysisProcessor processor;

    public SyntaxParser(SyntaxDriverInfo driverInfo, SymbolMaintainer maintainer,
                        AnalysisProcessor processor)
    {
        this.driverInfo = driverInfo;
        this.maintainer = maintainer;
        this.processor = processor;
    }

    private void initStacks(List<String> streams)
    {
        this.INPUT_STACK.addAll(streams);
        this.INPUT_STACK.add("$");
        this.STATUS_STACK.add(0);
    }

    private List<HashMap<String, List<ActionItem>>> actionTable()
    {
        return this.driverInfo.getACTION_TABLE();
    }

    private List<HashMap<String, Integer>> gotoTable()
    {
        return this.driverInfo.getGOTO_TABLE();
    }

    private String getLookAhead()
    {
        return INPUT_STACK.get(0);
    }

    private int getCurStatus()
    {
        return STATUS_STACK.get(STATUS_STACK.size() - 1);
    }

    private void shift(int status)
    {
        SYMBOL_STACK.add(INPUT_STACK.remove(0));
        STATUS_STACK.add(status);
    }

    private void reduce(String left, int status)
    {
        List<List<String>> rights = this.maintainer.getRights(left);
        if (rights.size() <= status) {

            log.error("Reduce Error.Parse End.");
            log.error("INPUT_STACK ＊ {}", this.INPUT_STACK);
            log.error("SYMBOL_STACK: ＊ {}", this.SYMBOL_STACK);
            throw new ParseException("Reducing Error.");
        }
        List<String> right = rights.get(status);
        int len = right.size();
        // 符号栈、状态栈pop
        for (int i = 0; i < len; i++ )
        {
            STATUS_STACK.remove(STATUS_STACK.size() - 1);
            SYMBOL_STACK.remove(SYMBOL_STACK.size() - 1);
        }

        int curStatus = getCurStatus();
        int nextStatus = gotoTable().get(curStatus).get(left);

        SYMBOL_STACK.add(left);
        STATUS_STACK.add(nextStatus);
    }

    public void parse(List<String> stream)
    {
        reset();
        initStacks(stream);
        while (true)
        {
            String lookAhead = getLookAhead();
            List<ActionItem> actionItems;
            int curStatus = getCurStatus();
            if (actionTable().get(curStatus).containsKey(lookAhead))
            {
                actionItems = actionTable().get(curStatus).get(lookAhead);
            }
            else
            {
                log.error("Wrong Grammar.");
                return;
            }

            ActionItem actionItem = actionItems.get(0);
            if (actionItem.type.equals(ActionType.SHFIT))
            {
                shift(actionItem.status);
                notifyProcessor();
            }
            else if (actionItem.type.equals(ActionType.REDUCE))
            {
                reduce(actionItem.left, actionItem.status);
                notifyProcessor();
            }
            else if (actionItem.type.equals(ActionType.ACCEPT))
            {
                log.debug("Text Accepted.");
                notifyProcessor();
                return;
            }
        }
    }

    private void notifyProcessor()
    {
        this.processor.processStatusStack(this.STATUS_STACK);
        this.processor.processInputStack(this.INPUT_STACK);
        this.processor.processSymbolStack(this.SYMBOL_STACK);
        this.processor.process(this.INPUT_STACK, this.STATUS_STACK, this.SYMBOL_STACK);
    }

    private void reset()
    {
        this.INPUT_STACK.clear();
        this.STATUS_STACK.clear();
        this.SYMBOL_STACK.clear();
    }
}
