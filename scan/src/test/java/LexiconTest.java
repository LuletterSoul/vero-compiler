import static com.vero.compiler.scan.expression.RegularExpression.Literal;
import static com.vero.compiler.scan.expression.RegularExpression.Range;

import org.junit.Assert;
import org.junit.Test;

import com.vero.compiler.scan.FiniteAutomationMachineEngine;
import com.vero.compiler.scan.ScannerInfo;
import com.vero.compiler.scan.compress.CompressedTransitionTable;
import com.vero.compiler.scan.converter.NFAConverter;
import com.vero.compiler.scan.converter.RegularExpressionConverter;
import com.vero.compiler.scan.generator.DFAModel;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;
import com.vero.compiler.scan.token.Token;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 20:40 2018/3/14.
 * @since vero-compiler
 */

public class LexiconTest
{

    @Test
    public void testLexerStateToDFA()
    {
        Lexicon lexicon = new Lexicon();
        Lexer global = lexicon.getDefaultLexer();
        Lexer keywords = global.createSubLexer();
        Lexer xml = keywords.createSubLexer();
        Token ID = global.defineToken(
            Range('a', 'z').Concat(Range('a', 'z').Union(Range('0', '9')).Many()));

        Token NUM = global.defineToken(Range('0', '9').Many1());

        Token IF = keywords.defineToken(Literal("if"));

        Token ERROR = global.defineToken(Range(Character.MIN_VALUE, (char)255));

        Token ELSE = keywords.defineToken(Literal("else"));

        Token XMLNS = xml.defineToken(Literal("xmlns"));

        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
        FiniteAutomationMachineEngine engine = new FiniteAutomationMachineEngine(
            tc.getCharClassTable(), tc.getRealCompressedTransitionTable());
        ScannerInfo info = new ScannerInfo(tc.getRealCompressedTransitionTable(),
            tc.getCharClassTable(), dfaModel.getAcceptTables(), lexicon.getTokenList().size());

        // 默认上下文Lexer,环境为global
        //if应该被识别为标识符
        engine.inputString("if");
        Assert.assertEquals(ID.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("asfagth31234");
        Assert.assertEquals(ID.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("123456");
        Assert.assertEquals(NUM.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        //非法字符
        engine.resetMachineState();
        engine.inputString("A");
        Assert.assertEquals(ERROR.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        engine.resetMachineState();
        engine.inputString("AAAA");
        Assert.assertTrue(engine.isAtStoppedState());

        //后有空格,在全局环境下自动机应该停机
        engine.resetMachineState();
        engine.inputString("if ");
        Assert.assertTrue(engine.isAtStoppedState());

        //切换到关键字环境
        engine.resetMachineState();
        info.setLexerState(keywords.getIndex());
        engine.inputString("else");
        Assert.assertEquals(ELSE.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        //if应该被识别为关键字
        engine.resetMachineState();
        engine.inputString("if");
        Assert.assertEquals(IF.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        //xmlns应该被识别为标识符
        engine.resetAndInputString("xmlns");
        Assert.assertEquals(ID.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));

        //切换到xml环境
        info.setLexerState(xml.getIndex());
        engine.resetAndInputString("xmlns");
        Assert.assertEquals(XMLNS.getIndex(), info.getTokenIndex(engine.getCurrentStateIndex()));
    }
}
