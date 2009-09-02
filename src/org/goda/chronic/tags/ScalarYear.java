package org.goda.chronic.tags;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;


public class ScalarYear extends Scalar {

    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return ScalarYear.scan(tokens, options);
        }

    };

    public static final String YEAR_PATTERN = "^([1-9]\\d)?\\d\\d?$";

    public ScalarYear(Integer type) {
        super(type);
    }

    public static ScalarYear scan(Token token, Token postToken, Options options) {
        if (token.getWord().matches(ScalarYear.YEAR_PATTERN)) {
            int scalarValue = Integer.parseInt(token.getWord());

            if (!((postToken != null) && Scalar.TIMES.contains(postToken.getWord()))) {
                if (scalarValue <= 37) {
                    scalarValue += 2000;
                } else if ((scalarValue <= 137) && (scalarValue >= 69)) {
                    scalarValue += 1900;
                }

                return new ScalarYear(Integer.valueOf(scalarValue));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-year-" + getType();
    }
}
