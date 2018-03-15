package com.vero.compiler.scan.generator;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

public class NFAEdge extends Edge
{

    public NFAEdge(int symbol, State targetState)
    {
        super(symbol, targetState);
    }

    public NFAEdge(State targetState)
    {
        super(targetState);
    }

    @Override
    public String toString()
    {
        return "NFAEdge{" + "symbol=" + symbol + ", targetState=" + targetState.getIndex() + '}';
    }
}
