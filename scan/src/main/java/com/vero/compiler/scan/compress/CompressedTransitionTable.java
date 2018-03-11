//package com.vero.compiler.scan.compress;
//
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.vero.compiler.scan.generator.DFAModel;
//import com.vero.compiler.scan.generator.DFAState;
//
//
///**
// * @author XiangDe Liu qq313700046@icloud.com .
// * @version 1.5 created in 12:57 2018/3/11.
// * @since vero-compiler
// */
//
//public class CompressedTransitionTable
//{
//    private Integer[] charClassTable;
//
//    private CompactCharSetManager compactCharSetManager;
//
//    private Integer[][] compressedTransitionTable;
//
//    private List<DFAState> DFAStates;
//
//    private Map<String, Integer> stateSetDict;
//
//    private CompressedTransitionTable(DFAModel dfaModel)
//    {
//        charClassTable = new Integer[65536];
//
//        stateSetDict = new HashMap<String, Integer>();
//
//        DFAStates = ;
//
//        compactCharSetManager = dfaModel;
//
//        compressedTransitionTable = new int[DFAStates.Count][];
//    }
//
//    public int[][] TransitionTable
//    {
//        get { return compressedTransitionTable; }
//    }
//
//    public ushort[] CharClassTable
//    {
//        get { return charClassTable; }
//    }
//
//    public static CompressedTransitionTable Compress(DFAModel dfa)
//    {
//        if (dfa == null)
//        {
//            return null;
//        }
//
//        CompressedTransitionTable compressor = new CompressedTransitionTable(dfa);
//        compressor.Compress();
//
//        return compressor;
//    }
//
//    private void Compress()
//    {
//        Dictionary<int, int>[] transitionTable = new Dictionary<int, int>[DFAStates.Count];
//
//
//
//        for (int i = 0; i < DFAStates.Count; i++)
//        {
//            transitionTable[i] = new Dictionary<int, int>();
//
//            foreach (var edge in DFAStates[i].OutEdges)
//            {
//                transitionTable[i].Add(edge.Symbol, edge.TargetState.Index);
//            }
//        }
//
//        List<int[]> transitionColumnTable = new List<int[]>();
//        var compactCharMapTable = compactCharSetManager.CreateCompactCharMapTable();
//
//        //valid chars
//        for (int i = compactCharSetManager.MinClassIndex; i <= compactCharSetManager.MaxClassIndex; i++)
//        {
//            int[] columnSequence = (from row in transitionTable select row[i]).ToArray();
//            StringBuilder signatureBuilder = new StringBuilder();
//
//            foreach (var item in columnSequence)
//            {
//                signatureBuilder.Append(item);
//                signatureBuilder.Append(',');
//            }
//
//            string columnSignature = signatureBuilder.ToString();
//
//            if (stateSetDict.ContainsKey(columnSignature))
//            {
//                //already exist
//                foreach (var c in compactCharMapTable[i])
//                {
//                    charClassTable[c] = stateSetDict[columnSignature];
//                }
//            }
//            else
//            {
//                //there is at most 65536 char classes
//                ushort nextIndex = (ushort)transitionColumnTable.Count;
//
//                //a new char set
//
//                transitionColumnTable.Add(columnSequence);
//                stateSetDict[columnSignature] = nextIndex;
//
//                foreach (var c in compactCharMapTable[i])
//                {
//                    charClassTable[c] = nextIndex;
//                }
//            }
//        }
//
//        //create a char set for all invalid chars
//        //navigate them all to dfa state #0 (the invalid state)
//        int[] invalidColumn = new int[DFAStates.Count];
//        ushort invalidIndex = (ushort)transitionColumnTable.Count;
//
//        transitionColumnTable.Add(invalidColumn);
//
//        foreach (var c in compactCharMapTable[0])
//        {
//            charClassTable[c] = invalidIndex;
//        }
//
//        //generate real compressed transition table
//
//        for (int i = 0; i < DFAStates.Count; i++)
//        {
//            compressedTransitionTable[i] = (from column in transitionColumnTable select column[i]).ToArray();
//        }
//    }
//}
