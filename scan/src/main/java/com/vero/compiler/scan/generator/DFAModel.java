package com.vero.compiler.scan.generator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.vero.compiler.scan.compress.CompactCharSetManager;
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
        for (int i = 0; i < nfaStateSet.size(); i++ )
        {
            Integer tokenIndex = getNfaModel().getStates().get(i).getTokenIndex();
            if (tokenIndex >= 0)
            {
                Integer lexerIndex = tokens.get(tokenIndex).getState().getIndex();

            }
        }
        // var acceptStates = (from i in state.NFAStateSet
        // let tokenIndex = nfaModel.States[i].TokenIndex
        // where tokenIndex >=0
        // let token = tokens[tokenIndex]
        // orderby token.Tag.Index
        // group token by token.State.Index into lexerState
        // orderby lexerStates[ lexerState.Key].Level
        // select lexerState).ToArray();

    }

    private void convertNFAToDFA()
    {

    }

    private DFAState GetDFAState(DFAState start, int symbol)
    {
        DFAState target = new DFAState();

        return GetClosure(target);
    }

    private DFAState GetClosure(DFAState state)
    {
        DFAState closure = new DFAState();

        return closure;
    }
}