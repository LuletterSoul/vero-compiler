package com.vero.compiler.lexer.compress;


import java.util.*;

import com.vero.compiler.lexer.generator.DFAEdge;
import com.vero.compiler.lexer.generator.DFAModel;
import com.vero.compiler.lexer.generator.DFAState;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:57 2018/3/11.
 * @since vero-compiler
 */

@Getter
@Slf4j
@SuppressWarnings("unchecked")
public class CompressedTransitionTable
{
    /**
     * 字符到等价类的查询表
     */
    private Integer[] charClassTable;

    /**
     * 状态转换表的管理器
     */
    private CompactCharSetManager compactCharSetManager;

    /**
     * DFA状态的压缩表
     */
    private Integer[][] realCompressedTransitionTable;

    private List<DFAState> dfaStates;

    private Map<Integer[], Integer> stateSetDict;

    private CompressedTransitionTable(DFAModel dfaModel)
    {
        this.charClassTable = new Integer[65536];

        for (int i = 0; i < this.charClassTable.length; i++ )
        {
            charClassTable[i] = 0;
        }

        this.stateSetDict = new HashMap<>();

        this.dfaStates = dfaModel.getDfaStates();

        this.compactCharSetManager = dfaModel.getCompactCharSetManager();

        this.realCompressedTransitionTable = new Integer[dfaStates.size()][];

        this.compress();

    }

    public static CompressedTransitionTable compress(DFAModel dfa)
    {
        if (dfa == null)
        {
            return null;
        }
        return new CompressedTransitionTable(dfa);
    }

    private void compress()
    {
        Map<Integer, List<Integer>> classIndexesDic = new HashMap<>();
        List<Integer[]> transitionColumnTable = new ArrayList<>();
        this.composeEdgesByClassIndex(classIndexesDic);
        // NFA压缩后的等价类集
        HashSet[] compactCharMapTable = getCompactCharSetManager().createCompactCharMapTable();
        generateChars2DFAEquivalenceClass(classIndexesDic, transitionColumnTable,
            compactCharMapTable);
        // 将所有非法字符投影到到DFA非法下标
        buildInValidCharsNavigation(transitionColumnTable, compactCharMapTable[0]);
        generationRealCompressedTable(transitionColumnTable);
    }

    private void generationRealCompressedTable(List<Integer[]> transitionColumnTable)
    {
        // 初始化二位矩陣
        for (int i = 0; i < realCompressedTransitionTable.length; i++ )
        {
            realCompressedTransitionTable[i] = new Integer[transitionColumnTable.size()];
            for (int j = 0; j < realCompressedTransitionTable[i].length; j++ )
            {
                realCompressedTransitionTable[i][j] = 0;
            }
        }
        for (int i = 0; i < realCompressedTransitionTable.length; i++ )
        {
            Integer[] newColumn = new Integer[transitionColumnTable.size()];
            for (int dfaClass = 0; dfaClass < transitionColumnTable.size(); dfaClass++ )
            {
                Integer[] column = transitionColumnTable.get(dfaClass);
                newColumn[dfaClass] = column[i];
            }
            realCompressedTransitionTable[i] = newColumn;
        }
    }

    /**
     * 获取DFA的所有边并按当前的等价类重新编排映射
     *
     * @param classIndexesDic
     * @return
     */
    private void composeEdgesByClassIndex(Map<Integer, List<Integer>> classIndexesDic)
    {
        for (int i = 0; i < getDfaStates().size(); i++ )
        {
            List<DFAEdge> dfaEdges = getDfaStates().get(i).getOutEdges();
            // 抽取每个NFA等价类指向的所有目标状态下标
            for (DFAEdge edge : dfaEdges)
            {
                List<Integer> dfaStateIndexes = classIndexesDic.computeIfAbsent(edge.getSymbol(),
                    k -> new ArrayList<>());
                dfaStateIndexes.add(edge.getTargetState().getIndex());
            }
        }
    }

    private void generateChars2DFAEquivalenceClass(Map<Integer, List<Integer>> classIndexesDic,
                                                   List<Integer[]> transitionColumnTable,
                                                   HashSet[] compactCharMapTable)
    {
        // 等价类的最大下标
        Integer minClassIndex = getCompactCharSetManager().getMinClassIndex();
        Integer maxClassIndex = getCompactCharSetManager().getMaxClassIndex();
        for (int i = minClassIndex; i <= maxClassIndex; i++ )
        {
            // 选出指向当前等价类的所有字符集
            Integer[] columnSequence = columnSequenceListToArray(classIndexesDic, i);
            Set<Integer[]> nfaCompressedIndexSet = getStateSetDict().keySet();
            Integer signedNFAClass = containsDFAClass2NFAClassMapping(columnSequence,
                nfaCompressedIndexSet);
            // 已经存在映射
            if (signedNFAClass != null)
            {
                mapCharToDFAEquivalenceClassIndex(compactCharMapTable[i], signedNFAClass);
            }
            else
            {
                // 新增一个DFA等价类下标
                Integer nextIndex = transitionColumnTable.size();
                transitionColumnTable.add(columnSequence);
                getStateSetDict().put(columnSequence, nextIndex);
                mapCharToDFAEquivalenceClassIndex(compactCharMapTable[i], nextIndex);
            }
        }
    }

    private Integer[] columnSequenceListToArray(Map<Integer, List<Integer>> classIndexesDic, int i)
    {
        List<Integer> columnSequenceSet = classIndexesDic.get(i);
        Integer[] columnSequence = new Integer[columnSequenceSet.size()];
        Iterator<Integer> iterator = columnSequenceSet.iterator();
        int index = 0;
        while (iterator.hasNext())
        {
            columnSequence[index++ ] = iterator.next();
        }
        return columnSequence;
    }

    private void buildInValidCharsNavigation(List<Integer[]> transitionColumnTable,
                                             HashSet hashSet)
    {

        Integer[] invalidColumn = new Integer[getDfaStates().size()];
        for (int i = 0; i < invalidColumn.length; i++ )
        {
            invalidColumn[i] = 0;
        }
        Integer invalidIndex = transitionColumnTable.size();
        transitionColumnTable.add(invalidColumn);
        mapCharToDFAEquivalenceClassIndex(hashSet, invalidIndex);
    }

    /**
     * 将字符映射到DFA等价类
     * 
     * @param charSet
     * @param index
     */
    private void mapCharToDFAEquivalenceClassIndex(HashSet charSet, Integer index)
    {
        charSet.forEach(c -> {
            char c2 = (char)c;
            getCharClassTable()[(int)c2] = index;
        });
    }

    private Integer containsDFAClass2NFAClassMapping(Integer[] columnSequence,
                                                     Set<Integer[]> nfaCompressedIndexSet)
    {
        if (columnSequence == null)
        {
            return null;
        }
        Integer[] target;
        for (Integer[] nfaIndex : nfaCompressedIndexSet)
        {
            target = nfaIndex;
            StringBuilder stringBuilder1 = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();
            for (Integer stateIndex : nfaIndex)
            {
                stringBuilder1.append(stateIndex).append(",");
            }
            for (Integer column : columnSequence)
            {
                stringBuilder2.append(column).append(',');
            }
            if (stringBuilder1.toString().equals(stringBuilder2.toString()))
            {
                return getStateSetDict().get(target);
            }
        }
        return null;
    }
}
