package com.vero.compiler.scan.core;


import com.vero.compiler.lexer.compress.CompressedTransitionTable;
import com.vero.compiler.lexer.converter.NFAConverter;
import com.vero.compiler.lexer.converter.RegularExpressionConverter;
import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.generator.DFAModel;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.lexer.source.FileSourceReader;
import com.vero.compiler.lexer.source.SourceReader;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:38 2018/3/22.
 * @since vero-compiler
 */

public class DefaultLexiconContent implements LexiconContent
{

    protected Lexicon lexicon;

    protected DFAModel dfaModel;

    protected SourceReader sourceReader;

    protected LexerTransitionInfo info;

    protected Scanner scanner;

    protected TokenDefinitions definitions;

    public DefaultLexiconContent(RegularExpression[] regularExpressions)
    {
        this.lexicon = defineLexicon(regularExpressions);
        initContent();
    }

    private void initContent()
    {
        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        this.info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        this.scanner = new Scanner(info, new FileSourceReader());
    }

    private Lexicon defineLexicon(RegularExpression[] regularExpressions)
    {
        Lexicon lexicon = new Lexicon();
        this.definitions = new TokenDefinitions(lexicon, regularExpressions);
        return lexicon;
    }

    @Override
    public LexemeCollector buildCollector()
    {
        return new DefaultLexemeCollector(this.scanner, this.info);
    }
}
