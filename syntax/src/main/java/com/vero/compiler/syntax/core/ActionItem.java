package com.vero.compiler.syntax.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by XiangDe Liu on 2018/3/11.
 */
@Data
@AllArgsConstructor
public class ActionItem {
    private Integer status;

    private ActionType type;

    private String left;

    private Integer nextStatus;

    public ActionItem(Integer status, ActionType type) {
        this.status = status;
        this.type = type;
    }

    public ActionItem(Integer index, ActionType type, String left) {
        this.status = index;
        this.type = type;
        this.left = left;
    }



    public String toString() {
        if (type == ActionType.SHFIT)
            return type.getAction() + "->" + status;
        else {
            if (status == null)
                return type.getAction();
            else
                return type.getAction() + status + "(" + left + ")";
        }
    }

}
