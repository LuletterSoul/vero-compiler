package com.vero.compiler.scan.generator;


import lombok.Data;


/**
 * 自动机中的转换边
 * 
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:12 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class Edge
{
    /**
     * 自动机中边的输入字符的等价类下标
     */
    protected int symbol = 0;

    protected State targetState;

    public Edge(int symbol, State targetState)
    {
        this.symbol = symbol;
        this.targetState = targetState;
    }

    public Edge(State targetState)
    {
        this.targetState = targetState;
    }

    /**
     * 如果下标为0,表示输入符号为'ε'
     * 
     * @return
     */
    public boolean isEmpty()
    {
        return this.symbol == 0;
    }

    @Override
    public String toString()
    {
        return "Edge{" + "symbol=" + symbol + ", targetState=" + targetState.getIndex() + '}';
    }
}
