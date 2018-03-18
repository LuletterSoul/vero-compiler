package com.vero.compiler.parser;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vero.compiler.scan.expression.RegularExpression;

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

    private List<String> grammarProductionDefinitions;

    private Map<String, RegularExpression> production2RegularExpression = new HashMap<>();

    private Map<String, RegularGrammarProduction> grammarProductionMap = new HashMap<>();

    private List<RegularGrammarProduction> grammarProductions = new ArrayList<>();

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
            List<List<Object>> rightParts = new ArrayList<>();
            List<Object> currentRightPart = new ArrayList<>();
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

    public List<RegularGrammarProduction> groupNoContainsNoTerminalSymbolProductions()
    {
        List<RegularGrammarProduction> productions = new ArrayList<>();
        Set<String> terminalSymbols = getNoTerminalSymbols();
        this.grammarProductions.forEach(p -> {
            List<List<Object>> rightParts = p.getRightPart();
            AtomicBoolean isContainNoTerminalSymbols = new AtomicBoolean(false);
            rightParts.forEach(rightPart -> rightPart.forEach(r -> terminalSymbols.forEach(t -> {
                if (t.equals(r.toString()))
                {
                    isContainNoTerminalSymbols.set(true);
                }
            })));
            if (!isContainNoTerminalSymbols.get())
            {
                productions.add(p);
            }
        });
        return productions;
    }

    public List<RegularExpression> transfer()
    {
        List<RegularGrammarProduction> productions = groupNoContainsNoTerminalSymbolProductions();
        List<RegularExpression> expressions = new ArrayList<>();
        productions.forEach(p -> {
            RegularExpression expression = transferRegularGrammar2RegularExpression(p)
            expressions.add(expression);
            this.production2RegularExpression.put(p.getLeftPart(),
                    expression);
        });
        return expressions;
    }

    public RegularExpression transferRegularGrammar2RegularExpression(RegularGrammarProduction production)
    {
        List<List<Object>> rightParts = production.getRightPart();
        boolean isFirstSetUpUnion = true;
        RegularExpression alternationExpression = null;
        for (List<Object> r : rightParts)
        {
            boolean isFirstSetUpConcat = true;
            RegularExpression concatationExpression = null;
            for (Object o : r)
            {
                if (o instanceof String)
                {
                    String s = (String)o;
                    char[] chars = ((String)o).toCharArray();
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

    private String buildNewRightPart(Queue<Character> queue, List<Object> currentRightPart)
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
