package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.List;


public class ScalarDay extends Scalar {
    public static final Scanner SCANNER = new Scanner() {
            public List<Token> scan(List<Token> tokens, Options options) {
                return ScalarDay.scan(tokens, options);
            }
        };

    private static final String DAY_PATTERN = "^\\d\\d?$";

    public ScalarDay(Double type) {
        super(type);
    }

    public static ScalarDay scan(Token token, Token postToken, Options options) {
        if (token.getWord()
                     .matches(DAY_PATTERN)) {
            int scalarValue = Integer.parseInt(token.getWord());

            if (!((scalarValue > 31) || ((postToken != null) && Scalar.TIMES.contains(postToken.getWord())))) {
                return new ScalarDay(Double.valueOf(scalarValue));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-day-" + getType();
    }
}
