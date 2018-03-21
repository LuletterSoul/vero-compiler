package com.vero.compiler.common.error;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  21:10 2018/3/21.
 * @since vero-compiler
 */

public enum CompilationStage {
    None,
    PreProcessing,
    Scanning,
    Parsing,
    SemanticAnalysis,
    CodeGeneration,
    PostProcessing,
    Other;
}
