package com.vero.compiler.scan.token;


import java.util.HashMap;
import java.util.Map;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:04 2018/3/18.
 * @since vero-compiler
 */

public enum TokenType {
    VAR(0, "<var>", "标识符"),
    KEY_WORDS(1, "<keywords>", "关键字"),
    NUMBER(2, "<number>", "数字"),
    NUMBER_ALPHABET(3,"<number_alphabet>","字母数字"),
    INTEGER(4, "<integer>", "整数"),
    UNSIGNED_INTEGER(5, "<unsigned_integer>", "无符号整数"),
    ALPHABET(6, "<alphabet>", "字母"),
    DELIMITER(7, "<delimiter>", "界符"),
    OPERATOR(8, "<operator>", "运算符"),;

    private Integer priority;

    private String type;

    private String description;

    private static Map<String, TokenType> tokenTypeMap = new HashMap<>();
    static
    {
        TokenType[] values = TokenType.values();
        for (int i = 0; i < values.length; i++ )
        {
            tokenTypeMap.put(values[i].type.toUpperCase(), values[i]);
        }
    }

    TokenType(Integer priority, String type, String description)
    {
        this.priority = priority;
        this.type = type;
        this.description = description;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public String getType()
    {
        return type;
    }

    public String getDescription()
    {
        return description;
    }

    public static Map<String, TokenType> getTokenTypeMap()
    {
        return tokenTypeMap;
    }
}
