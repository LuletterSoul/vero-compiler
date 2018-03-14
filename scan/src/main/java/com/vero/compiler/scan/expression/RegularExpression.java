package com.vero.compiler.scan.expression;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.NFAModel;

import lombok.Data;


/**
 * 正则表达式的基类 定义了一系列DFA的转化方法 转换的算法委托
 *
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:05 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class RegularExpression
{
    protected RegularExpressionType expressionType;

    protected List<HashSet> compressibleCharSets = new LinkedList<>();

    protected HashSet<Character> unCompressibleCharSet = new HashSet<>();

    protected RegularExpressionConverter regularExpressionConverter;

    public RegularExpression(RegularExpressionType expressionType)
    {
        this.expressionType = expressionType;
    }

    public abstract NFAModel accept();

    public RegularExpression(RegularExpressionType expressionType, RegularExpressionConverter regularExpressionConverter) {
        this.expressionType = expressionType;
        this.regularExpressionConverter = regularExpressionConverter;
    }

    public abstract HashSet<Character> getUnCompressibleCharSet();

    public abstract List<HashSet> getCompressibleCharSets();
}
