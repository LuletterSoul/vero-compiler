package com.vero.compiler.syntax.table;


import java.util.*;

import com.vero.compiler.syntax.core.ActionItem;
import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 9:14 2018/3/23.
 * @since vero-compiler
 */

@Data
public class SyntaxDriverInfo
{

    private List<HashMap<String, List<ActionItem>>> ACTION_TABLE = new ArrayList<>();

    private List<HashMap<String, Integer>> GOTO_TABLE = new ArrayList<>();

    public SyntaxDriverInfo()
    {}

    public SyntaxDriverInfo(List<HashMap<String, List<ActionItem>>> ACTION_TABLE,
                            List<HashMap<String, Integer>> GOTO_TABLE)
    {

        this.ACTION_TABLE = ACTION_TABLE;
        this.GOTO_TABLE = GOTO_TABLE;
    }

    public AnalysisTable transfer()
    {
        Integer size = this.ACTION_TABLE.size();
        List<AnalysisTableRow> rows = new ArrayList<>();
        Set<String> terminalSet = new HashSet<>();
        Set<String> noTermianlSet = new HashSet<>();
        for (Integer i = 0; i < size; i++ )
        {
            List<ActionCell> actionCells = new ArrayList<>();
            List<GotoCell> gotoCells = new ArrayList<>();
            Map<String, ActionCell> actionCellMap = new HashMap<>();
            Map<String, GotoCell> gotoCellMap = new HashMap<>();
            HashMap<String, List<ActionItem>> actionRows = this.ACTION_TABLE.get(i);
            HashMap<String, Integer> gotoRows = this.GOTO_TABLE.get(i);
            Integer finalI = i;
            actionRows.forEach((key, actionItem) -> {
                ActionCell actionCell = new ActionCell(finalI, actionItem.get(0).getStatus(),
                    actionItem.get(0).getType(), key);
                actionCells.add(actionCell);
                terminalSet.add(key);
                actionCellMap.put(key, actionCell);
            });
            gotoRows.forEach((key, status) -> {
                GotoCell gotoCell = new GotoCell(finalI, status, key);
                gotoCells.add(gotoCell);
                noTermianlSet.add(key);
                gotoCellMap.put(key, gotoCell);
            });
            rows.add(new AnalysisTableRow(i,i, actionCells, gotoCells, actionCellMap, gotoCellMap));
        }
        List<Integer> statusSet = new ArrayList<>();
        for (Integer i = 0; i < size; i++ )
        {
            statusSet.add(i);
        }
        List<String> terminals = new ArrayList<>(terminalSet);
        List<String> noTerminals = new ArrayList<>(noTermianlSet);
        //填充所有非法字符
        rows.forEach( r-> {
            terminals.forEach(t->{
                if(!r.getActionCellMap().containsKey(t)){
                    ;
                    r.getActionCellMap().put(t, new ActionCell(r.getRowIndex(),t));
                }
            });
            noTerminals.forEach( nt ->{
                if(!r.getGotoCellMap().containsKey(nt)){
                    r.getGotoCellMap().put(nt, new GotoCell(r.getRowIndex(),nt));
                }
            });
        });
        AnalysisTable table = new AnalysisTable(statusSet, terminals, noTerminals, rows);
        return table;
    }
}
