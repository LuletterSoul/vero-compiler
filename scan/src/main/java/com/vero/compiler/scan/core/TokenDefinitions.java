package com.vero.compiler.scan.core;


import com.vero.compiler.lexer.core.Lexicon;
import com.vero.compiler.lexer.expression.RegularExpression;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 16:07 2018/3/22.
 * @since vero-compiler
 */

public interface TokenDefinitions
{
    void initTokenDefinitions(Lexicon lexicon, RegularExpression[] tokenExpressions);
}
