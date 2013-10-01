package org.goda.chronic.tags;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.Map;


public class SeparatorAt extends Separator {

    public static final Scanner SCANNER = new Scanner() {

        public List<Token> scan(List<Token> tokens, Options options) {
            return SeparatorAt.scan(tokens, options);
        }

    };
    private static final String AT_PATTERN = "^(at|@)$";

    public SeparatorAt(Separator.SeparatorType type) {
        super(type);
    }

    public static SeparatorAt scan(Token token, Options options) {
        Map<String, Separator.SeparatorType> scanner = new HashMap<String, Separator.SeparatorType>();
        scanner.put(SeparatorAt.AT_PATTERN, Separator.SeparatorType.AT);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                               .matches(SeparatorAt.AT_PATTERN)) {
                return new SeparatorAt(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-at";
    }
}
