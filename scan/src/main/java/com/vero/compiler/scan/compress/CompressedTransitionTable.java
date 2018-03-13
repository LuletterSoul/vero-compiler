package com.vero.compiler.scan.compress;


import java.util.*;

import com.vero.compiler.scan.generator.DFAEdge;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.generator.DFAState;
import lombok.Getter;
import lombok.experimental.var;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:57 2018/3/11.
 * @since vero-compiler
 */

@Getter
@SuppressWarnings("unchecked")
public class CompressedTransitionTable
{
    private Integer[] charClassTable;

    private CompactCharSetManager compactCharSetManager;

    private Integer[][] compressedTransitionTable;

    private List<DFAState> dfaStates;

    private Map<String, Integer> stateSetDict;

    private CompressedTransitionTable(DFAModel dfaModel)
    {
        this.charClassTable = new Integer[65536];

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
        HashMap[] transitionTable = new HashMap[getDfaStates().size()];

        for (int i = 0; i < getDfaStates().size(); i++)
        {
            transitionTable[i] = new HashMap<Integer,Integer>();
            List<DFAEdge> dfaEdges = getDfaStates().get(i).getOutEdges();
            //将边映射到二维哈希表
            for (DFAEdge edge : dfaEdges) {
                transitionTable[i].put(edge.getSymbol(), edge.getTargetState().getIndex());
            }
        }
        List<Integer[]> transitionColumnTable = new LinkedList<>();
        //获取等价类到字符的映射集
        HashSet[] compactCharMapTable =  getCompactCharSetManager().createCompactCharMapTable();
        //等价类的最大下标
        Integer minClassIndex = getCompactCharSetManager().getMinClassIndex();
        Integer maxClassIndex = getCompactCharSetManager().getMaxClassIndex();
        for (int i = minClassIndex; i <= maxClassIndex; i++) {
            HashMap<Integer,Integer> coulumSequence = transitionTable[i];

        }

        List<int[]> transitionColumnTable = new List<int[]>();
        var compactCharMapTable = compactCharSetManager.CreateCompactCharMapTable();

        //valid chars
        for (int i = compactCharSetManager.MinClassIndex; i <= compactCharSetManager.MaxClassIndex; i++)
        {
            int[] columnSequence = (from row in transitionTable select row[i]).ToArray();
            StringBuilder signatureBuilder = new StringBuilder();

            foreach (var item in columnSequence)
            {
                signatureBuilder.Append(item);
                signatureBuilder.Append(',');
            }

            string columnSignature = signatureBuilder.ToString();

            if (stateSetDict.ContainsKey(columnSignature))
            {
                //already exist
                foreach (var c in compactCharMapTable[i])
                {
                    charClassTable[c] = stateSetDict[columnSignature];
                }
            }
            else
            {
                //there is at most 65536 char classes
                ushort nextIndex = (ushort)transitionColumnTable.Count;

                //a new char set

                transitionColumnTable.Add(columnSequence);
                stateSetDict[columnSignature] = nextIndex;

                foreach (var c in compactCharMapTable[i])
                {
                    charClassTable[c] = nextIndex;
                }
            }
        }

        //create a char set for all invalid chars
        //navigate them all to dfa lexerState #0 (the invalid lexerState)
        int[] invalidColumn = new int[dfaStates.Count];
        ushort invalidIndex = (ushort)transitionColumnTable.Count;

        transitionColumnTable.Add(invalidColumn);

        foreach (var c in compactCharMapTable[0])
        {
            charClassTable[c] = invalidIndex;
        }

        //generate real compressed transition table

        for (int i = 0; i < dfaStates.Count; i++)
        {
            compressedTransitionTable[i] = (from column in transitionColumnTable select column[i]).ToArray();
        }
    }
}
