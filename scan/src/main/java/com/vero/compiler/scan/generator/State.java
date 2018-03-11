package com.vero.compiler.scan.generator;


import lombok.Data;

import java.util.List;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:13 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class State
{
    protected Integer tokenIndex;

    protected Edge targetEdge;

    public State() {
        // 初始化为-1表示没有token绑定到此状态点
        this.tokenIndex = -1;
    }



    public State(Integer tokenIndex, Edge targetEdge) {
        this.tokenIndex = tokenIndex;
        this.targetEdge = targetEdge;
    }

}
