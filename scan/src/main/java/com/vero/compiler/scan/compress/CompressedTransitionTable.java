package com.vero.compiler.scan.compress;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.vero.compiler.scan.generator.DFAEdge;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.generator.DFAState;

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
    private Integer[] charClassTable;

    private CompactCharSetManager compactCharSetManager;

    private Integer[][] compressedTransitionTable;

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

        this.compressedTransitionTable = new Integer[dfaStates.size()][];
    }

    public static CompressedTransitionTable compress(DFAModel dfa)
    {
        if (dfa == null)
        {
            return null;
        }
        CompressedTransitionTable compressor = new CompressedTransitionTable(dfa);
        compressor.compress();
        return compressor;
    }

    private void compress()
    {
        Map<Integer, List<Integer>> classIndexesDic = new HashMap<>();
        List<Integer[]> transitionColumnTable = new ArrayList<>();
        this.composeEdgesByClassIndex(classIndexesDic);
        // 获取等价类到字符的映射集
        HashSet[] compactCharMapTable = getCompactCharSetManager().createCompactCharMapTable();
        buildChars2EquivalenceClass(classIndexesDic, transitionColumnTable, compactCharMapTable);
        buildInValidCharsNavigation(transitionColumnTable, compactCharMapTable[0]);
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
            // 抽取每个等价类指向所有目标状态
            for (DFAEdge edge : dfaEdges)
            {
                List<Integer> dfaStateIndexes = classIndexesDic.computeIfAbsent(edge.getSymbol(),
                    k -> new ArrayList<>());
                dfaStateIndexes.add(edge.getTargetState().getIndex());
            }
        }
    }

    private void buildChars2EquivalenceClass(Map<Integer, List<Integer>> classIndexesDic,
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
            Set<Integer[]> mappingStateIndexes = getStateSetDict().keySet();
            // 已经存在映射
            Integer signedClass = containsCharMapping(columnSequence, mappingStateIndexes);
            if (signedClass != null)
            {
                mapCharToEquivalenceClassIndex(compactCharMapTable[i], signedClass);
            }
            else
            {
                Integer nextIndex = transitionColumnTable.size();
                transitionColumnTable.add(columnSequence);
                getStateSetDict().put(columnSequence, nextIndex);
                mapCharToEquivalenceClassIndex(compactCharMapTable[i], nextIndex);
            }
        }
    }

    private Integer[] columnSequenceListToArray(Map<Integer, List<Integer>> classIndexesDic, int i)
    {
        List<Integer> columnSequenceList = classIndexesDic.get(i);
        AtomicInteger index = new AtomicInteger();
        Integer[] columnSequence = new Integer[columnSequenceList.size()];
        columnSequenceList.parallelStream().forEach(
            c -> columnSequence[index.getAndIncrement()] = c);
        return columnSequence;
    }

    private void buildInValidCharsNavigation(List<Integer[]> transitionColumnTable,
                                             HashSet hashSet)
    {

        Integer[] invalidColumn = new Integer[getDfaStates().size()];
        Integer invalidIndex = transitionColumnTable.size();

        transitionColumnTable.add(invalidColumn);

        mapCharToEquivalenceClassIndex(hashSet, invalidIndex);
    }

    private void mapCharToEquivalenceClassIndex(HashSet hashSet, Integer index)
    {
        hashSet.forEach(c ->{
            char c2 = (char) c;
            getCharClassTable()[(int) c2] = index;
        });
    }

    private Integer containsCharMapping(Integer[] columnSequence,
                                        Set<Integer[]> mappingStateIndexes)
    {
        if (columnSequence == null)
        {
            return null;
        }
        AtomicBoolean isExist = new AtomicBoolean(true);
        Integer[] target = null;
        for (Integer[] mappingStateIndex : mappingStateIndexes)
        {
            target = mappingStateIndex;
            for (Integer stateIndex : mappingStateIndex)
            {
                for (Integer column : columnSequence)
                {
                    if (!column.equals(stateIndex))
                    {
                        isExist.set(false);
                        break;
                    }
                }
            }
        }
        if (!isExist.get())
        {
            return null;
        }
        return getStateSetDict().get(target);
    }
}
