package com.vero.compiler.syntax.result;


import java.util.List;

import com.vero.compiler.common.error.CompilationErrorDto;
import com.vero.compiler.parser.LexemeDto;
import com.vero.compiler.syntax.core.AnalysisHistory;
import com.vero.compiler.syntax.table.AnalysisTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 0:33 2018/3/25.
 * @since vero-compiler
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyntaxAnalysisResult
{

    /**
     * 语法分析产生式
     */
    private List<String> syntaxGrammarProductions;

    /**
     * Action、Goto表
     */
    private AnalysisTable analysisTable;

    /**
     * 源文件每一行
     */
    private List<String> preLines;

    /**
     * 所有分析历史
     */
    private List<AnalysisHistory> analysisHistories;

    /**
     * 词法分析得出的Token流
     */
    private List<String> tokenStream;


    /**
     * 词法分析结果
     */
    private LexerAnalysisResult lexerAnalysisResult;


}
