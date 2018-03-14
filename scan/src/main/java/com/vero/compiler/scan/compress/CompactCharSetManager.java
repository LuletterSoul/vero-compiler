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
    /**
     * 等价类的最大下标
     */
    private int maxClassIndex;

    /**
     * 等价类的最小下标
     *
     */
    private int minClassIndex = 1;

    private Integer[] compactCharTable;


    public CompactCharSetManager(Integer[] compactCharTable,int maxClassIndex) {
        this.maxClassIndex = maxClassIndex;
        this.compactCharTable = compactCharTable;
    }

    /***
     * 获取该字符对应的等价类
     * @param character 字符
     * @return 等价类
     */
    public Integer getCompactClass(Character character)
    {
        return getCompactCharTable()[(Integer.valueOf(character))];
    }

    /**
     * 判断该字符在等价类表中是否存在映射
     * @param character 字符
     * @return 是否存在
     */
    public boolean hasCompactClass(Character character) {
        return getCompactClass(character) >= getMinClassIndex();
    }

    /**
     * 等价类到字符集的映射
     * 即反向求出等价类中含有所有字符集
     * @return 等价类到字符集的映射集
     */
    public HashSet[] createCompactCharMapTable()
    {
        HashSet[] result = new HashSet[getMaxClassIndex()+1];
        for (int i = 0; i <= getMaxClassIndex(); i++)
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
