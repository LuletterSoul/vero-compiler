import static com.vero.compiler.scan.expression.RegularExpression.Range;

import org.junit.Test;

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
        RegularExpressionConverter converter = new NFAConverter(
            lexicon.getCompactCharSetManager());
        DFAModel dfaModel = DFAModel.build(lexicon, converter);
        CompressedTransitionTable tc = CompressedTransitionTable.compress(dfaModel);
    }
}
