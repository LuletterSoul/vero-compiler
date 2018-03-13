package com.vero.compiler.scan.generator;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.exception.NFAStartIndexException;
import com.vero.compiler.scan.exception.TargetIndexException;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;
import com.vero.compiler.scan.token.TokenInfo;

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

@Getter
public class DFAModel
{
    private ArrayList<Integer>[] acceptTables;

    private List<DFAState> dfaStates;

    private Lexicon lexicon;

    private NFAModel nfaModel;

    private DFAModel(Lexicon lexicon)
    {
        this.lexicon = lexicon;
        this.dfaStates = new ArrayList<>();

        // initialize accept table
        int stateCount = lexicon.getLexerStates().size();
        this.acceptTables = new ArrayList[stateCount];
        for (int i = 0; i < stateCount; i++ )
        {
            acceptTables[i] = new ArrayList<Integer>();
        }
    }

    private CompactCharSetManager compactCharSetManager;

    private List<Integer> appendEosToken(List<Integer> list)
    {
        list.add(lexicon.getTokenList().size());
        return list;
    }

    public static DFAModel build(Lexicon lexicon)
    {
        if (lexicon == null)
        {
            return null;
        }
        DFAModel newDFA = new DFAModel(lexicon);
        newDFA.convertLexcionToNFA();
        newDFA.convertNFAToDFA();
        return newDFA;
    }

    private void convertLexcionToNFA()
    {
        // Compact transition char set
        this.compactCharSetManager = lexicon.createCompactCharSetManager();
        NFAConverter converter = new NFAConverter(compactCharSetManager);
        NFAState entryState = new NFAState();
        NFAModel lexerNFA = new NFAModel();

        lexerNFA.addState(entryState);
        lexicon.getTokenList().forEach(t -> {
            NFAModel tokenNFA = t.createFiniteAutomationModel(converter);
            entryState.addEdge(tokenNFA.getEntryEdge());
            lexerNFA.addStates(tokenNFA.getStates());
        });
        lexerNFA.setEntryEdge(new NFAEdge(entryState));
        nfaModel = lexerNFA;
    }

    private void setAcceptState(int lexerStateIndex, int dfaStateIndex, int tokenIndex)
    {
        getAcceptTables()[lexerStateIndex].set(dfaStateIndex, tokenIndex);
    }

    private void addDFAState(DFAState state)
    {
        getDfaStates().add(state);
        state.setIndex(getDfaStates().size() - 1);
        for (int i = 0; i < getAcceptTables().length; i++ )
        {
            getAcceptTables()[i].add(-1);
        }

        List<TokenInfo> tokens = getLexicon().getTokenList();
        List<Lexer> lexerStates = getLexicon().getLexerStates();
        // check accept states
        HashSet<Integer> nfaStateSet = state.getNfaStateSet();

        List<TokenInfo> candidates = new ArrayList<>();
        for (int i = 0; i < nfaStateSet.size(); i++ )
        {
            Integer tokenIndex = getNfaModel().getStates().get(i).getIndex();
            if (tokenIndex >= 0)
            {
                candidates.add(tokens.get(tokenIndex));
            }
        }
        candidates.sort((o1, o2) -> {
            if (o1.getTagIndex() >= o2.getTagIndex())
            {
                return 1;
            }
            if (o1.getTagIndex().equals(o2.getTagIndex()))
            {
                return 0;
            }
            else
            {
                return -1;
            }
        });

        //根据token所在的词法分析器将Token进行分类
        Map<Integer, List<TokenInfo>> acceptStates = candidates.stream().collect(
            Collectors.groupingBy(TokenInfo::getLexerStateIndex));

        /**
         * 根据分析器的层级将token info的信息排序
         */
        Map<Integer, List<TokenInfo>> sortAcceptStates = new TreeMap<>((o1, o2) -> {
            if (lexerStates.get(o1).getLevel() > lexerStates.get(o2).getLevel())
            {
                return 1;
            }
            else if (lexerStates.get(o1).getLevel().equals(lexerStates.get(o2).getLevel()))
            {
                return 0;
            }
            else
                return -1;
        });
        //排序
        sortAcceptStates.putAll(acceptStates);

        if (sortAcceptStates.size() > 0)
        {
            Queue<Lexer> stateTreeQueue = new LinkedList<>();
            for (Map.Entry<Integer, List<TokenInfo>> acceptEntry : sortAcceptStates.entrySet())
            {
                Integer acceptTokenIndex = acceptEntry.getValue().get(0).getTagIndex();
                stateTreeQueue.clear();
                stateTreeQueue.add(lexerStates.get(acceptEntry.getKey()));
                while (stateTreeQueue.size() > 0)
                {
                    Lexer currentLexerState = stateTreeQueue.poll();
                    stateTreeQueue.addAll(currentLexerState.getChildren());
                    //生成接受态的表格
                    this.setAcceptState(currentLexerState.getIndex(), state.getIndex(),
                        acceptTokenIndex);
                }
            }
        }
    }

