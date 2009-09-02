package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Grabber extends Tag<Grabber.Relative> {

    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return Grabber.scan(tokens, options);
        }

    };

    private static final String THIS_PATTERN = "this";
    private static final String NEXT_PATTERN = "next";
    private static final String LAST_PATTERN = "last";

    public Grabber(Grabber.Relative type) {
        super(type);
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (Token token : tokens) {
            Grabber t = Grabber.scanForAll(token, options);

            if (t != null) {
                token.tag(t);
            }
        }

        return tokens;
    }

    public static Grabber scanForAll(Token token, Options options) {
        Map<String, Grabber.Relative> scanner = new HashMap<String, Grabber.Relative>();
        scanner.put(Grabber.LAST_PATTERN, Grabber.Relative.LAST);
        scanner.put(Grabber.NEXT_PATTERN, Grabber.Relative.NEXT);
        scanner.put(Grabber.THIS_PATTERN, Grabber.Relative.THIS);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                               .matches(scannerItem)) {
                return new Grabber(scanner.get(scannerItem));
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "grabber-" + getType();
    }
    public static enum Relative {LAST, NEXT, THIS;
    }
}
