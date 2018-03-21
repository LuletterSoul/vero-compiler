package com.vero.compiler.lexer.expression;

/**
 * 编程语言的正则表达式的类型
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5
 * created in  20:14 2018/3/10.
 * @since vero-compiler
 */


public enum RegularExpressionType {
    Empty(1, '2', "空"),
    Symbol(2, '1', "标识符"),
    Alternation(2, '1', "或"),
    Concatenation(2, '1', "连接"),
    KleeneStar(2, '1', "克林闭包"),
    AlternationCharSet(2, '1', "或字符"),
    StringLiteral(2, '1', "字符串字面常量"),;


    private Integer value;
    private Character character;
    private String description;

    RegularExpressionType(Integer value, Character character ,String description) {
        this.value = value;
        this.character = character;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
