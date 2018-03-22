package com.vero.compiler.scan.core;


import java.io.File;
import java.util.List;
import java.util.Map;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.lexer.core.Lexeme;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:16 2018/3/22.
 * @since vero-compiler
 */

public interface LexemeCollector
{
    Map<String, String> collect(File source);

    void postPerLexemeCollected(Lexeme lexeme);

    List<Lexeme>  getLexemeStream();

    List<CompilationError> getErrors();

    boolean isCashed();
}
