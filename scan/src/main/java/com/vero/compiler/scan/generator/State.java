package com.vero.compiler.scan.generator;


import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 21:13 2018/3/10.
 * @since vero-compiler
 */

@Data
public abstract class State
{
    protected Integer index;

    protected Edge targetEdge;

    public State() {
        // 初始化为-1表示没有token绑定到此状态点
        this.index = -1;
    }



    public State(Integer index, Edge targetEdge) {
        this.index = index;
        this.targetEdge = targetEdge;
    }

}
