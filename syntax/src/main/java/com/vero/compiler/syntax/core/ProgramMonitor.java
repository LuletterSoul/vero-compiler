package com.vero.compiler.syntax.core;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by XiangDe Liu on 2018/3/7.
 */
public class ProgramMonitor
{
    public static ArrayList<ProgramItemSet> setFamily = new ArrayList<>();


    public ProgramMonitor() {
    }

    public static void items(ProgramItem augmentedGrammar)
    {
        ArrayList<ProgramItem> arr = new ArrayList<>();
        arr.add(augmentedGrammar);
        ProgramItemSet I0 = closure(arr);
        setFamily.add(I0);

        for (int i = 0; i < setFamily.size(); ++i)
        {
            ProgramItemSet I = setFamily.get(i);
            HashMap<String, ArrayList<Integer>> ids = I.getProIds();
            for (String key : ids.keySet())
            {
                Goto(I, ids.get(key), key);
            }
        }
    }

    private static ProgramItemSet closure(ArrayList<ProgramItem> startItem)
    {
        // 状态初始项
        ProgramItemSet I = new ProgramItemSet(startItem);

        for (int i = 0; i < I.container.size(); ++i)
        {
            ProgramItem item = I.container.get(i);

            if (!item.dotAtEnd() && item.NextIsInterminal())
            {

                ArrayList<String> lookAhead = new ArrayList<>();
                ArrayList<String> beta = item.getBeta();
                if (beta != null) lookAhead.addAll(first(beta));
                if (lookAhead.isEmpty()) lookAhead.addAll(item.getLookAhead());

                String left = item.getDotRight();
                ArrayList<ArrayList<String>> rights = Utils.getRights(left);

                int index = 0;
                for (ArrayList<String> right : rights)
                {
                    ProgramItem extendItem = new ProgramItem(left, right, -1, lookAhead, index);
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

    private static void Goto(ProgramItemSet I, ArrayList<Integer> ids, String key)
    {
        ArrayList<ProgramItem> initItems = I.statusInitialize(ids);
        int flag = IExist(initItems);
        if (flag != -1)
        {
            I.setStatus(key, flag);
        }
        else
        {
            setFamily.add(closure(initItems));
            I.setStatus(key, setFamily.size() - 1);
        }
    }

    private static int IExist(ArrayList<ProgramItem> init)
    {
        for (int index = 0; index < setFamily.size(); index++ )
        {
            int[] flag = new int[init.size()];
            ProgramItemSet itemSet = setFamily.get(index);
            for (ProgramItem item : itemSet.container)
            {
                for (int j = 0; j < init.size(); j++ )
                {
                    if (myEqual(item, init.get(j)))
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

    private static boolean myEqual(ProgramItem i0, ProgramItem i1)
    {
        if (i0.left.equals(i1.left) && i0.dot == i1.dot && i0.right.equals(i1.right)
            && i0.lookAhead.equals(i1.lookAhead))
        {
            return true;
        }
        return false;
    }

    private static ArrayList<String> first(ArrayList<String> bate)
    {
        ArrayList<String> firstSet = new ArrayList<>();

        first(bate, firstSet, 0);

        return firstSet;
    }

    private static String first(ArrayList<String> beta, ArrayList<String> firstSet, int index)
    {
        String finalSymbol;
        ArrayList<String> record = new ArrayList<>();
        String left = beta.get(index);

        if (!Utils.isTerminal(beta.get(index)) && !record.contains(left))
        {
            record.add(left);
            ArrayList<ArrayList<String>> rights = Utils.getRights(left);
            for (int i = 0; i < rights.size(); ++i)
            {
                if (Utils.isRecursion(rights.get(i), left))
                {
                    if (Utils.hasEmpty(rights))
                    {
                        rights.get(i).remove(0);
                    }
                    else
                    {
                        rights.remove(i);
                    }
                }
            }
            for (ArrayList<String> arr : rights)
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
            if (!left.equals("ε") && Utils.isTerminal(left))
            {
                firstSet.add(left);
            }
            return left;
        }
    }

    private static boolean inI(ProgramItemSet I, ProgramItem extendItem)
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

    private static boolean prefixSame(ProgramItemSet I, ProgramItem extendItem)
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
