package com.vero.compiler.scan.generator;

import lombok.Data;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:12 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class Edge
{
    protected int symbol = 0;

    protected State targetState;

    public Edge(int symbol, State targetState) {
        this.symbol = symbol;
        this.targetState = targetState;
    }

    public Edge(State targetState) {
        this.targetState = targetState;
    }

    public boolean isEmpty()
    {
        return this.symbol == 0;
    }

}
