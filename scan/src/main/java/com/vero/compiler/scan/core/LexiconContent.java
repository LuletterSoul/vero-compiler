package com.vero.compiler.scan.core;


import com.vero.compiler.lexer.compress.CompressedTransitionTable;
import com.vero.compiler.lexer.converter.NFAConverter;
import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.generator.DFAModel;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.source.FileSourceReader;
import com.vero.compiler.lexer.source.SourceReader;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 17:22 2018/3/21.
 * @since vero-compiler
 */

public class LexiconContent
{
    private Lexicon lexicon;

    private DFAModel dfaModel;

    private SourceReader sourceReader;

    private CompressedTransitionTable tc;

    private LexerTransitionInfo info;

    private Scanner scanner;

    private RegularExpressionConverter converter;

    private TokenDefinitions tokenDefinitions;

    public LexiconContent(RegularExpression[] regularExpressions)
    {
        Lexicon lexicon = defineLexicon(regularExpressions);
        converter = new NFAConverter(lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        this.tc = CompressedTransitionTable.compress(dfaModel);
        this.info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(), tc.getCharClassTable(),
            dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        this.scanner = new Scanner(info,new FileSourceReader());
    }

    private Lexicon defineLexicon(RegularExpression[] regularExpressions)
    {
        Lexicon lexicon = new Lexicon();
        this.tokenDefinitions = new TokenDefinitions(lexicon, regularExpressions);
        return lexicon;
    }

    public LexemeCollector buildCollector()
    {
        return new LexemeCollector(this.scanner, this.info, this.tokenDefinitions);
    }

}
