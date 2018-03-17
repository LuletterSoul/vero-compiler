package com.vero.compiler.scan.generator;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.exception.NFAStartIndexException;
import com.vero.compiler.scan.exception.TargetIndexException;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;
import com.vero.compiler.scan.token.TokenInfo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

@Slf4j
@Getter
public class DFAModel
{
    private ArrayList[] acceptTables;

    private List<DFAState> dfaStates = new ArrayList<>();

    private List<DFAState> terminalStates;

    private Lexicon lexicon;

    private NFAModel nfaModel;

    private RegularExpressionConverter nfaConverter;

    // 0号状态点为停机状态,所有的非法输入会回到状态0
    private DFAState stopState;

    public DFAModel(Lexicon lexicon, RegularExpressionConverter nfaConverter)
    {
        this(lexicon);
        this.nfaConverter = nfaConverter;
    }

    public DFAModel(Lexicon lexicon)
    {
        this.lexicon = lexicon;
        this.dfaStates = new ArrayList<>();

        // initialize accept table
        int stateCount = lexicon.getLexerStates().size();
        this.acceptTables = new ArrayList[stateCount];
        for (int i = 0; i < stateCount; i++ )
        {
            acceptTables[i] = new ArrayList();
        }
        this.stopState = new DFAState();
        addDFAState(stopState);
    }

    private CompactCharSetManager compactCharSetManager;

    private List<Integer> appendEosToken(List<Integer> list)
    {
        list.add(lexicon.getTokenList().size());
        return list;
    }

    public static DFAModel build(Lexicon lexicon,
                                 RegularExpressionConverter regularExpressionConverter)
    {
        if (lexicon == null)
        {
            return null;
        }
        DFAModel newDFA = new DFAModel(lexicon, regularExpressionConverter);
        newDFA.convertLexcionToNFA();
        newDFA.convertNFAToDFA();
        return newDFA;
    }

    private void convertLexcionToNFA()
    {
        this.compactCharSetManager = lexicon.getCompactCharSetManager();
        // NFAConverter converter = new NFAConverter(compactCharSetManager);
        NFAState entryState = new NFAState();
        NFAModel lexerNFA = new NFAModel();
        lexerNFA.addState(entryState);
        // 使用 | 运算将所有NFA连接在一起形成全部Token的NFA
        for (TokenInfo t : lexicon.getTokenList())
        {
            NFAModel tokenNFA = t.createFiniteAutomationModel(getNfaConverter());
            // 将当前Token的入口边挂载到当前的自动机的入口点
            entryState.addEdge(tokenNFA.getEntryEdge());
            // 加入状态点(d)
            lexerNFA.addStates(tokenNFA.getStates());
        }
        // 新建一个入口边,作为整个自动机的入口
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
//        recordStateIndexMappingToToken(state);
    }

