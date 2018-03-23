package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vero.compiler.lexer.core.Lexeme;

import lombok.Getter;


/**
 * Created by XiangDe Liu on 2018/3/11.
 */
@Getter
public class SyntaxParser
{
    // 输入符号,栈顶在左
    public List<String> INPUT_STACK = new ArrayList<>();

    // 状态栈,栈顶在右
    public List<Integer> STATUS_STACK = new ArrayList<>();

    // 符号栈,栈顶在右
    public List<String> SYMBOL_STACK = new ArrayList<>();

    private SyntaxDriverInfo driverInfo;

    private SymbolMaintainer maintainer;

    public SyntaxParser(SyntaxDriverInfo driverInfo, SymbolMaintainer maintainer)
    {
        this.driverInfo = driverInfo;
        this.maintainer = maintainer;
    }

    private void initStacks(List<Lexeme> lexemes)
    {
        for (Lexeme lexeme : lexemes)
        {
            this.INPUT_STACK.add(lexeme.getContent());
        }
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
        List<String> rights = this.maintainer.getRights(left).get(status);
        int len = rights.size();
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

    public void parse(List<Lexeme> lexemes)
    {
        reset();
        initStacks(lexemes);
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
                System.out.println("文法错误");
                return;
            }

            ActionItem actionItem = actionItems.get(0);
            if (actionItem.type.equals("s"))
            {
                shift(actionItem.status);
            }
            else if (actionItem.type.equals("r"))
            {
                reduce(actionItem.left, actionItem.status);
            }
            else if (actionItem.type.equals("acc"))
            {
                System.out.println("文法正确");
                return;
            }
        }
    }

    private void reset()
    {
        this.INPUT_STACK.clear();
        this.STATUS_STACK.clear();
        this.SYMBOL_STACK.clear();
    }
}
