package com.vero.compiler.scan.generator;


import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */


public class NFAEdge extends Edge
{
    public NFAEdge(State targetState) {
        super(targetState);
    }
}
