package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Pointer extends Tag<Pointer.PointerType> {
    public static final Scanner SCANNER = new Scanner() {
            public List<Token> scan(List<Token> tokens, Options options) {
                return Pointer.scan(tokens, options);
            }
        };

    private static final String IN_PATTERN = "\\bin\\b";
    private static final String FUTURE_PATTERN = "\\bfuture\\b";
    private static final String PAST_PATTERN = "\\bpast\\b";

    public Pointer(Pointer.PointerType type) {
        super(type);
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (Token token : tokens) {
            Pointer t = Pointer.scanForAll(token, options);

            if (t != null) {
                token.tag(t);
            }
        }

        return tokens;
    }

    public static Pointer scanForAll(Token token, Options options) {
        Map<String, Pointer.PointerType> scanner = new HashMap<String, Pointer.PointerType>();
        scanner.put(Pointer.PAST_PATTERN, Pointer.PointerType.PAST);
        scanner.put(Pointer.FUTURE_PATTERN, Pointer.PointerType.FUTURE);
        scanner.put(Pointer.IN_PATTERN, Pointer.PointerType.FUTURE);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                         .matches(scannerItem)) {
                return new Pointer(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "pointer-" + getType();
    }
    public enum PointerType {FUTURE, NONE, PAST;
    }
}
