package com.vero.compiler.scan.compress;


import lombok.Data;

import java.util.HashSet;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:35 2018/3/10.
 * @since vero-compiler
 */

@Data
public class CompactCharSetManager
{
    private int maxIndex;

    private int minIndex = 1;

    private Integer[] compactCharTable;

    public CompactCharSetManager(Integer[] compactCharTable,int maxIndex) {
        this.maxIndex = maxIndex;
        this.compactCharTable = compactCharTable;
    }

    public Integer getCompactClass(Character character)
    {
        return getCompactCharTable()[(Integer.valueOf(character))];
    }

    public boolean hasCompactClass(Character character) {
        return getCompactClass(character) >= getMinIndex();
    }

    public HashSet[] createCompactCharMapTable()
    {
        HashSet[] result = new HashSet[getMaxIndex()+1];
        for (int i = 0; i <= getMaxIndex(); i++)
        {
            result[i] = new HashSet<Character>();
        }

        for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++)
        {
            int index = getCompactCharTable()[i];
            result[index].add((char) i);
        }
        return result;
    }

}
