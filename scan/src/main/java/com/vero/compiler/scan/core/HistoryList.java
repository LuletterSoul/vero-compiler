package com.vero.compiler.scan.core;


import java.util.List;

import com.vero.compiler.lexer.core.Lexeme;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 15:53 2018/3/18.
 * @since vero-compiler
 */

@Getter
public class HistoryList
{

    private List<Lexeme> fullHistoryList;

    private List<Integer> valuableHistoryList;

    public HistoryList(List<Lexeme> fullHistoryList, List<Integer> valuableHistoryList)
    {
        this.fullHistoryList = fullHistoryList;
        this.valuableHistoryList = valuableHistoryList;
    }
}
