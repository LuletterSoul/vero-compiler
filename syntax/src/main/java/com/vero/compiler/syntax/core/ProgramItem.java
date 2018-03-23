package com.vero.compiler.syntax.core;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by XiangDe Liu on 2018/3/7.
 */
@Getter
public class ProgramItem
{
    private ProgramMonitor monitor;

    public String left;

    public List<String> right;

    public List<String> lookAhead;

    public int dot;

    public int index;

//    public ProgramItem(String left, List<String> right, int dot, List<String> lookAhead, int index)
//    {
//        this.right = right;
//        this.left = left;
//        this.dot = dot;
//        this.lookAhead = lookAhead;
//        this.index = index;
//    }

    public ProgramItem(String left, List<String> right, int dot, List<String> lookAhead, int index,
                       ProgramMonitor monitor)
    {

        this.right = right;
        this.left = left;
        this.dot = dot;
        this.lookAhead = lookAhead;
        this.index = index;
        this.monitor = monitor;
    }

    public Set<String> getNoTerminalSet()
    {
        return this.monitor.getProductionManager().getNoTerminalSet();
    }

    public void addLookAhead(List<String> lookAhead)
    {
        this.lookAhead.addAll(lookAhead);
    }

    public boolean nextIsNoTerminal()
    {
        boolean status = false;
        int indexOfNext = dot + 1;
        if (!dotAtEnd() && getNoTerminalSet().contains(right.get(indexOfNext)))
        {
            status = true;
        }
        return status;
    }

    public List<String> getLookAhead()
    {
        return new ArrayList<>(this.lookAhead);
    }

    public List<String> getBeta()
    {
        if (hasBeta())
        {
            return new ArrayList<>(right.subList(this.dot + 2, this.right.size()));
        }
        else
        {
            return null;
        }
    }

    private boolean hasBeta()
    {
        if (this.dot + 1 == this.right.size() - 1)
        {
            return false;
        }
        return true;
    }

    public String getDotRight()
    {
        return this.right.get(this.dot + 1);
    }

    public boolean dotAtEnd()
    {
        if (dot >= right.size() - 1)
        {
            return true;
        }
        return false;
    }
}
