package com.vero.compiler.scan.lexer;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.exception.TokenDefinitionsNotFoundException;
import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.expression.StringLiteralExpression;
import com.vero.compiler.scan.token.Token;
import com.vero.compiler.scan.token.TokenInfo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 这个类相当于一个语言的字典，保存了所有单词的定义， 同时在内部进行正则表达式到DFA的转换等工作。
 * 
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:09 2018/3/11.
 * @since vero-compiler
 */

@Data
@Slf4j
public class Lexicon
{
    // 默认的词法分析器,一般情况下只有一个;
    private Lexer defaultLexer;

    private List<Lexer> lexerStates;

    private List<TokenInfo> tokenList;

    private CompactCharSetManager compactCharSetManager;

    public Lexicon()
    {
        tokenList = new ArrayList<>();
        lexerStates = new ArrayList<>();
        defaultLexer = new Lexer(this, 0);
        lexerStates.add(defaultLexer);
    }

    public TokenInfo addToken(RegularExpression definition, Lexer state, int indexInState,
                              String description)
    {
        int index = tokenList.size();
        Token tag = new Token(index, description, state.getIndex());
        TokenInfo token = new TokenInfo(definition, this, state, tag);
        tokenList.add(token);
        return token;
    }

    /**
     * 定义一个具备新状态的词法分析器
     * 
     * @param baseLexer
     * @return
     */
    public Lexer defineLexer(Lexer baseLexer)
    {
        Integer index = getLexerStates().size();
        Lexer newState = new Lexer(this, index, baseLexer);
        getLexerStates().add(newState);
        baseLexer.getChildren().add(newState);
        return newState;
    }

    /**
     * {@link com.vero.compiler.scan.expression.SymbolExpression},
     * {@link com.vero.compiler.scan.expression.StringLiteralExpression}
     * {@link StringLiteralExpression#getCompressibleCharSets()}返回空, 因为这两种类型的正则表达式没用等价转换的字符集
     * {@link StringLiteralExpression#getUnCompressibleCharSet()}返回的是不可被压缩的字符集
     * <code>""IamString"""</code>表示字面值常量<code>"s"</code>表示的标识符
     * {@link com.vero.compiler.scan.expression.AlternationExpression} {@link }
     * StringLiteralExpression, AlternationCharSet的GetCompactableCharsets
     * 和GetUncompactableCharset方法返回值的， 其中前两个的GetCompactableCharset返回空，
     * AlternationCharSet类的GetCompactableCharSet返回包含所有可选字符的一个hashset。
     * GetCompactable方法主要是用来对Alternation这种类型的charset进行压缩用的，
     * 因为如果不压缩的话，在生成的NFA中，可能会包含太多的edge，占用大量的内存，而是用此压缩方法之后，
     * 在DFAEdge变可以用压缩后字符所对应的int值来代替真正的char存储在edge中int
     * {@link CompactCharSetManager#getCompactClass(Character)} 此时变可以用compactClass来代替真正的char。
     * 类似求子集的方法 当然具体的压缩算法在lexicon的方法public CompactCharSetManager
     * CreateCompactCharSetManager()中，这里是用了一个类似求子集的
     * 
     * @return compactCharSetManager
     */
    public CompactCharSetManager createCompactCharSetManager()
    {
        List<TokenInfo> tokenInfos = getTokenList();

        if (tokenInfos.isEmpty())
        {
            throw new TokenDefinitionsNotFoundException(
                "Current lexcion context's token info is empty.Required base definition about lexer.");
        }

        // 可以被压缩的字符集
        HashSet<Character> compressibleCharSet = new HashSet<>();
        // 不可被压缩的字符集
        HashSet<Character> unCompressibleCharSet = new HashSet<>();
        List<HashSet> compressibleCharSets = new ArrayList<>();
        // 将分散在每个正则表达式的字符集集合起来
        tokenInfos.forEach(t -> {
            List<HashSet> cs = t.getDefinition().getCompressibleCharSets();
            HashSet<Character> ucs = t.getDefinition().getUnCompressibleCharSet();
            compressibleCharSets.addAll(cs);
            unCompressibleCharSet.addAll(ucs);
        });
        compressibleCharSets.forEach(compressibleCharSet::addAll);
        // 排除所有不可压缩的字符集
        compressibleCharSet.removeAll(unCompressibleCharSet);
        log.debug("When remove un-compressible set:", compressibleCharSet.toString());
        Map<HashSet<Integer>, Integer> compactClassDict = new HashMap<>();
        AtomicReference<Integer> compactCharIndex = new AtomicReference<>(1);
        // 字符到等价类的映射表
        Integer[] compactClassTable = new Integer[Character.MAX_VALUE + 1];
        for (int i = 0; i < compactClassTable.length; i++ )
        {
            compactClassTable[i] = 0;
        }
        unCompressibleCharSet.forEach(ucs -> {
            Integer index = compactCharIndex.getAndSet(compactCharIndex.get() + 1);
            // 将该字符的数值映射到新的等价类下标index
            compactClassTable[ucs] = index;
            if (log.isDebugEnabled())
            {
                log.debug("Mapping:[{}] -------->[{}]", ucs, index);
            }
        });
        log.debug("Current class table from un-compressible char set:",
            Arrays.toString(compactClassTable));
        for (Character cs : compressibleCharSet) {
            // 遍历所以正则表达式的字符集,搜索每个字符集中可进行压缩字符构建等价类
            HashSet<Integer> setOfCharset = new HashSet<>();
            for (int i = 0; i < compressibleCharSets.size(); i++ )
            {
                HashSet set = compressibleCharSets.get(i);
                if (set.contains(cs))
                {
                    setOfCharset.add(i);
                }
            }
            // 若已经存在对应的等价类,将其映射到新信标Index 如{'a','b','c'} ——》1表示等价的状态{'a','b','c'}映射到新别名状态1
            HashSet<Integer> target = isContainSetOfChar(compactClassDict, setOfCharset);
            if (target != null)
            {
                Integer index = compactClassDict.get(target);
                compactClassTable[cs] = index;
                log.debug("Mapping:['{}'] -------->[{}]", cs, index);
            }
            // 如不存在,则标记为新的状态
            else
            {
                Integer index = compactCharIndex.getAndSet(compactCharIndex.get() + 1);
                compactClassDict.put(setOfCharset, index);
                if (log.isDebugEnabled())
                {
                    StringBuilder stringBuilder = new StringBuilder("[");
                    setOfCharset.forEach(c -> stringBuilder.append(c).append(","));
                    stringBuilder.append("]");
                    log.debug("New char class set:", stringBuilder.toString());
                }
                compactClassTable[cs] = index;
                log.debug("Mapping:['{}'] -------->[{}]", cs, index);
            }
        }
        this.compactCharSetManager = new CompactCharSetManager(compactClassTable,
            compactCharIndex.get());
        return this.compactCharSetManager;
    }

