package com.vero.compiler.syntax.production;


import java.util.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 产生式管理器,一般由 {@link com.vero.compiler.syntax.reader.SyntaxGrammarParser}
 * 解析语法的文法文件输入流,从而转化得到二维哈希表中去 Created by XiangDe Liu on 2018/3/21.
 */
@Getter
@Slf4j
public class GrammarProductionManager
{

    public List<String> rawProductions;

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

    public GrammarProductionManager(List<String> rawProductions,
                                    Map<String, List<List<String>>> productCutMap)
    {
        this.rawProductions = rawProductions;
        this.productCutMap = productCutMap;
        if (log.isDebugEnabled())
        {
            this.rawProductions.forEach(r -> log.debug("Production ＊ : {}", r));
        }
        this.computeNoTerminals();
        this.computeTerminals();
    }

    private void computeNoTerminals()
    {
        this.noTerminalSet = productCutMap.keySet();
        if (log.isDebugEnabled())
        {
            log.debug("No Terminal Symbol Set ＊ : {}", noTerminalSet);
        }
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
        if (log.isDebugEnabled())
        {
            log.debug("Terminal Symbol Set ＊ : {}", terminalSet);
        }
    }

    public List<List<String>> getRightParts(String key)
    {
        List<List<String>> rightParts = getProductCutMap().get(key);
        if (rightParts == null) {
            return null;
        }
        return new ArrayList<>(getProductCutMap().get(key));
    }
}
