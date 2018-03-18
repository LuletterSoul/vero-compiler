package com.vero.compiler.scan;


import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:21 2018/3/11.
 * @since vero-compiler
 */

@Getter
@Setter
public class ScannerInfo
{
    private Integer lexerState = 0;

    private SerializableScannerInfo detail;

    private Integer endOfStreamTokenIndex;

    public ScannerInfo(Integer[][] realCompressedTransitionTable, Integer[] charClassTable,
                       ArrayList[] acceptTables, int size)
    {
        this.endOfStreamTokenIndex = size;
        Integer[][] commonAcceptTable = transferToCommonAcceptTable(acceptTables);
        this.detail = new SerializableScannerInfo(commonAcceptTable, charClassTable, size,
                realCompressedTransitionTable);
    }

    private Integer[][] transferToCommonAcceptTable(ArrayList[] acceptTables)
    {
        Integer[][] commonAcceptTable = new Integer[acceptTables.length][];
        for (int i = 0; i < commonAcceptTable.length; i++ )
        {
            //增加一列作为eof
            commonAcceptTable[i] = new Integer[acceptTables[i].size() + 1];
            Integer length = commonAcceptTable[i].length;
            for (int j = 0; j < length; j++ )
            {
                if (j < length - 1) {
                    commonAcceptTable[i][j] = (Integer)acceptTables[i].get(j);
                }
                else{
                    commonAcceptTable[i][j] = getEndOfStreamTokenIndex();
                }

            }
        }
        return commonAcceptTable;
    }

    public Integer[][] getTransitionTable()
    {
        return getDetail().getTransitionTable();
    }

    public Integer[] getCharClassTable()
    {
        return getDetail().getCharClassTable();
    }

    public Integer getTokenNum()
    {
        return getDetail().getTokenNum();
    }

    public Integer getEndOfStreamState()
    {
        return getDetail().getTransitionTable().length;
    }

    public Integer getLexerStateCount()
    {
        return getDetail().getAcceptTables().length;
    }

    public Integer getCurrentLexerIndex()
    {
        return getLexerState();
    }

    public Integer getStateIndex(Integer tokenIndex)
    {
        Integer possibleIndex = -1;
        Integer acceptTableLength = this.getDetail().getAcceptTables().length;
        Integer[][] acceptTables = this.getDetail().getAcceptTables();
        for (Integer i = 0; i < acceptTableLength; i++ )
        {
            for (int j = 0; j < acceptTables[i].length; j++ )
            {
                if (acceptTables[i][j] == tokenIndex)
                {
                    possibleIndex = j;
                }
            }
            if (possibleIndex >= 0)
            {
                break;
            }
        }
        return possibleIndex;
    }

    public Integer getStateIndex(Integer tokenIndex, Integer state)
    {
        return null;
    }

    public Integer getTokenIndex(Integer state)
    {
        return getDetail().getAcceptTables()[getCurrentLexerIndex()][state];
    }

    public Integer getTokenIndex(Integer state, Integer lexerState)
    {
        return getDetail().getAcceptTables()[lexerState][state];
    }

    @Getter
    class SerializableScannerInfo implements Serializable
    {
        private Integer[][] acceptTables;

        private Integer[] charClassTable;

        private Integer tokenNum;

        private Integer[][] transitionTable;

        SerializableScannerInfo(Integer[][] acceptTables, Integer[] charClassTable,
                                Integer tokenNum, Integer[][] transitionTable)
        {
            this.acceptTables = acceptTables;
            this.charClassTable = charClassTable;
            this.tokenNum = tokenNum;
            this.transitionTable = transitionTable;
        }
    }
}
