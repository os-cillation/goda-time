package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.List;


public class Separator extends Tag<Separator.SeparatorType> {

    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return Separator.scan(tokens, options);
        }

    };

    public Separator(Separator.SeparatorType type) {
        super(type);
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (Token token : tokens) {
            Separator t;
            t = SeparatorComma.scan(token, options);

            if (t != null) {
                token.tag(t);
            }

            t = SeparatorSlashOrDash.scan(token, options);

            if (t != null) {
                token.tag(t);
            }

            t = SeparatorAt.scan(token, options);

            if (t != null) {
                token.tag(t);
            }

            t = SeparatorIn.scan(token, options);

            if (t != null) {
                token.tag(t);
            }
        }

        return tokens;
    }

    @Override
    public String toString() {
        return "separator";
    }
    public static enum SeparatorType {AT, COMMA, DASH, IN, NEWLINE, SLASH;
    }
}
