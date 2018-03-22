package com.vero.compiler.syntax.reader;


import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;
import com.vero.compiler.scan.core.DefaultLexemeCollector;
import com.vero.compiler.scan.core.Scanner;

import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:45 2018/3/22.
 * @since vero-compiler
 */

@Slf4j
public class SyntaxLexemeCollector extends DefaultLexemeCollector
{
    public SyntaxLexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo)
    {
        super(scanner, lexerTransitionInfo);
    }

    @Override
    public void postPerLexemeCollected(Lexeme lexeme)
    {
        super.postPerLexemeCollected(lexeme);
    }
}
