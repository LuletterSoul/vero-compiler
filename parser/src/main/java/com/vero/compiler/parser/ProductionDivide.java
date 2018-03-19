package com.vero.compiler.parser;


import lombok.Getter;

import java.util.List;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 16:59 2018/3/19.
 * @since vero-compiler
 */

@Getter
public class ProductionDivide
{
    private List<RegularGrammarProduction> noContainTerminalSymbolProductions;

    private List<RegularGrammarProduction> containTerminalSymbolProductions;

    public ProductionDivide(List<RegularGrammarProduction> noContainTerminalSymbolProductions,
                            List<RegularGrammarProduction> containTerminalSymbolProductions)
    {
        this.noContainTerminalSymbolProductions = noContainTerminalSymbolProductions;
        this.containTerminalSymbolProductions = containTerminalSymbolProductions;
    }
}
