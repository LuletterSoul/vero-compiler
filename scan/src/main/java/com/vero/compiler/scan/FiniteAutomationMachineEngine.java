package com.vero.compiler.scan;


import lombok.Getter;
import lombok.Setter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 11:10 2018/3/16.
 * @since vero-compiler
 */

@Getter
@Setter
public class FiniteAutomationMachineEngine
{
    private Integer[] charClassTable;

    private Integer currentStateIndex;

    private Integer[][] transitionTable;

    public FiniteAutomationMachineEngine(Integer[] charClassTable, Integer[][] transitionTable)
    {
        this.charClassTable = charClassTable;
        this.currentStateIndex = 1;
        this.transitionTable = transitionTable;
    }

    public void resetMachineState()
    {
        this.currentStateIndex = 1;
    }

    public void input(char c)
    {
        Integer[] transitions = getTransitionTable()[getCurrentStateIndex()];
        // 寻找下一个状态
        Integer charClass = getCharClassTable()[c];
        Integer nextState = transitions[charClass];
        // 转移到下一个状态
        setCurrentStateIndex(nextState);
    }

    public void inputString(String string)
    {
        char[] chars = string.toCharArray();
        for (char aChar : chars)
        {
            this.input(aChar);
        }
    }
}
