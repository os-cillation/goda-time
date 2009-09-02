package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeZone extends Tag<Object> {

    public static final Scanner SCANNER  = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return TimeZone.scan(tokens, options);
        }

    };

    private static final String TIMEZONE_PATTERN = "[pmce][ds]t";
    public static final Object TZ = new Object();

    public TimeZone() {
        super(null);
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (Token token : tokens) {
            TimeZone t = TimeZone.scanForAll(token, options);

            if (t != null) {
                token.tag(t);
            }
        }

        return tokens;
    }

    public static TimeZone scanForAll(Token token, Options options) {
        Map<String, Object> scanner = new HashMap<String, Object>();
        scanner.put(TimeZone.TIMEZONE_PATTERN, null);

        for (String scannerItem : scanner.keySet()) {
            if (token.getWord()
                               .matches(scannerItem)) {
                return new TimeZone();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "timezone";
    }
}
