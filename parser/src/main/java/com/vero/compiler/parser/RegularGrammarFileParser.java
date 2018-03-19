package com.vero.compiler.parser;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.token.TokenType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:59 2018/3/18.
 * @since vero-compiler
 */

@Slf4j
@Getter
public class RegularGrammarFileParser
{
    private File grammarSource;

    private FileInputStream fileReader;

    private List<String> nonTerminalSymbol;

    private List<Character> terminalSymbol;

    /**
     * 产生式的定义
     */
    private List<String> grammarProductionDefinitions;

    /**
     * 产生式的
     */
    private Map<String, RegularExpression> production2RegularExpression = new HashMap<>();

    /**
     *
     */
    private Map<String, RegularGrammarProduction> grammarProductionMap = new HashMap<>();

    private ProductionDivide productionDivide;

    private List<RegularGrammarProduction> grammarProductions = new ArrayList<>();

    private RegularExpression[] tokenExpressions = new RegularExpression[TokenType.getTokenTypeMap().size()];

    public RegularGrammarFileParser(File grammarSource)
    {
        this.grammarSource = grammarSource;
        try
        {
            this.fileReader = new FileInputStream(grammarSource);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        nonTerminalSymbol = new ArrayList<>();
        terminalSymbol = new ArrayList<>();
        grammarProductionDefinitions = new ArrayList<>();
    }

    public void parse()
        throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(getFileReader(), "Unicode"));
        String str = null;
        while ((str = bufferedReader.readLine()) != null)
        {
            grammarProductionDefinitions.add(str);
        }
    }

    public void parseDefinitions()
    {
        this.grammarProductionDefinitions.forEach(pd -> {
            Queue<Character> queue = new LinkedList<>();
            char chars[] = pd.toCharArray();
            boolean isScanningRightPart = false;
            String currentLeftPart = null;
            List<List<String>> rightParts = new ArrayList<>();
            List<String> currentRightPart = new ArrayList<>();
            RegularGrammarProduction grammarProduction = new RegularGrammarProduction();
            for (int i = 0; i < chars.length; i++ )
            {
                if (!isScanningRightPart)
                {
                    if (chars[i] == '→')
                    {
                        isScanningRightPart = true;
                        continue;
                    }
                    queue.add(chars[i]);
                    if (chars[i] == '>')
                    {
                        currentLeftPart = concatSubProduction(queue);
                        grammarProduction.setLeftPart(currentLeftPart);
                        log.debug("No terminal symbol : [{}]", currentLeftPart);
                    }
                }
                else
                {
                    boolean isNoTerminalSymbol = chars[i] == '>' || chars[i] == '<';
                    boolean isOr = chars[i] == '|';
                    boolean isEndOfLine = i == chars.length - 1;
                    if (isNoTerminalSymbol && !isEndOfLine)
                    {
                        if (chars[i] == '<')
                        {
                            if (isNotDelimiter(chars, i))
                            {
                                String newRightPartCompose = buildNewRightPart(queue,
                                    currentRightPart);
                                log.debug("Contained terminal symbol : [{}]", newRightPartCompose);
                            }
                            queue.add(chars[i]);
                        }
                        else
                        {
                            queue.add(chars[i]);
                            String newRightPartCompose = buildNewRightPart(queue,
                                currentRightPart);
                            log.debug("Contained terminal symbol : [{}]", newRightPartCompose);
                        }
                    }
                    else if (isOr || isEndOfLine)
                    {
                        if (isEndOfLine)
                        {
                            queue.add(chars[i]);
                        }
                        buildNewRightPart(queue, currentRightPart);
                        rightParts.add(currentRightPart);
                        currentRightPart = new ArrayList<>();
                    }
                    else
                    {
                        queue.add(chars[i]);
                    }
                }
            }
            if (currentLeftPart == null)
            {
                return;
            }
            grammarProduction.setRightPart(rightParts);
            grammarProductionMap.put(currentLeftPart, grammarProduction);
            grammarProductions.add(grammarProduction);
        });
    }

    public Set<String> getNoTerminalSymbols()
    {
        return this.grammarProductionMap.keySet();
    }

    public ProductionDivide group()
    {
        List<RegularGrammarProduction> productions = new ArrayList<>();
        List<RegularGrammarProduction> elseProductions = new ArrayList<>();
        Set<String> terminalSymbols = getNoTerminalSymbols();
        this.grammarProductions.forEach(p -> {
            List<List<String>> rightParts = p.getRightPart();
            AtomicBoolean isContainNoTerminalSymbols = new AtomicBoolean(false);
            rightParts.forEach(rightPart -> rightPart.forEach(r -> terminalSymbols.forEach(t -> {
                if (t.equals(r))
                {
                    isContainNoTerminalSymbols.set(true);
                }
            })));
            if (!isContainNoTerminalSymbols.get())
            {
                productions.add(p);
            }
            else
            {
                elseProductions.add(p);
            }
        });

        return new ProductionDivide(productions, elseProductions);
    }

    public List<RegularExpression> transfer()
    {
        Map<String, TokenType> tokenTypeMap = TokenType.getTokenTypeMap();
        this.productionDivide = group();
        List<RegularExpression> expressions = new ArrayList<>();
        productionDivide.getNoContainTerminalSymbolProductions().forEach(p -> {
            RegularExpression expression = transferRegularGrammar2RegularExpression(p);
            expressions.add(expression);
            this.production2RegularExpression.put(p.getLeftPart(), expression);
            TokenType tokenType = tokenTypeMap.get(p.getLeftPart().toUpperCase());
            if (tokenType != null)
            {
                this.tokenExpressions[tokenType.getPriority()] = expression;
            }
        });

        Set<String> noTerminalSymbols = this.getNoTerminalSymbols();
        productionDivide.getContainTerminalSymbolProductions().forEach(c -> {
            List<List<String>> rightParts = c.getRightPart();
            rightParts.forEach(r -> {
                RegularExpression baseRegularExpression = RegularExpression.Empty();
                List<String> preComponents = new ArrayList<>();
                for (String s : r)
                {
                    preComponents.add(s);
                    RegularExpression periodExpression = RegularExpression.Empty();
                    if (noTerminalSymbols.contains(s))
                    {
                        // 正规文法含右递归
                        if (s.equals(c.getLeftPart()))
                        {
                            for (String component : preComponents)
                            {
                                // 寻找对应表达式生成闭包
                                TokenType tokenType = tokenTypeMap.get(component.toUpperCase());
                                RegularExpression expression = this.tokenExpressions[tokenType.getPriority()];
                                periodExpression = expression.Many();
                            }
                        }
                        TokenType tokenType = tokenTypeMap.get(s);
                        if (tokenType != null)
                        {
                            periodExpression = this.tokenExpressions[tokenType.getPriority()];
                        }
                    }
                    else
                    {
                        periodExpression = RegularExpression.CharSet(s.toCharArray());
                    }
                    baseRegularExpression.Union(periodExpression);
                }
                preComponents.clear();
            });
        });
        return expressions;
    }

    public RegularExpression transferRegularGrammar2RegularExpression(RegularGrammarProduction production)
    {
        List<List<String>> rightParts = production.getRightPart();
        boolean isFirstSetUpUnion = true;
        RegularExpression alternationExpression = null;
        for (List<String> r : rightParts)
        {
            boolean isFirstSetUpConcat = true;
            RegularExpression concatationExpression = null;
            for (String s : r)
            {
                char[] chars = s.toCharArray();
                List<Character> wrap = new ArrayList<>();
                for (char aChar : chars)
                {
                    wrap.add(aChar);
                }
                RegularExpression newExpression = RegularExpression.CharSet(wrap);
                if (isFirstSetUpConcat)
                {
                    isFirstSetUpConcat = false;
                    concatationExpression = newExpression;
                }
                else
                {
                    concatationExpression = concatationExpression.Concat(newExpression);
                }
            }
            if (isFirstSetUpUnion)
            {
                isFirstSetUpUnion = false;
                alternationExpression = concatationExpression;
            }
            else
            {
                alternationExpression = alternationExpression.Union(concatationExpression);
            }
        }
        return alternationExpression;
    }

    private boolean isNotDelimiter(char[] chars, int i)
    {
        return chars[i - 1] != '|' && chars[i - 1] != '→' && chars[i - 1] != '>';
    }

    private String buildNewRightPart(Queue<Character> queue, List<String> currentRightPart)
    {
        if (queue.isEmpty())
        {
            return null;
        }
        String newRightPartCompose = concatSubProduction(queue);
        currentRightPart.add(newRightPartCompose);
        return newRightPartCompose;
    }

    private String concatSubProduction(Queue<Character> queue)
    {
        StringBuilder stringBuilder = new StringBuilder();
        while (!queue.isEmpty())
        {
            stringBuilder.append(queue.poll());
        }
        return stringBuilder.toString();
    }

}
