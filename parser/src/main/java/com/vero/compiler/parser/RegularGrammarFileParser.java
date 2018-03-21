package com.vero.compiler.parser;


import static com.vero.compiler.lexer.expression.RegularExpression.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vero.compiler.exception.TokenMatchLostException;
import com.vero.compiler.exception.TokenTypeNoDefinitionException;
import com.vero.compiler.lexer.expression.RegularExpression;
import com.vero.compiler.lexer.expression.RegularExpressionType;
import com.vero.compiler.lexer.token.TokenType;

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

    public RegularGrammarFileParser()
    {
        nonTerminalSymbol = new ArrayList<>();
        terminalSymbol = new ArrayList<>();
        grammarProductionDefinitions = new ArrayList<>();
    }

    public RegularExpression[] parse(File grammarSource)
        throws IOException
    {
        BufferedReader bufferedReader = loadGrammarSource(grammarSource);
        String str = null;
        while ((str = bufferedReader.readLine()) != null)
        {
            grammarProductionDefinitions.add(str.replace(" ", ""));
        }
        this.parseDefinitions();
        this.transfer();
        return this.tokenExpressions;
    }

    private BufferedReader loadGrammarSource(File grammarSource)
        throws UnsupportedEncodingException
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
        return new BufferedReader(new InputStreamReader(getFileReader(), "Unicode"));
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
            for (Integer i = 0; i < chars.length;)
            {
                if (!isScanningRightPart)
                {
                    if (chars[i] == '→')
                    {
                        isScanningRightPart = true;
                        ++i;
                        continue;
                    }
                    if (chars[i] == '<')
                    {
                        do
                        {
                            queue.add(chars[i]);
                        }
                        while (chars[i++ ] != '>');
                        currentLeftPart = concatSubProduction(queue);
                        log.debug("Parser gained a new terminal symbol : [{}]", currentLeftPart);
                    }
                }
                else
                {
                    if (chars[i] == '<')
                    {
                        boolean isEnd = false;
                        while (!isEnd)
                        {
                            do
                            {
                                queue.add(chars[i]);
                            }
                            while (chars[i++ ] != '>');
                            String newRightPartCompose = buildNewRightPart(queue,
                                currentRightPart);
                            log.debug(
                                "Parser gained production right part no terminal symbol : [{}]",
                                newRightPartCompose);
                            if (i + 1 <= chars.length && chars[i] != '<' && chars[i] != '|')
                            {
                                i = handleInputAlphabet(queue, chars, currentRightPart, i);
                            }
                            if (i + 1 > chars.length || chars[i] == '|')
                            {
                                isEnd = true;
                            }
                        }

                    }
                    else if (chars[i] == '|')
                    {
                        i++ ;
                        continue;
                    }
                    else if (chars[i] != '"')
                    {
                        i = handleInputAlphabet(queue, chars, currentRightPart, i);
                    }
                    else if (chars[i] == '"')
                    {
                        do
                        {
                            queue.add(chars[i]);
                        }
                        while (chars[++i] != '"');
                        queue.add(chars[i]);
                        String newRightPartCompose = buildNewRightPart(queue, currentRightPart);
                        log.debug("Parser gained production right part literal string : [{}]",
                            newRightPartCompose);
                        ++i;
                    }
                    buildNewRightPart(queue, currentRightPart);
                    rightParts.add(currentRightPart);
                    currentRightPart = new ArrayList<>();
                }
            }
            if (currentLeftPart == null)
            {
                return;
            }
            RegularGrammarProduction grammarProduction = new RegularGrammarProduction(
                getNoTerminalSymbols(), currentLeftPart, rightParts);
            grammarProduction.setRightPart(rightParts);
            grammarProductionMap.put(currentLeftPart, grammarProduction);
            grammarProductions.add(grammarProduction);
        });
        grammarProductionMap.forEach(
            (leftName,
             production) -> production.setGlobalRegularGrammarProductions(grammarProductionMap));
    }

    private Integer handleInputAlphabet(Queue<Character> queue, char[] chars,
                                        List<String> currentRightPart, Integer i)
    {
        while (i != chars.length && chars[i] != '<')
        {
            if (chars[i] == '|')
            {
                i++ ;
                continue;
            }
            queue.add(chars[i]);
            i++ ;
        }
        String newRightPartCompose = buildNewRightPart(queue, currentRightPart);
        log.debug("Parser gained production right part contactable string : [{}]",
            newRightPartCompose);
        if (i != chars.length && chars[i] == '<')
        {
            do
            {
                queue.add(chars[i]);
            }
            while (chars[i++ ] != '>');
            newRightPartCompose = buildNewRightPart(queue, currentRightPart);
            log.debug("Parser gained production right part no terminal symbol : [{}]",
                newRightPartCompose);
        }
        return i;
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

    public RegularExpression[] transfer()
    {
        Map<String, TokenType> tokenTypeMap = TokenType.getTokenTypeMap();
        this.productionDivide = group();
        Set<String> noTerminalSymbols = this.getNoTerminalSymbols();
        Queue<RegularGrammarProduction> queue = new LinkedList<>();
        queue.addAll(productionDivide.getNoContainTerminalSymbolProductions());
        queue.addAll(productionDivide.getContainTerminalSymbolProductions());
        while (!queue.isEmpty())
        {
            RegularGrammarProduction c = queue.poll();
            List<List<String>> rightParts = c.getRightPart();
            TokenType currentTokenType = TokenType.getTokenTypeMap().get(
                c.getLeftPart().toUpperCase());
            validateLeftPart(c, currentTokenType);
            RegularExpression baseRegularExpression = Empty();
            boolean isDelayed = false;
            Set<RegularExpression> computedRegularExpressions = new HashSet<>();
            for (List<String> r : rightParts)
            {
                RegularExpression concatExpression = Empty();
                Queue<String> preComponents = new LinkedList<>();
                for (String s : r)
                {
                    RegularExpression periodExpression = Empty();
                    if (noTerminalSymbols.contains(s))
                    {
                        // 正规文法含右递归
                        if (s.equals(c.getLeftPart()))
                        {
                            while (!preComponents.isEmpty())
                            {
                                String component = preComponents.poll();
                                // 寻找对应表达式生成闭包
                                RegularExpression expression = findGeneratedExpression(
                                    tokenTypeMap, component);
                                if (expression == null)
                                {
                                    queue.add(c);
                                    isDelayed = true;
                                    preComponents.clear();
                                    break;
                                }
                                periodExpression = concat(periodExpression, expression);
                            }
                            RegularExpression union = RegularExpression.Empty();
                            for (RegularExpression ex : computedRegularExpressions)
                            {
                                // 求所有闭包
                                union = union(union, ex);
                            }
                            union = union.Many();
                            periodExpression = union;
                        }
                        else
                        {
                            TokenType tokenType = tokenTypeMap.get(s.toUpperCase());
                            if (tokenType != null)
                            {
                                RegularExpression expression = this.tokenExpressions[tokenType.getPriority()];
                                if (expression == null)
                                {
                                    queue.add(c);
                                    isDelayed = true;
                                    preComponents.clear();
                                    break;
                                }
                                periodExpression = expression;
                            }
                        }
                    }
                    else
                    {
                        if (s.indexOf('"') == -1 && s.length() != 1)
                        {
                            String regex = ".*[0-9a-zA-Z]+.*";
                            Matcher m = Pattern.compile(regex).matcher(s);
                            if (!m.matches())
                            {
                                char chars[] = s.toCharArray();
                                for (char z : chars)
                                {
                                    periodExpression = union(periodExpression, Symbol(z));
                                }
                            }
                            else
                            {
                                periodExpression = CharSet(s.toCharArray());
                            }
                        }
                        else if (s.length() == 1)
                        {
                            periodExpression = Symbol(s.charAt(0));
                        }
                        else
                        {
                            periodExpression = Literal(s.substring(1, s.length() - 1));
                        }
                    }
                    preComponents.add(s);
                    concatExpression = concat(concatExpression, periodExpression);
                    computedRegularExpressions.add(concatExpression);
                }
                if (isDelayed)
                {
                    break;
                }
                baseRegularExpression = union(baseRegularExpression, concatExpression);
            }
            if (!isDelayed)
            {
                this.tokenExpressions[currentTokenType.getPriority()] = baseRegularExpression;
            }
        }
        return this.tokenExpressions;
    }

    private void validateLeftPart(RegularGrammarProduction c, TokenType currentTokenType)
    {
        if (currentTokenType == null)
        {
            throw new TokenTypeNoDefinitionException(
                "[ " + c.getLeftPart() + " ]" + " don't exist definition in system.All no terminals should be one of "
                                                     + TokenType.getNoTerminalNames());
        }
    }

    private RegularExpression findGeneratedExpression(Map<String, TokenType> tokenTypeMap,
                                                      String component)
    {
        TokenType tokenType = tokenTypeMap.get(component.toUpperCase());
        if (tokenType == null)
        {
            throw new TokenMatchLostException(
                "Could not find corresponding no terminal production");
        }
        return this.tokenExpressions[tokenType.getPriority()];
    }

    // private RegularExpression buildPerTokenExpression(Map<String, TokenType> tokenTypeMap,
    // RegularGrammarProduction right)
    // {
    // for (List<String> rightPart : right.getRightPart())
    // {
    // Queue<String> preComponents = new LinkedList<>();
    // for (String part : rightPart)
    // {
    // {
    // TokenType partTokenType = tokenTypeMap.get(part);
    // RegularExpression periodExpression = this.tokenExpressions[partTokenType.getPriority()];
    // RegularGrammarProduction production = this.getGrammarProductionMap().get(
    // part);
    // if (periodExpression == null)
    // {
    // if (production != null)
    // {
    // periodExpression = buildPerTokenExpression(tokenTypeMap, production);
    // }
    // else
    // {
    // if (part.indexOf('"') == -1 && part.length() != 1)
    // {
    // return CharSet(part.toCharArray());
    // }
    // else if (part.length() == 1)
    // {
    // return Symbol(part.charAt(0));
    // }
    // else
    // {
    // return Literal(part.substring(1, part.length() - 1));
    // }
    // }
    // }
    // else {
    // if (production != null)
    // {
    // //右递归式
    // if (production.getLeftPart().equals(part)) {
    // while (!preComponents.isEmpty()) {
    // String component = preComponents.poll();
    // // 寻找对应表达式生成闭包
    // TokenType tokenType = tokenTypeMap.get(component.toUpperCase());
    // // 如果当前Token的表达式还未生成,重新入队;
    // if (tokenType == null)
    // {
    // throw new TokenMatchLostException(
    // "Could not find corresponding no terminal production");
    // }
    // RegularExpression expression = this.tokenExpressions[tokenType.getPriority()];
    // periodExpression = concat(periodExpression, expression);
    // }
    // periodExpression = buildPerTokenExpression(tokenTypeMap, production);
    // }
    // else{
    // periodExpression = concat(periodExpression, buildPerTokenExpression(tokenTypeMap,
    // production));
    // }
    //
    // }
    // }
    // }
    // preComponents.add(part);
    // }
    // }
    // }

    private RegularExpression union(RegularExpression baseRegularExpression,
                                    RegularExpression unionExpression)
    {
        if (!isEmptyExpression(unionExpression) && isEmptyExpression(baseRegularExpression))
        {
            baseRegularExpression = unionExpression;
        }
        else
        {
            baseRegularExpression = baseRegularExpression.Union(unionExpression);
        }
        return baseRegularExpression;
    }

    private boolean isEmptyExpression(RegularExpression concatExpression)
    {
        return concatExpression.getExpressionType().equals(RegularExpressionType.Empty);
    }

    private RegularExpression concat(RegularExpression concatExpression,
                                     RegularExpression periodExpression)
    {
        if (!isEmptyExpression(periodExpression) && isEmptyExpression(concatExpression))
        {
            concatExpression = periodExpression;
        }
        else
        {
            concatExpression = concatExpression.Concat(periodExpression);
        }
        return concatExpression;
    }

    private RegularExpression many(RegularExpression baseExpression,
                                   RegularExpression closureExpression)
    {
        if (!isEmptyExpression(closureExpression) && isEmptyExpression(baseExpression))
        {
            baseExpression = closureExpression;
        }
        else
        {
            baseExpression = closureExpression.Many();
        }
        return baseExpression;
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
                RegularExpression newExpression = CharSet(wrap);
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
