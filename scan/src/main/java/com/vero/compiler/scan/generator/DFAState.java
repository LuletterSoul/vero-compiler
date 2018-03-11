package com.vero.compiler.scan.generator;


import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

@Data
public class DFAState
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
