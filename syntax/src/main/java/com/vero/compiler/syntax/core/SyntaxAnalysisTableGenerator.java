package com.vero.compiler.syntax.core;


import static com.vero.compiler.syntax.core.PrintUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by XiangDe Liu on 2018/3/11.
 */
@Getter
@Slf4j
public class SyntaxAnalysisTableGenerator
{
    public List<HashMap<String, List<ActionItem>>> ACTION_TABLE = new ArrayList<>();

    public List<HashMap<String, Integer>> GOTO_TABLE = new ArrayList<>();

    public SymbolMaintainer maintainer;

    private ProgramMonitor monitor;

    public SyntaxAnalysisTableGenerator(ProgramMonitor monitor, SymbolMaintainer maintainer)
    {
        this.maintainer = maintainer;
        this.monitor = monitor;
    }

    private List<ProgramItemSet> getFamily()
    {
        return this.monitor.getFamily();
    }

    public void initGrammar(ProgramItem augmentedGrammar)
    {
        this.monitor.items(augmentedGrammar);
    }

    public SyntaxDriverInfo generate()
    {
        this.initGrammar(this.maintainer.getI0());
        this.createActionTable();
        this.createGotoTable();
        if (log.isDebugEnabled())
        {
            printItems(getFamily());
            printActionTable(getACTION_TABLE());
            printGotoTable(getGOTO_TABLE());
        }
        return new SyntaxDriverInfo(getACTION_TABLE(), getGOTO_TABLE());
    }

    public void createActionTable()
    {
        for (int i = 0; i < getFamily().size(); i++ )
        {
            HashMap<String, List<ActionItem>> table = new HashMap<>();
            ProgramItemSet itemSet = getFamily().get(i);

            // 移入
            for (String symbol : itemSet.shiftSymbol())
            {
                List<ActionItem> actList = new ArrayList<>();
                ActionItem actionItem = new ActionItem(itemSet.getStatus(symbol), ActionType.SHFIT);
                actList.add(actionItem);
                table.put(symbol, actList);
            }

            // 归约
            for (ProgramItem item : itemSet.reduceItems())
            {
                ActionItem actionItem;
                if (log.isDebugEnabled())
                {
                    log.debug("Reduce item right: [{}]-----size:[{}]----lookAhead:[{}]----",
                        item.getRight().get(0), item.getRight().size(), item.lookAhead);
                }
                if (item.left.equals(this.maintainer.getAcceptLeft()) && item.right.size() == 1
                    && item.right.get(0).equals(this.maintainer.getAcceptRight())
                    && item.lookAhead.contains("$"))
                {
                    actionItem = new ActionItem(null, ActionType.ACCEPT);
                }
                else
                {
                    actionItem = new ActionItem(item.index, ActionType.REDUCE, item.left);
                }

                for (String symbol : item.getLookAhead())
                {
                    if (table.keySet().contains(symbol))
                    {
                        List<ActionItem> actList = table.get(symbol);
                        actList.add(actionItem);
                        table.put(symbol, actList);
                    }
                    else
                    {
                        List<ActionItem> actList = new ArrayList<>();
                        actList.add(actionItem);
                        table.put(symbol, actList);
                    }
                }
            }
            ACTION_TABLE.add(table);
        }
    }

    public void createGotoTable()
    {
        for (int i = 0; i < getFamily().size(); ++i)
        {
            HashMap<String, Integer> gt = new HashMap<>();
            ProgramItemSet itemSet = getFamily().get(i);
            for (String symbol : itemSet.gotoSymbol())
            {
                gt.put(symbol, itemSet.getStatus(symbol));
            }
            GOTO_TABLE.add(gt);
        }
    }
}
