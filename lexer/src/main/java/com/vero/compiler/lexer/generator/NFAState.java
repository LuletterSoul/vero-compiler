package com.vero.compiler.lexer.generator;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:50 2018/3/10.
 * @since vero-compiler
 */

@Data
public class NFAState extends State
{

    private List<NFAEdge> outEdges;

    private Integer tokenIndex = 0;

    public NFAState()
    {
        outEdges = new ArrayList<>();
    }

    public NFAState(Integer tokenIndex, Edge targetEdge)
    {
        super(tokenIndex, targetEdge);
        outEdges = new ArrayList<>();
    }

    public void addEmptyEdgeTo(NFAState targetState)
    {
        outEdges.add(new NFAEdge(targetState));
    }

    public void addEdge(NFAEdge edge)
    {
        outEdges.add(edge);
    }

    @Override
    public String toString()
    {
        return "NFAState{" + "outEdges=" + outEdges.toString() + "}";
    }
}
