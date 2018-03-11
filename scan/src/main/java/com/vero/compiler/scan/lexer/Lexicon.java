package com.vero.compiler.scan.lexer;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.vero.compiler.scan.ScannerInfo;
import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.token.Token;
import com.vero.compiler.scan.token.TokenInfo;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:09 2018/3/11.
 * @since vero-compiler
 */

@Data
public class Lexicon
{
    private Lexer defaultState;

    private List<Lexer> lexerStates;

    private List<TokenInfo> tokenList;

    public Lexicon()
    {
        tokenList = new ArrayList<>();
        lexerStates = new ArrayList<>();
        defaultState = new Lexer(this, 0);
        lexerStates.add(defaultState);
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

    public Lexer defineLexer(Lexer baseLexer) {
        Integer index = getLexerStates().size();
        Lexer newState = new Lexer(this, index, baseLexer);
        getLexerStates().add(newState);
        return newState;
    }


    public CompactCharSetManager createCompactCharSetManager()
    {
        List<TokenInfo> tokenInfos = getTokenList();

        HashSet<Character> compactableCharSet = new HashSet<>();
        HashSet<Character> uncompactableCharSet = new HashSet<>();

        List<HashSet> compactableCharSets = new ArrayList<>();

        tokenInfos.forEach( t->{
            List<HashSet> cs = t.getDefinition().getCompactableCharSet();
            HashSet<Character> ucs = t.getDefinition().getUnCompressibleCharSet();
            compactableCharSets.addAll(cs);
            uncompactableCharSet.addAll(ucs);
        });

        compactableCharSets.forEach(compactableCharSet::addAll);

        compactableCharSet.removeAll(uncompactableCharSet);

        Map<HashSet<Integer>, Integer> compactClassDict = new HashMap<>();
        AtomicReference<Integer> compactCharIndex = new AtomicReference<>(1);
        Integer[] compactClassTable = new Integer[65536];

        uncompactableCharSet.forEach( ucs ->{
            Integer index = compactCharIndex.getAndSet(compactCharIndex.get() + 1);
            compactClassTable[ucs] = index;
        });
        compactableCharSet.forEach( cs ->{
            HashSet<Integer> setOfCharset = new HashSet<Integer>();
            for (int i = 0; i < compactableCharSets.size(); i++) {
                HashSet set = compactableCharSets.get(i);
                if (set.contains(cs)) {
                    setOfCharset.add(i);
                }
            }
            if (compactClassDict.containsKey(setOfCharset)) {
                Integer index = compactClassDict.get(setOfCharset);
                compactClassTable[cs] = index;
            }
            else{
                Integer index = compactCharIndex.getAndSet(compactCharIndex.get() + 1);
                compactClassDict.put(setOfCharset, index);
                compactClassTable[cs] = index;
            }
        });
        return new CompactCharSetManager(compactClassTable, compactCharIndex.get());
    }

    public ScannerInfo createScannerInfo()
    {
//        DFAModel dfa = f.Create(this);
//        CompressedTransitionTable ctt = CompressedTransitionTable.Compress(dfa);
//
//        return new ScannerInfo(ctt.TransitionTable, ctt.CharClassTable, dfa.GetAcceptTables(),
//            tokenList.Count);
        return null;
    }
}
