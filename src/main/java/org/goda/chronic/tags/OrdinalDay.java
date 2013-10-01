package org.goda.chronic.tags;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;



public class OrdinalDay extends Ordinal {

    public static final Scanner SCANNER = new Scanner() {

        public List<Token> scan(List<Token> tokens, Options options) {
            return OrdinalDay.scan(tokens, options);
        }

    };

    public OrdinalDay(Integer type) {
        super(type);
    }

    public static OrdinalDay scan(Token token) {
        if (token.getWord().matches(Ordinal.ORDINAL_PATTERN)) {
            int ordinalValue = Integer.parseInt(token.getWord().substring(0, token.getWord().length() -2));

            if (!(ordinalValue > 31)) {
                return new OrdinalDay(Integer.valueOf(ordinalValue));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-day-" + getType();
    }
}
