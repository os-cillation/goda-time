package org.goda.chronic.tags;

import org.goda.chronic.Options;
import org.goda.chronic.utils.Token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Scalar extends Tag<Double> {

    public static final Scanner SCANNER = new Scanner() {

        public List<Token> scan(List<Token> tokens, Options options) {
            return Scalar.scan(tokens, options);
        }

    };

    private static final String SCALAR_PATTERN = "^[\\d|\\.]*$";
    public static Set<String> TIMES = new HashSet<String>();

    static {
        Scalar.TIMES.add("am");
        Scalar.TIMES.add("pm");
        Scalar.TIMES.add("morning");
        Scalar.TIMES.add("afternoon");
        Scalar.TIMES.add("evening");
        Scalar.TIMES.add("night");
    }

    public Scalar(Double type) {
        super(type);
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token postToken = null;

            if (i < (tokens.size() - 1)) {
                postToken = tokens.get(i + 1);
            }

            Scalar t;
            t = Scalar.scan(token, postToken, options);

            if (t != null) {
                token.tag(t);
            }

            t = ScalarDay.scan(token, postToken, options);

            if (t != null) {
                token.tag(t);
            }

            t = ScalarMonth.scan(token, postToken, options);

            if (t != null) {
                token.tag(t);
            }

            t = ScalarYear.scan(token, postToken, options);

            if (t != null) {
                token.tag(t);
            }
        }

        return tokens;
    }

    public static Scalar scan(Token token, Token postToken, Options options) {
        if(options.isDebug()){
            System.out.println("Scalar.scan "+token+" "+postToken);
        }
        if (token.getWord()
                                     .matches(Scalar.SCALAR_PATTERN)) {
            if (
                (token.getWord() != null) && (token.getWord()
                                                       .length() > 0) &&
                    !((postToken != null) && Scalar.TIMES.contains(postToken.getWord()))) {
                return new Scalar(Double.valueOf(token.getWord()));
            }
        } else {
            Double intStrValue = null;
            try{
                Double.valueOf(token.getWord());
            } catch(NumberFormatException e){
                ;// noop.
            }

            if (intStrValue != null) {
                return new Scalar(intStrValue);
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "scalar";
    }
}
