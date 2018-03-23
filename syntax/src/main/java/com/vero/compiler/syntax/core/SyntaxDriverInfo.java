package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
}
