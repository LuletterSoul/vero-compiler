package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.syntax.production.GrammarProductionManager;

import lombok.Getter;


/**
 * Created by XiangDe Liu on 2018/3/7.
 */
@Getter
public class ProgramMonitor
{
    private SymbolMaintainer maintainer;

    private GrammarProductionManager productionManager;

    private SyntaxAnalysisTableGenerator generator;

    private List<ProgramItemSet> family = new ArrayList<>();

    public ProgramMonitor(GrammarProductionManager productionManager)
    {
        init(productionManager);
    }

    private void init(GrammarProductionManager productionManager)
    {
        this.productionManager = productionManager;
        this.maintainer = new SymbolMaintainer(productionManager);
        this.generator = new SyntaxAnalysisTableGenerator(this, this.maintainer);
        this.extendGrammar();
    }

    /**
     * 重启总控程序
     * 
     * @param productionManager
     *            传入新的文法产生式
     */
    public void reloadMonitor(GrammarProductionManager productionManager)
    {
        init(productionManager);
    }

    /**
     * 输入Token流
     * 
     * @param lexemes
     */
    public void monitor(List<Lexeme> lexemes)
    {
        SyntaxDriverInfo driverInfo = generator.generate();
        SyntaxParser parser = new SyntaxParser(driverInfo, getMaintainer());
        parser.parse(lexemes);
    }



    /**
     * 拓广文法
     */
    public void extendGrammar()
    {
        List<String> right = new ArrayList<>();
        right.add("<E>");
        List<String> lookAhead = new ArrayList<>();
        lookAhead.add("$");
        ProgramItem extendItem = new ProgramItem("<E'>", right, -1, lookAhead, -1, this);
        this.maintainer.setAcceptLeft(extendItem.getLeft());
        this.maintainer.setAcceptRight(extendItem.getRight().get(0));
        this.maintainer.setI0(extendItem);
    }

    public void items(ProgramItem augmentedGrammar)
    {
        List<ProgramItem> arr = new ArrayList<>();
        arr.add(augmentedGrammar);
        ProgramItemSet I0 = closure(arr);
        family.add(I0);
        for (int i = 0; i < family.size(); ++i)
        {
            ProgramItemSet I = family.get(i);
            HashMap<String, List<Integer>> ids = I.getProIds();
            for (String key : ids.keySet())
            {
                gotoShift(I, ids.get(key), key);
            }
        }
    }

    private ProgramItemSet closure(List<ProgramItem> startItem)
    {
        // 状态初始项
        ProgramItemSet I = new ProgramItemSet(startItem, this, getMaintainer());

        for (int i = 0; i < I.container.size(); ++i)
        {
            ProgramItem item = I.container.get(i);

            if (!item.dotAtEnd() && item.nextIsNoTerminal())
            {

                List<String> lookAhead = new ArrayList<>();
                List<String> beta = item.getBeta();
                if (beta != null) lookAhead.addAll(first(beta));
                if (lookAhead.isEmpty()) lookAhead.addAll(item.getLookAhead());

                String left = item.getDotRight();
                List<List<String>> rights = maintainer.getRights(left);

                int index = 0;
                for (List<String> right : rights)
                {
                    ProgramItem extendItem = new ProgramItem(left, right, -1, lookAhead, index,
                        this);
                    if (!inI(I, extendItem))
                    {
                        I.addItem(extendItem);
                    }
                    index++ ;
                }
            }
        }
        return I;
    }

    private void gotoShift(ProgramItemSet I, List<Integer> ids, String key)
    {
        List<ProgramItem> initItems = I.statusInitialize(ids);
        int flag = isItemExist(initItems);
        if (flag != -1)
        {
            I.setStatus(key, flag);
        }
        else
        {
            family.add(closure(initItems));
            I.setStatus(key, family.size() - 1);
        }
    }

    /**
     * 严格判等
     * 要求每个项目集的所有元素
     * 相等时才相等
     * @param init
     * @return
     */
    private int isItemExist(List<ProgramItem> init)
    {
        for (int index = 0; index < family.size(); index++ )
        {
            int[] flag = new int[init.size()];
            ProgramItemSet itemSet = family.get(index);
            for (ProgramItem item : itemSet.container)
            {
                for (int j = 0; j < init.size(); j++ )
                {
                    if (isProgramItemEqual(item, init.get(j)))
                    {
                        flag[j] = 1;
                    }
                }
            }
            int sum = 0;
            for (int i = 0; i < init.size(); ++i)
            {
                sum += flag[i];
            }
            if (sum == flag.length) return index;
        }

        return -1;
    }

    private boolean isProgramItemEqual(ProgramItem i0, ProgramItem i1)
    {
        if (i0.left.equals(i1.left) && i0.dot == i1.dot && i0.right.equals(i1.right)
            && i0.lookAhead.equals(i1.lookAhead))
        {
            return true;
        }
        return false;
    }

    private List<String> first(List<String> bate)
    {
        List<String> firstSet = new ArrayList<>();

        first(bate, firstSet, 0);

        return firstSet;
    }

    private String first(List<String> beta, List<String> firstSet, int index)
    {
        String finalSymbol;
        List<String> record = new ArrayList<>();
        String left = beta.get(index);

        if (!maintainer.isTerminal(beta.get(index)) && !record.contains(left))
        {
            record.add(left);
            List<List<String>> rights = maintainer.getRights(left);
            for (int i = 0; i < rights.size(); ++i)
            {
                if (maintainer.isRecursion(rights.get(i), left))
                {
                    if (maintainer.hasEmpty(rights))
                    {
                        rights.get(i).remove(0);
                    }
                    else
                    {
                        rights.remove(i);
                    }
                }
            }
            for (List<String> arr : rights)
            {
                finalSymbol = first(arr, firstSet, 0);
                if (finalSymbol.equals("ε") && index < beta.size() - 1)
                {
                    first(beta, firstSet, index + 1);
                }
            }
            return "success";
        }
        else
        {
            if (!left.equals("ε") && maintainer.isTerminal(left))
            {
                firstSet.add(left);
            }
            return left;
        }
    }

    private boolean inI(ProgramItemSet I, ProgramItem extendItem)
    {
        for (ProgramItem item : I.container)
        {
            if (item.left.equals(extendItem.left) && item.right.equals(extendItem.right)
                && item.dot == extendItem.dot && item.lookAhead.equals(extendItem.lookAhead))
            {
                return true;
            }
        }
        return false;
    }

    private boolean prefixSame(ProgramItemSet I, ProgramItem extendItem)
    {
        for (ProgramItem item : I.container)
        {
            if (item.left.equals(extendItem.left) && item.right.equals(extendItem.right)
                && item.dot == extendItem.dot)
            {
                return true;
            }
        }
        return false;
    }
}
