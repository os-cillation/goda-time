package org.goda.chronic.tags;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;



public class ScalarMonth extends Scalar {

    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return ScalarMonth.scan(tokens, options);
        }

    };

    private static final String MONTH_PATTERN = "^\\d\\d?$";

    public ScalarMonth(Double type) {
        super(type);
    }

    public static ScalarMonth scan(Token token, Token postToken, Options options) {
        if (token.getWord()
                                         .matches(ScalarMonth.MONTH_PATTERN)) {
            int scalarValue = Integer.parseInt(token.getWord());

            if (!((scalarValue > 12) || ((postToken != null) && Scalar.TIMES.contains(postToken.getWord())))) {
                return new ScalarMonth(Double.valueOf(scalarValue));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-month-" + getType();
    }
}
