package com.vero.compiler.scan.generator;


import java.util.List;

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

    public void addState(NFAState state)
    {
        this.states.add(state);
    }

    public void addStates(List<NFAState> states)
    {
        this.states.addAll(states);
    }
}
