package com.vero.compiler.scan.token;


import com.vero.compiler.scan.exception.NFAModalInValidException;
import com.vero.compiler.scan.expression.RegularExpression;
import com.vero.compiler.scan.generator.NFAConverter;
import com.vero.compiler.scan.generator.NFAModel;
import com.vero.compiler.scan.lexer.Lexer;
import com.vero.compiler.scan.lexer.Lexicon;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 13:08 2018/3/11.
 * @since vero-compiler
 */

@Data
public class TokenInfo
{

    private Token tag;

    private Lexicon lexicon;

    private Lexer state;

    private RegularExpression definition;

    public TokenInfo(RegularExpression definition,Lexicon lexicon, Lexer state, Token tag)
    {
        this.tag = tag;
        this.lexicon = lexicon;
        this.state = state;
        this.definition = definition;
    }

    public NFAModel createFiniteAutomationModel(NFAConverter converter)
    {
        NFAModel nfaModel = converter.convert(getDefinition());

        if (nfaModel == null)
        {
            throw new NFAModalInValidException("NFA modal could not be null.");
        }

        nfaModel.getTailState().setTokenIndex(getTag().getIndex());
        return nfaModel;
    }
}
