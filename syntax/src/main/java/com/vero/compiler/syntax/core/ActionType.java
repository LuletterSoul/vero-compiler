package com.vero.compiler.syntax.core;

/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 17:31 2018/3/23.
 * @since vero-compiler
 */

public enum ActionType {
    REDUCE("reduce", "归约"), SHFIT("shift", "移进"), ACCEPT("accept", "归约"),INVALID("invalid","非法");

    ActionType(String action, String description)
    {
        this.action = action;
        this.description = description;
    }

    private String action;

    private String description;

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
