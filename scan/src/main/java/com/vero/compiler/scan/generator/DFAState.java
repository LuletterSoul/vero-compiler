package com.vero.compiler.scan.generator;


import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * DFA状态点封装体,该类封装了一个DFA状态点
 * 以及该状态点求闭包运算后所在的闭包集合
 * 闭包集合是指状态点在施加闭包算法后NFA状态点等价统一集合
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class DFAState extends State
{
    private Integer index;

    private HashSet<Integer> nfaStateSet;

    private List<DFAEdge> outEdges;

    public DFAState()
    {
        outEdges = new ArrayList<DFAEdge>();
        nfaStateSet = new HashSet<Integer>();
    }

    public void addEdge(DFAEdge edge)
    {
        outEdges.add(edge);
    }

}
