package com.vero.compiler.scan.generator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * DFA状态点封装体,该类封装了一个DFA状态点 以及该状态点求闭包运算后所在的闭包集合 闭包集合是指状态点在施加闭包算法后NFA状态点等价统一集合
 *
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class DFAState extends State {
    private Integer index;

    /**
     * ε-closure 的闭包子集 集合中的每个元素是经过压缩后的等价类下标;
     */
    private HashSet<Integer> nfaStateIndexSet;

    private List<DFAEdge> outEdges;

    public DFAState() {
        outEdges = new ArrayList<DFAEdge>();
        nfaStateIndexSet = new HashSet<Integer>();
    }

    public void addEdge(DFAEdge edge) {
        outEdges.add(edge);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DFAState dfaState = new DFAState();
        HashSet<Integer> copyOfStateSet = new HashSet<>(getNfaStateIndexSet());
        List<DFAEdge> copyOutEdges = new ArrayList<>(getOutEdges());
        dfaState.setIndex(getIndex());
        dfaState.setNfaStateIndexSet(copyOfStateSet);
        dfaState.setOutEdges(copyOutEdges);
        return dfaState;
    }
}
