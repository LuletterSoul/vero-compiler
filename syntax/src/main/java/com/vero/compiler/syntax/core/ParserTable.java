package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by XiangDe Liu on 2018/3/11.
 */
public class ParserTable
{
    public static ArrayList<HashMap<String, ArrayList<ActionItem>>> ACTION_TABLE = new ArrayList<>();

    public static ArrayList<HashMap<String, Integer>> GOTO_TABLE = new ArrayList<>();

    public static void initGrammar(ProgramItem augmentedGrammar)
    {
        ProgramMonitor.items(augmentedGrammar);
    }

    public static void createAT()
    {
        for (int i = 0; i < ProgramMonitor.setFamily.size(); i++ )
        {
            HashMap<String, ArrayList<ActionItem>> table = new HashMap<>();
            ProgramItemSet itemSet = ProgramMonitor.setFamily.get(i);

            // 移入
            for (String symbol : itemSet.shiftSymbol())
            {
                ArrayList<ActionItem> actList = new ArrayList<>();
                ActionItem actionItem = new ActionItem(itemSet.getStatus(symbol), "s");
                actList.add(actionItem);
                table.put(symbol, actList);
            }

            // 归约
            for (ProgramItem item : itemSet.reduceItems())
            {
                ActionItem actionItem;
                if (item.left.equals(Utils.ACC_LEFT) && item.right.size() == 1
                    && item.right.get(0).equals(Utils.ACC_RIGHT) && item.lookAhead.contains("$"))
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
                        ArrayList<ActionItem> actList = table.get(symbol);
                        actList.add(actionItem);
                        table.put(symbol, actList);
                    }
                    else
                    {
                        ArrayList<ActionItem> actList = new ArrayList<>();
                        actList.add(actionItem);
                        table.put(symbol, actList);
                    }
                }
            }
            ACTION_TABLE.add(table);
        }
    }

    public static void createGT()
    {
        for (int i = 0; i < ProgramMonitor.setFamily.size(); ++i)
        {
            HashMap<String, Integer> gt = new HashMap<>();
            ProgramItemSet itemSet = ProgramMonitor.setFamily.get(i);
            for (String symbol : itemSet.gotoSymbol())
            {
                gt.put(symbol, itemSet.getStatus(symbol));
            }

            GOTO_TABLE.add(gt);
        }
    }
}
