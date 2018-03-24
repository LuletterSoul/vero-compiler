package com.vero.compiler.web.controllers;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vero.compiler.common.error.PathUtils;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.web.services.CompilerService;

import io.swagger.annotations.ApiOperation;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:38 2018/3/24.
 * @since vero-compiler
 */

@RestController
@Slf4j
@RequestMapping(value = "/compiler")
public class CompilerController
{

    private CompilerService compilerService;

    @Autowired
    public void setCompilerService(CompilerService compilerService)
    {
        this.compilerService = compilerService;
    }

    @ApiOperation(value = "创建词法分析器")
    @PostMapping(value = "/compiler/token_lexer")
    public ResponseEntity<RegularExpression[]> createTokenLexer(@RequestPart MultipartFile file)
    {
        File sourceFile = transfer(file,"token_lexer");
        try
        {
            file.transferTo(sourceFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(compilerService.generateTokenLexer(sourceFile), HttpStatus.OK);
    }

    private File transfer(MultipartFile file,String dir) {
        String filename = file.getOriginalFilename();
        String lexerDir = Objects.requireNonNull(PathUtils.getAbsolutePath(dir));
        return new File(PathUtils.concat(lexerDir, filename));
    }

    @ApiOperation(value = "创建语法文本分析器")
    @PostMapping(value = "/compiler/grammar_lexer")
    public ResponseEntity<List<String>> createSyntaxLexer(@RequestPart MultipartFile file)
    {
        File sourceFile = transfer(file,"grammar_lexer");
        try
        {
            file.transferTo(sourceFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(compilerService.generateSyntaxReadLexer(sourceFile),
            HttpStatus.OK);
    }

    @ApiOperation(value = "开始语法分析")
    @PostMapping(value = "/compiler/analysis_results")
    public ResponseEntity<Map<String, String>> analysis(@RequestPart MultipartFile file)
    {
        File sourceFile = transfer(file,"input");
        try
        {
            file.transferTo(sourceFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(compilerService.analysis(sourceFile), HttpStatus.OK);
    }

}
