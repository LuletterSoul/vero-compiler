package com.vero.compiler.scan.core;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vero.compiler.common.error.CompilationError;
import com.vero.compiler.lexer.core.Lexeme;
import com.vero.compiler.lexer.info.LexerTransitionInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 12:18 2018/3/22.
 * @since vero-compiler
 */

@Setter
@Getter
@Slf4j
public class DefaultLexemeCollector implements LexemeCollector
{

    protected Scanner scanner;

    protected LexerTransitionInfo lexerTransitionInfo;

    protected List<Lexeme> lexemeStream = new ArrayList<>();

    protected List<CompilationError> errors = new ArrayList<>();

    protected Map<String, String> lexeme2TokenType = new HashMap<>();

    public DefaultLexemeCollector(Scanner scanner, LexerTransitionInfo lexerTransitionInfo)
    {
        this.scanner = scanner;
        this.lexerTransitionInfo = lexerTransitionInfo;
    }

    @Override
    public Map<String, String> collect(File source)
    {
        prepareCollect(source);
        Lexeme lexeme = scanner.read();
        postPerLexemeCollected(lexeme);
        debugMessage(lexeme);
        while (!lexeme.getTokenIndex().equals(lexerTransitionInfo.getEndOfStreamTokenIndex()))
        {
            lexeme = scanner.read();
            postPerLexemeCollected(lexeme);
            debugMessage(lexeme);
        }
        // 保存扫描过程的错误
        this.errors = scanner.getErrorsList();
        return lexeme2TokenType;
    }

    private void debugMessage(Lexeme lexeme)
    {
        if (!lexeme.getContent().equals(" ") && !lexeme.getContent().equals("\r\n"))
        {
            log.debug("Eat a lexeme:[{}]", lexeme.getContent());
        }
    }

    private void prepareCollect(File source)
    {
        this.lexemeStream.clear();
        this.lexeme2TokenType.clear();
        scanner.changeSourceFile(source);
    }

    /**
     * 对每个词条的操作由子类实现
     * 
     * @param lexeme
     */
    @Override
    public void postPerLexemeCollected(Lexeme lexeme)
    {
        if (lexeme.isEndOfStream())
        {
            lexeme.setContent("EOF");
            return;
        }
        this.lexemeStream.add(lexeme);
    }

    @Override
    public boolean isCashed()
    {
        return !errors.isEmpty();
    }

}
