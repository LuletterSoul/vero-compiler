package com.vero.compiler.scan.generator;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:49 2018/3/10.
 * @since vero-compiler
 */
@Data
public class NFAModel
{
    private List<NFAState> states;

    private NFAState tailState;

    private NFAEdge entryEdge;

    public NFAModel() {
        states = new CopyOnWriteArrayList<>();
    }

    public void addState(NFAState state)
    {
        this.states.add(state);
        //记录当前状态的下标
        state.setIndex(getStates().size() - 1);
    }

    public void addStates(List<NFAState> states)
    {
        for (NFAState state : this.states) {
            addState(state);
        }
    }

    @Override
    public String toString() {
        return "NFAModel{" +
                "states=" + states +
                ", tailState=" + tailState +
                ", entryEdge=" + entryEdge +
                '}';
    }
}
