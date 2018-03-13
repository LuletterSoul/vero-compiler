package com.vero.compiler.scan.generator;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  20:52 2018/3/10.
 * @since vero-compiler
 */

public class DFAEdge extends Edge {

    public DFAEdge(int symbol, State targetState) {
        super(symbol, targetState);
    }


    public DFAEdge(State targetState) {
        super(targetState);
    }
}
