package com.vero.compiler.syntax.core;


import java.util.ArrayList;


/**
 * Created by XiangDe Liu on 2018/3/11.
 */
public class Input
{
    public static ArrayList<String> inputs = new ArrayList<>();

    public static void addLine(String line)
    {
        for (String str : line.split(" "))
        {
            inputs.add(str);
        }
    }

    public static String result()
    {
        String result = "";
        for (String str : inputs)
        {
            result += (str + " ");
        }
        return result;
    }
}