    private HashSet<Integer> isContainSetOfChar(Map<HashSet<Integer>, Integer> compactClassDict,
                                                HashSet<Integer> setOfCharset)
    {
        Set<HashSet<Integer>> compactClassSet = compactClassDict.keySet();
        HashSet<Integer> target = null;
        for (HashSet<Integer> compact : compactClassSet)
        {
            target = compact;
            Integer targetElementTimes = 0;
            for (Integer c : compact)
            {

                for (Integer now : setOfCharset)
                {
                    if (now.equals(c))
                    {
                        ++targetElementTimes;
                    }
                }
            }
            if (targetElementTimes.equals(setOfCharset.size()))
            {
                return target;
            }
        }
        return null;
    }

    private boolean isContainedChar(Character cs, HashSet set)
    {
        boolean isContained = false;
        for (Object object : set)
        {
            if (object instanceof Character)
            {
                Character c = (Character)object;
                if (c.equals(cs))
                {
                    isContained = true;
                    break;
                }
            }
        }

        return isContained;
    }

    public CompactCharSetManager getCompactCharSetManager()
    {
        if (this.compactCharSetManager == null)
        {
            return createCompactCharSetManager();
        }
        return compactCharSetManager;
    }

    // public ScannerInfo createScannerInfo()
    // {
    // DFAModel dfa = DFAModel.build(this);
    // CompressedTransitionTable ctt = CompressedTransitionTable.compress(dfa);
    //
    // return new ScannerInfo(ctt.getCompressedTransitionTable(), ctt.getCharClassTable(),
    // dfa.getAcceptTables(),
    // tokenList.size());
    // return null;
    // }

    @Override
    public String toString()
    {
        return "Lexicon{" + "tokenList=" + tokenList + '}';
    }
}
