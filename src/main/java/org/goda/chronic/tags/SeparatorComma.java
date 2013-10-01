package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeparatorComma extends Separator {
    public static final Scanner SCANNER = new Scanner() {
            public List<Token> scan(List<Token> tokens, Options options) {
                return SeparatorComma.scan(tokens, options);
            }
        };

    private static final String COMMA_PATTERN = "^,$";

    public SeparatorComma(Separator.SeparatorType type) {
        super(type);
    }

    public static SeparatorComma scan(Token token, Options options) {
        Map<String, Separator.SeparatorType> scanner = new HashMap<String, Separator.SeparatorType>();
        scanner.put(SeparatorComma.COMMA_PATTERN, Separator.SeparatorType.COMMA);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                               .matches(scannerItem)) {
                return new SeparatorComma(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-comma";
    }
}
