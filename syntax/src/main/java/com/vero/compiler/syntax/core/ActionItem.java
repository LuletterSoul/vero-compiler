package com.vero.compiler.syntax.core;

/**
 * Created by XiangDe Liu on 2018/3/11.
 */
public class ActionItem
{
    public Integer status;

    public ActionType type;

    public String left;

    public ActionItem(Integer status, ActionType type)
    {
        this.status = status;
        this.type = type;
    }

    public ActionItem(Integer index, ActionType type, String left)
    {
        this.status = index;
        this.type = type;
        this.left = left;
    }

    public String toString()
    {
        if (type == ActionType.SHFIT)
            return type.getAction() +"->"+ status;
        else
        {
            if (status == null)
                return type.getAction();
            else
                return type.getAction() + status + "(" + left + ")";
        }
    }

}
