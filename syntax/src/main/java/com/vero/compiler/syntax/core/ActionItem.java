package com.vero.compiler.syntax.core;

/**
 * Created by XiangDe Liu on 2018/3/11.
 */
public class ActionItem
{
    public Integer status;

    public String type;

    public String left;

    public ActionItem(Integer status, String type)
    {
        this.status = status;
        this.type = type;
    }

    public ActionItem(Integer index, String type, String left)
    {
        this.status = index;
        this.type = type;
        this.left = left;
    }

    public String toString()
    {
        if (type == "s")
            return "s" + status;
        else
        {
            if (status == null)
                return type;
            else
                return type + status + "(" + left + ")";
        }
    }

}
