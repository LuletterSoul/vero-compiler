package com.vero.compiler.syntax.production;


import java.util.*;

import lombok.Getter;


/**
 * 产生式管理器,一般由 {@link com.vero.compiler.syntax.reader.SyntaxGrammarParser}
 * 解析语法的文法文件输入流,从而转化得到二维哈希表中去
 * Created by XiangDe Liu on 2018/3/21.
 */
@Getter
public class GrammarProductionManager
{

    public List<String> rawProduction = new ArrayList<>();

    /**
     * 非终结符集合
     */
    public Set<String> noTerminalSet = new HashSet<>();

    /**
     * 终结符集合
     */
    public Set<String> terminalSet = new HashSet<>();

    /**
     * 切割后的文法产生式
     */
    public Map<String, List<List<String>>> productCutMap = new HashMap<>();

    public GrammarProductionManager(List<String> rawProduction,
                                    Map<String, List<List<String>>> productCutMap)
    {
        this.rawProduction = rawProduction;
        this.productCutMap = productCutMap;
        this.computeNoTerminals();
        this.computeTerminals();
    }

    private void computeNoTerminals()
    {
        this.noTerminalSet = productCutMap.keySet();
    }

    private void computeTerminals()
    {
        Set<String> noTerminals = getNoTerminalSet();
        Set<String> terminals = getTerminalSet();
        Collection<List<List<String>>> rightPartsSet = getProductCutMap().values();
        rightPartsSet.forEach(
            rightParts -> rightParts.forEach(rightPart -> rightPart.forEach(r -> {
                if (!noTerminals.contains(r) && !terminals.contains(r) && !r.equals("ε"))
                {
                    this.terminalSet.add(r);
                }
            })));
    }

    public List<List<String>> getRightParts(String key)
    {
        return new ArrayList<>(getProductCutMap().get(key));
    }
}
