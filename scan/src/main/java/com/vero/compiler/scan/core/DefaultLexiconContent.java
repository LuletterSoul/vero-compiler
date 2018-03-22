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

import lombok.Getter;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:38 2018/3/22.
 * @since vero-compiler
 */

@Getter
public abstract class DefaultLexiconContent implements LexiconContent
{

    protected Lexicon lexicon;

    protected DFAModel dfaModel;

    protected SourceReader sourceReader;

    protected LexerTransitionInfo info;

    protected Scanner scanner;

    protected TokenDefinitions tokenDefinitions;

    public DefaultLexiconContent()
    {
        this.lexicon = defineLexicon();
        // todo handleExpression:define the default regular expression of lexicon content;
        initContent();
    }

    public DefaultLexiconContent(RegularExpression[] regularExpressions)
    {
        this.lexicon = defineLexicon();
        this.handleExpressions(regularExpressions);
        initContent();
    }

    public DefaultLexiconContent(TokenDefinitions tokenDefinitions)
    {
        this.tokenDefinitions = tokenDefinitions;
    }

    protected void initContent()
    {
        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        this.info = new LexerTransitionInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());
        this.scanner = new Scanner(info, new FileSourceReader());
    }

    @Override
    public Lexicon defineLexicon()
    {
        return new Lexicon();
    }

    @Override
    public abstract void handleExpressions(RegularExpression[] regularExpressions);

    @Override
    public LexemeCollector buildCollector()
    {
        return new DefaultLexemeCollector(this.scanner, this.info);
    }

    @Override
    public TokenDefinitions getDefinitions()
    {
        return tokenDefinitions;
    }
}
