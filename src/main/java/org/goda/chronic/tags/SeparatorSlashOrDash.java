package org.goda.chronic.tags;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.Map;


public class SeparatorSlashOrDash extends Separator {
    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return SeparatorSlashOrDash.scan(tokens, options);
        }

    };
    private static final String SLASH_PATTERN = "^/$";
    private static final String DASH_PATTERN = "^-$";

    public SeparatorSlashOrDash(Separator.SeparatorType type) {
        super(type);
    }

    public static SeparatorSlashOrDash scan(Token token, Options options) {
        Map<String, Separator.SeparatorType> scanner = new HashMap<String, Separator.SeparatorType>();
        scanner.put(SeparatorSlashOrDash.DASH_PATTERN, Separator.SeparatorType.DASH);
        scanner.put(SeparatorSlashOrDash.SLASH_PATTERN, Separator.SeparatorType.SLASH);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                               .matches(scannerItem)) {
                return new SeparatorSlashOrDash(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "-slashordash-" + getType();
    }
}
