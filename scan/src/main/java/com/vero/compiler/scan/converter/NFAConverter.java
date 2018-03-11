package com.vero.compiler.scan.converter;

import com.vero.compiler.scan.expression.*;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.generator.NFAModel;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  21:34 2018/3/10.
 * @since vero-compiler
 */

public class NFAConverter extends RegularExpressionConverter {

    @Override
    public NFAModel convert(RegularExpression expression) {
        return super.convert(expression);
    }

    @Override
    public NFAModel convertAlternation(AlternationExpression exp) {
        return super.convertAlternation(exp);
    }

    @Override
    public NFAModel convertSymbol(SymbolExpression exp) {
        return super.convertSymbol(exp);
    }

    @Override
    public NFAModel convertEmpty(EmptyExpression exp) {
        return super.convertEmpty(exp);
    }

    @Override
    public NFAModel convertConcatenation(ConcatenationExpression exp) {
        return super.convertConcatenation(exp);
    }

    @Override
    public NFAModel convertAlternationCharSet(AlternationCharSetExpression exp) {
        return super.convertAlternationCharSet(exp);
    }

    @Override
    public NFAModel convertStringLiteral(StringLiteralExpression exp) {
        return super.convertStringLiteral(exp);
    }

    @Override
    public NFAModel convertKleeneStar(KleeneStarExpression exp) {
        return super.convertKleeneStar(exp);
    }
}