    /**
     * 记录当前状态对应哪一个Token
     * 
     * @param state
     */
    private void recordStateIndexMappingToToken(DFAState state)
    {
        // 增加一列
        for (int i = 0; i < getAcceptTables().length; i++ )
        {
            getAcceptTables()[i].add(-1);
        }
        List<TokenInfo> tokens = getLexicon().getTokenList();
        List<Lexer> lexerStates = getLexicon().getLexerStates();
        HashSet<Integer> nfaStateIndexSet = state.getNfaStateIndexSet();
        List<TokenInfo> candidates = new LinkedList<>();
        for (int i = 0; i < nfaStateIndexSet.size(); i++ )
        {
            Integer tokenIndex = getNfaModel().getStates().get(i).getTokenIndex();
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
        // 根据token所在的词法分析器将Token进行分类
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
        // 排序
        sortAcceptStates.putAll(acceptStates);

        if (sortAcceptStates.size() > 0)
        {
            Queue<Lexer> stateTreeQueue = new LinkedList<>();
            for (Map.Entry<Integer, List<TokenInfo>> acceptEntry : sortAcceptStates.entrySet())
            {
                Integer acceptTokenIndex = acceptEntry.getValue().get(0).getTagIndex();
                stateTreeQueue.clear();
                stateTreeQueue.add(lexerStates.get(acceptEntry.getKey()));
                while (!stateTreeQueue.isEmpty())
                {
                    Lexer currentLexerState = stateTreeQueue.poll();
                    stateTreeQueue.addAll(currentLexerState.getChildren());
                    // 生成接受态的表格
                    this.setAcceptState(currentLexerState.getIndex(), state.getIndex(),
                        acceptTokenIndex);
                }
            }
        }
    }

    private void convertNFAToDFA()
    {
        DFAState preState1 = new DFAState();
        // 获取入口边的目标状态
        State state = getNfaModel().getEntryEdge().getTargetState();
        Integer nfaStartIndex = state.getIndex();
        if (nfaStartIndex < 0)
        {
            throw new NFAStartIndexException("Nfa start index could not be less zero.");
        }
        // 加入到ε-closure 的闭包子集进行初始化
        preState1.setIndex(nfaStartIndex);
        preState1.getNfaStateIndexSet().add(nfaStartIndex);
        addDFAState(computeClosure(preState1));
        Integer p = 1, j = 0;
        Integer minClassIndex = getCompactCharSetManager().getMinClassIndex();
        Integer maxClassIndex = getCompactCharSetManager().getMaxClassIndex();
        DFAState[] newStates = new DFAState[maxClassIndex + 1];
        log.debug("Current equivalence class index range is [{}~{}]", minClassIndex,
            maxClassIndex);
        while (j <= p)
        {
            DFAState sourceState = getDfaStates().get(j);
            for (int symbol = minClassIndex; symbol <= maxClassIndex; symbol++ )
            {
                // symbol ---------> closure(move(sourceState,symbol))
                // 该数组建立了一条边
                newStates[symbol] = getDFAState(sourceState, symbol);
            }
            for (int symbol = minClassIndex; symbol <= maxClassIndex; symbol++ )
            {
                DFAState e = newStates[symbol];
                boolean isTheSame = false;
                // 遍历已有子集
                for (int i = 0; i <= p; i++ )
                {
                    HashSet<Integer> newNfaStateIndexSet = e.getNfaStateIndexSet();
                    HashSet<Integer> currentNfaStateIndexSet = getDfaStates().get(
                        i).getNfaStateIndexSet();
                    if (isSetExist(newNfaStateIndexSet, currentNfaStateIndexSet))
                    {
                        // 如果子集已存在,直接建立后向边
                        if (log.isDebugEnabled())
                        {
                            log.debug("Duplicated children set:{}",
                                Arrays.toString(newNfaStateIndexSet.toArray()));
                        }
                        DFAEdge newEdge = new DFAEdge(symbol, getDfaStates().get(i));
                        sourceState.addEdge(newEdge);
                        isTheSame = true;
                    }
                }
                // 不存在,将当前的DFA状态点加入,并建立边
                if (!isTheSame)
                {
                    p += 1;
                    addDFAState(e);
                    DFAEdge newEdge = new DFAEdge(symbol, e);
                    sourceState.addEdge(newEdge);
                }
            }
            j += 1;
        }
    }

    /**
     * 判断两个子集是否含有相同的元素 如果含有相同的元素则重复
     * 
     * @param newNfaStateIndexSet
     * @param currentDfaStates
     * @return
     */
    private boolean isSetExist(HashSet<Integer> newNfaStateIndexSet,
                               HashSet<Integer> currentDfaStates)
    {
        AtomicBoolean isTheSame = new AtomicBoolean(false);
        if (newNfaStateIndexSet.size() != currentDfaStates.size())
        {
            return false;
        }
        AtomicInteger targetTimes = new AtomicInteger(0);
        newNfaStateIndexSet.forEach(d -> currentDfaStates.forEach(c -> {
            if (c.equals(d))
            {
                targetTimes.getAndIncrement();
            }
        }));
        if (targetTimes.get() == newNfaStateIndexSet.size())
        {
            isTheSame.set(true);
        }
        return isTheSame.get();
    }

    /**
     * 子集构造算法,重复子集的剔除逻辑没有加入,交由调用方处理
     * 
     * @param start
     *            输入的等价状态集
     * @param equivalenceClassIndex
     *            输入等价类的下标
     * @return 新的子集
     */
    private DFAState getDFAState(DFAState start, Integer equivalenceClassIndex)
    {
        DFAState moveResult = move(start, equivalenceClassIndex);
        if (log.isDebugEnabled())
        {
            log.debug("After input [{}], {} move to <------------->{}", equivalenceClassIndex,
                Arrays.toString(start.getNfaStateIndexSet().toArray()),
                Arrays.toString(moveResult.getNfaStateIndexSet().toArray()));
        }
        return computeClosure(moveResult);
    }

    /**
     * @param start
     *            DFA状态点
     * @param equivalenceClassIndex
     *            输入经过压缩后的等价类下标
     * @return move函数产生的转移子集
     */
    private DFAState move(DFAState start, Integer equivalenceClassIndex)
    {
        DFAState finalResult = new DFAState();
        List<NFAState> nfaStates = getNfaModel().getStates();
        AtomicBoolean change = new AtomicBoolean(false);
        start.getNfaStateIndexSet().forEach(s -> {
            NFAState nfaState = nfaStates.get(s);
            List<NFAEdge> outEdges = nfaState.getOutEdges();
            for (NFAEdge edge : outEdges)
            {
                // 当前边的输入是对应的等价类下标
                if (!edge.isEmpty() && equivalenceClassIndex.equals(edge.getSymbol()))
                {
                    Integer targetIndex = edge.getTargetState().getIndex();
                    if (targetIndex < 0)
                    {
                        throw new TargetIndexException(
                            ("Target lexerState index could not be null"));
                    }
                    // 加入闭包集合,即求得move()
                    change.set(finalResult.getNfaStateIndexSet().add(targetIndex));
                }
            }
        });
        finalResult.setMoved(change.get());
        return finalResult;
    }

    private DFAState deepCloneDFAState(DFAState start)
    {
        DFAState result = new DFAState();
        try
        {
            result = (DFAState)start.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    // NFA转换成DFA的第一步求闭包：子集构造算法
    // 一般算法的起点是输入State Index为0的DFA状态点
    private DFAState computeClosure(DFAState state)
    {
        // 新建一个闭包状态集合
        DFAState closure = new DFAState();
        List<NFAState> nfaStates = getNfaModel().getStates();
        // 将当前DFA状态点等价的NFA状态点加入到闭包中去
        closure.getNfaStateIndexSet().addAll(state.getNfaStateIndexSet());
        AtomicBoolean changed = new AtomicBoolean(true);
        // 准备队列
        while (changed.get())
        {
            changed.set(false);
            Set<Integer> lastStateSet = new HashSet<>(closure.getNfaStateIndexSet());
            for (Integer nfaStateIndex : lastStateSet)
            {
                NFAState nfaState = nfaStates.get(nfaStateIndex);
                List<NFAEdge> outEdges = nfaState.getOutEdges();
                outEdges.forEach(edge -> {
                    if (edge.isEmpty())
                    {
                        int targetIndex = edge.getTargetState().getIndex();
                        changed.set(
                            closure.getNfaStateIndexSet().add(targetIndex) || changed.get());
                    }
                });
            }
        }
        // Queue<Integer> currentStateIndexQueue = new LinkedList<>();
        // currentStateIndexQueue.addAll(closure.getNfaStateIndexSet());
        // boolean[] marked = new boolean[nfaStates.size()];
        // for (int i = 0; i < marked.length; i++ )
        // {
        // marked[i] = false;
        // }
        // for (int i = 0; i < marked.length; i++ )
        // {
        // closure.getNfaStateIndexSet().forEach(c -> marked[c] = true);
        // }
        // // 广度优先遍历求最大空符可到达的NFA状态点
        // while (!currentStateIndexQueue.isEmpty())
        // {
        // // 取出一个NFA的状态点下标
        // NFAState currentNFAState = nfaStates.get(currentStateIndexQueue.poll());
        // List<NFAEdge> outEdges = currentNFAState.getOutEdges();
        // outEdges.forEach((NFAEdge e) -> {
        // // 如果点当前边是空符的指向边
        // if (e.isEmpty())
        // {
        // NFAState targetState = (NFAState)e.getTargetState();
        // Integer targetIndex = targetState.getIndex();
        // log.debug("Access target state [{}]", targetIndex);
        // if (!marked[targetIndex])
        // {
        // if (targetIndex < 0)
        // {
        // throw new TargetIndexException("Target index could not be null.");
        // }
        // // 加入到闭包子集
        // closure.getNfaStateIndexSet().add(targetIndex);
        // // 加入到遍历队列
        // currentStateIndexQueue.add(targetIndex);
        // marked[targetIndex] = true;
        // log.debug("Add target index [{}] to the closure", targetIndex);
        // }
        // else
        // {
        // log.debug("Current target index [{}] accessed.", targetIndex);
        // }
        // }
        // });
        // }
        if (log.isDebugEnabled())
        {
            log.debug("Pre closure index set:{} <----------->After {}",
                Arrays.toString(state.getNfaStateIndexSet().toArray()),
                Arrays.toString(closure.getNfaStateIndexSet().toArray()));
        }
        return closure;
    }

}