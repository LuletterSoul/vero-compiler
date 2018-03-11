package com.vero.compiler.scan.generator;


import com.vero.compiler.scan.compress.CompactCharSetManager;
import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.expression.*;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:51 2018/3/10.
 * @since vero-compiler
 */

public class NFAConverter extends RegularExpressionConverter
{
    private CompactCharSetManager compactCharSetManager;

    public NFAConverter(CompactCharSetManager compactCharSetManager) {
        this.compactCharSetManager = compactCharSetManager;
    }

    @Override
    public NFAModel convertAlternation(AlternationExpression exp)
    {
        return super.convertAlternation(exp);
    }

    @Override
    public NFAModel convertSymbol(SymbolExpression exp)
    {
        return super.convertSymbol(exp);
    }

    @Override
    public NFAModel convertEmpty(EmptyExpression exp)
    {
        return super.convertEmpty(exp);
    }

    @Override
    public NFAModel convertConcatenation(ConcatenationExpression exp)
    {
        return super.convertConcatenation(exp);
    }

    @Override
    public NFAModel convertAlternationCharSet(AlternationCharSetExpression exp)
    {
        return super.convertAlternationCharSet(exp);
    }

    @Override
    public NFAModel convertStringLiteral(StringLiteralExpression exp)
    {
        return super.convertStringLiteral(exp);
    }

    @Override
    public NFAModel convertKleeneStar(KleeneStarExpression exp)
    {
        return super.convertKleeneStar(exp);
    }
}
