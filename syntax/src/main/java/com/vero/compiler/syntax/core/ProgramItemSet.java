package com.vero.compiler.syntax.core;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


/**
 * Created by XiangDe Liu on 2018/3/6.
 */
@Getter
public class ProgramItemSet
{
    public List<ProgramItem> container;

    public HashMap<String, Integer> status;

    private SymbolMaintainer maintainer;

    public ProgramItemSet(List<ProgramItem> items)
    {
        this.container = new ArrayList<>();
        this.status = new HashMap<>();
        for (ProgramItem item : items)
        {
            this.addItem(item);
        }
    }

    public ProgramItemSet(ProgramItem item)
    {
        this.container = new ArrayList<>();
        this.status = new HashMap<>();
        this.addItem(item);
    }

    public void addItem(ProgramItem item)
    {
        this.container.add(item);
    }

    // //---------------------------------
    // // 可以Goto的项
    // public List<String> getProIds(){
    // List<String> steps = new ArrayList<>();
    // int i = 0;
    // for(ProgramItem item : container) {
    // //---------------------------------
    // // 产生式右侧不为空 且 点未到末端
    // if(!item.dotAtEnd() && !item.right.get(0).equals("ε")){
    // steps.add(item.getDotRight());
    // }
    // i++;
    // }
    // return steps;
    // }

    public HashMap<String, List<Integer>> getProIds()
    {
        HashMap<String, List<Integer>> symbolMap = new HashMap<>();
        for (int i = 0; i < container.size(); ++i)
        {
            ProgramItem item = container.get(i);
            // 产生式右侧不为空 且点未到末端
            if (!item.dotAtEnd() && !item.right.get(0).equals("ε"))
            {
                String symbol = item.right.get(item.dot + 1);
                if (!symbolMap.keySet().contains(symbol))
                {
                    List<Integer> arr = new ArrayList<>();
                    arr.add(i);
                    symbolMap.put(item.right.get(item.dot + 1), arr);
                }
                else
                {
                    List<Integer> arr = symbolMap.get(symbol);
                    arr.add(i);
                    symbolMap.put(item.right.get(item.dot + 1), arr);
                }
            }
        }
        return symbolMap;
    }

    public int getStatus(String step)
    {
        return status.get(step);
    }

    public void setStatus(String step, Integer status)
    {
        this.status.put(step, status);
    }

    // --------------------------------
    // 点移动后的项
    public List<ProgramItem> statusInitialize(List<Integer> steps)
    {
        List<ProgramItem> init = new ArrayList<>();
        for (Integer i : steps)
        {
            ProgramItem item = container.get(i);
            init.add(new ProgramItem(item.left, item.right, item.dot + 1, item.getLookAhead(),
                item.index));
        }
        return init;
    }

    public List<String> shiftSymbol()
    {
        List<String> symbols = new ArrayList<>();
        for (ProgramItem item : container)
        {
            if (!item.dotAtEnd() && !item.right.get(0).equals("ε")
                && maintainer.isTerminal(item.getDotRight()))
            {
                symbols.add(item.getDotRight());
            }
        }
        return symbols;
    }

    public List<ProgramItem> reduceItems()
    {
        List<ProgramItem> items = new ArrayList<>();
        for (ProgramItem item : container)
        {
            if (item.dotAtEnd()) items.add(
                new ProgramItem(item.left, item.right, item.dot, item.getLookAhead(), item.index));
        }
        return items;
    }

    public List<String> gotoSymbol()
    {
        List<String> symbols = new ArrayList<>();
        for (ProgramItem item : container)
        {
            if (!item.dotAtEnd() && !item.right.get(0).equals("ε")
                && !maintainer.isTerminal(item.getDotRight()))
            {
                symbols.add(item.getDotRight());
            }
        }
        return symbols;
    }
}
