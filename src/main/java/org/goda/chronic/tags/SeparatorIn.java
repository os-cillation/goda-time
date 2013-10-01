package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeparatorIn extends Separator {
    public static final Scanner SCANNER = new Scanner() {
            public List<Token> scan(List<Token> tokens, Options options) {
                return SeparatorIn.scan(tokens, options);
            }
        };

    private static final String IN_PATTERN = "^in$";

    public SeparatorIn(Separator.SeparatorType type) {
        super(type);
    }

    public static SeparatorIn scan(Token token, Options options) {
        Map<String, Separator.SeparatorType> scanner = new HashMap<String, Separator.SeparatorType>();
        scanner.put(SeparatorIn.IN_PATTERN, Separator.SeparatorType.IN);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                         .matches(scannerItem)) {
                return new SeparatorIn(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-in";
    }
}
