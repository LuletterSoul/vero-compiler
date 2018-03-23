package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.vero.compiler.syntax.core.PrintUtils.*;


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
        if (log.isDebugEnabled()) {
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
                ActionItem actionItem = new ActionItem(itemSet.getStatus(symbol), "s");
                actList.add(actionItem);
                table.put(symbol, actList);
            }

            // 归约
            for (ProgramItem item : itemSet.reduceItems())
            {
                ActionItem actionItem;
                if (item.left.equals(ExtentionSymbol.ACCEPT_LEFT) && item.right.size() == 1
                    && item.right.get(0).equals(ExtentionSymbol.ACCEPT_RIGHT)
                    && item.lookAhead.contains(ExtentionSymbol.ACCEPT_RIGHT))
                {
                    actionItem = new ActionItem(null, "acc");
                }
                else
                {
                    actionItem = new ActionItem(item.index, "r", item.left);
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
