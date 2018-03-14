package com.vero.compiler.scan.converter;


import java.util.HashSet;

import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.expression.*;
import com.vero.compiler.scan.generator.NFAEdge;
import com.vero.compiler.scan.generator.NFAModel;
import com.vero.compiler.scan.generator.NFAState;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:34 2018/3/10.
 * @since vero-compiler
 */

public class NFAConverter extends RegularExpressionConverter
{

    public NFAConverter(CompactCharSetManager compactCharSetManager)
    {
        super(compactCharSetManager);
    }

    @Override
    public NFAModel convertAlternation(AlternationExpression exp)
    {
        NFAModel nfaModel1 = convert(exp.getExpression1());
        NFAModel nfaModel2 = convert(exp.getExpression2());

        NFAState head = new NFAState();
        NFAState tail = new NFAState();

        head.addEdge(nfaModel1.getEntryEdge());
        head.addEdge(nfaModel1.getEntryEdge());

        nfaModel1.getTailState().addEmptyEdgeTo(tail);
        nfaModel2.getTailState().addEmptyEdgeTo(tail);

        NFAModel alternationNFAModel = new NFAModel();

        alternationNFAModel.addState(head);
        alternationNFAModel.addStates(nfaModel1.getStates());
        alternationNFAModel.addStates(nfaModel2.getStates());
        alternationNFAModel.addState(tail);

        alternationNFAModel.setEntryEdge(new NFAEdge(head));
        alternationNFAModel.setTailState(tail);

        return alternationNFAModel;
    }

    @Override
    public NFAModel convertSymbol(SymbolExpression exp)
    {
        NFAState tail = new NFAState();
        Integer classIndex = compactCharSetManager.getCompactClass(exp.getSymbol());
        NFAEdge entryEdge = new NFAEdge(classIndex, tail);
        NFAModel symbolNfa = new NFAModel();

        symbolNfa.addState(tail);
        symbolNfa.setTailState(tail);
        symbolNfa.setEntryEdge(entryEdge);
        return symbolNfa;
    }

    @Override
    public NFAModel convertEmpty(EmptyExpression exp)
    {
        NFAState tail = new NFAState();
        NFAEdge entryEdge = new NFAEdge(tail);

        NFAModel emptyNfa = new NFAModel();
        emptyNfa.addState(tail);
        emptyNfa.setTailState(tail);
        emptyNfa.setEntryEdge(entryEdge);
        return emptyNfa;
    }

    @Override
    public NFAModel convertConcatenation(ConcatenationExpression exp)
    {
        NFAModel leftNFA = convert(exp.getLeft());
        NFAModel rightNFA = convert(exp.getRight());

        leftNFA.getTailState().addEdge(rightNFA.getEntryEdge());

        NFAModel concatenationNfa = new NFAModel();

        concatenationNfa.addStates(leftNFA.getStates());
        concatenationNfa.addStates(rightNFA.getStates());
        concatenationNfa.setEntryEdge(leftNFA.getEntryEdge());
        concatenationNfa.setTailState(rightNFA.getTailState());

        return concatenationNfa;
    }

    @Override
    public NFAModel convertAlternationCharSet(AlternationCharSetExpression exp)
    {
        NFAState head = new NFAState();
        NFAState tail = new NFAState();
        NFAModel charSetNfa = new NFAModel();

        charSetNfa.addState(head);
        HashSet<Integer> classsSet = new HashSet<>();
        exp.getCharacters().forEach(c -> {
            Integer classIndex = getCompactCharSetManager().getCompactClass(c);
            if (classsSet.add(classIndex))
            {
                NFAEdge symbolEdge = new NFAEdge(classIndex, tail);
                head.addEdge(symbolEdge);
            }
        });
        charSetNfa.addState(tail);
        charSetNfa.setEntryEdge(new NFAEdge(head));
        charSetNfa.setTailState(tail);
        return charSetNfa;
    }

    @Override
    public NFAModel convertStringLiteral(StringLiteralExpression exp)
    {
        NFAModel literalNfa = new NFAModel();

        NFAState terminalState = null;

        for (char c : exp.getLiteral().toCharArray())
        {
            NFAState symbolState = new NFAState();
            int classIndex = getCompactCharSetManager().getCompactClass(c);
            NFAEdge symbolEdge = new NFAEdge(classIndex, symbolState);
            if (terminalState != null)
            {
                terminalState.addEdge(symbolEdge);
            }
            else
            {
                literalNfa.setEntryEdge(symbolEdge);
            }
            terminalState = symbolState;
            literalNfa.addState(symbolState);
        }
        literalNfa.setTailState(terminalState);
        return literalNfa;
    }

    @Override
    public NFAModel convertKleeneStar(KleeneStarExpression exp)
    {
        NFAModel innerNFA = this.convert(exp.getInnerExpression());
        NFAState newTail = new NFAState();
        NFAEdge entry = new NFAEdge(newTail);
        innerNFA.getTailState().addEmptyEdgeTo(newTail);
        newTail.addEdge(innerNFA.getEntryEdge());
        NFAModel kleeneClosureNfa = new NFAModel();

        kleeneClosureNfa.addStates(innerNFA.getStates());
        kleeneClosureNfa.addState(newTail);
        kleeneClosureNfa.setEntryEdge(entry);
        kleeneClosureNfa.setTailState(newTail);
        return kleeneClosureNfa;
    }
}