    private void convertNFAToDFA()
    {
        DFAState state0 = new DFAState();
        addDFAState(state0);
        DFAState preState1 = new DFAState();
        State state = getNfaModel().getEntryEdge().getTargetState();
        Integer nfaStartIndex = state.getIndex();
        preState1.getNfaStateSet().add(nfaStartIndex);
        addDFAState(getClosure(preState1));
        if (nfaStartIndex < 0)
        {
            throw new NFAStartIndexException("Nfa start index could not be less zero.");
        }
        Integer p = 1, j = 0;
        DFAState[] newStates = new DFAState[getCompactCharSetManager().getMaxClassIndex() + 1];
        while (j < p)
        {
            DFAState sourceState = getDfaStates().get(j);
            DFAState dfaState = getDfaStates().get(j);
            Integer minIndex = getCompactCharSetManager().getMinClassIndex();
            Integer maxIndex = getCompactCharSetManager().getMaxClassIndex();
            for (int i = minIndex; i <= maxIndex; i++ )
            {
                newStates[i] = getDFAState(sourceState, i);
            }
            Integer minClassIndex = getCompactCharSetManager().getMinClassIndex();
            Integer maxClassIndex = getCompactCharSetManager().getMaxClassIndex();
            for (int symbol = minClassIndex; symbol <= maxClassIndex; symbol++ )
            {
                DFAState e = newStates[symbol];
                boolean isSetExist = false;
                for (int i = 0; i <= p; i++ )
                {
                    HashSet<Integer> dfaStates = e.getNfaStateSet();
                    HashSet<Integer> currentDfaStates = getDfaStates().get(i).getNfaStateSet();
                    AtomicBoolean isTheSame = new AtomicBoolean(true);
                    dfaStates.forEach(d -> currentDfaStates.forEach(c -> {
                        if (!c.equals(d))
                        {
                            isTheSame.set(false);
                        }
                    }));
                    if (isTheSame.get())
                    {
                        DFAEdge newEdge = new DFAEdge(symbol, getDfaStates().get(i));
                        sourceState.addEdge(newEdge);
                        isSetExist = true;
                    }
                    if (!isSetExist)
                    {
                        p += 1;
                        addDFAState(e);
                        DFAEdge newEdge = new DFAEdge(symbol, e);
                        sourceState.addEdge(newEdge);
                    }
                }
            }
        }
    }

    private DFAState getDFAState(DFAState start, int symbol)
    {
        DFAState target = new DFAState();
        List<NFAState> dfaStates = getNfaModel().getStates();
        start.getNfaStateSet().forEach(s -> {
            NFAState nfaState = dfaStates.get(s);
            List<NFAEdge> outEdges = nfaState.getOutEdges();
            for (NFAEdge edge : outEdges)
            {
                if (!edge.isEmpty() && symbol == edge.getSymbol())
                {
                    Integer targetIndex = edge.getTargetState().getIndex();
                    if (targetIndex < 0)
                    {
                        throw new TargetIndexException(("Target lexerState index could not be null"));
                    }
                    target.getNfaStateSet().add(targetIndex);
                }
            }
        });
        return getClosure(target);
    }

    //NFA转换成DFA的第一步求闭包：子集构造算法
    //一般算法的起点是输入State Index为0的DFA状态点
    private DFAState getClosure(DFAState state)
    {
        //新建一个闭包状态集合
        DFAState closure = new DFAState();
        List<NFAState> nfaStates = getNfaModel().getStates();
        //将当前DFA状态点等价的NFA状态点加入到闭包中去
        closure.getNfaStateSet().addAll(state.getNfaStateSet());
//        AtomicBoolean changed = new AtomicBoolean(false);
        //准备队列
        Queue<Integer> currentStateIndexQueue = new LinkedList<>();
        currentStateIndexQueue.addAll(closure.getNfaStateSet());
        //广度优先遍历求最大空符可到达的NFA状态点
        while (!currentStateIndexQueue.isEmpty()) {
            //取出一个NFA的状态点下标
            NFAState currentNFAState = nfaStates.get(currentStateIndexQueue.poll());
            List<NFAEdge> outEdges = currentNFAState.getOutEdges();
            outEdges.forEach( e ->{
                //如果点当前边是空符的指向边
                if (e.isEmpty()) {
                    NFAState targetState = (NFAState) e.getTargetState();
                    Integer targetIndex = targetState.getIndex();
                    if (targetIndex < 0) {
                        throw new TargetIndexException("Target index could not be null.");
                    }
                    //加入到闭包子集
                    closure.getNfaStateSet().add(targetIndex);
                    //加入到遍历队列
                    currentStateIndexQueue.add(targetIndex);
                }
            });
        }
//        while (changed.get())
//        {
//            HashSet<Integer> lastStateSet = closure.getNfaStateSet();
//            lastStateSet.forEach(lss -> {
//                NFAState nfaState = nfaStates.get(lss);
//                List<NFAEdge> outEdges = nfaState.getOutEdges();
//                for (NFAEdge edge : outEdges)
//                {
//                    if (edge.isEmpty())
//                    {
//                        NFAState target = (NFAState)edge.getTargetState();
//                        Integer targetIndex = target.getIndex();
//                        if (targetIndex < 0)
//                        {
//                            throw new TargetIndexException("Target index could not be null.");
//                        }
//                        changed.set(closure.getNfaStateSet().add(targetIndex) || changed.get());
//                    }
//                }
//            });
//
//        }
        return closure;
    }

}