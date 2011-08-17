package org.goda.chronic.repeaters;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Tag;
import org.goda.chronic.utils.Token;
import org.goda.time.MutableInterval;



public abstract class Repeater<T> extends Tag<T> implements Comparable<Repeater<?>> {

    public static final Scanner SCANNER = new Scanner(){

        public List<Token> scan(List<Token> tokens, Options options) {
            return Repeater.scan(tokens, options);
        }

    };

  public Repeater(T type) {
    super(type);
  }

  public static List<Token> scan(List<Token> tokens) {
    return Repeater.scan(tokens, new Options());
  }

  public static List<Token> scan(List<Token> tokens, Options options) {
    for (Token token : tokens) {
      Tag<?> t;
      t = RepeaterMonthName.scan(token);
      if (t != null) {
        token.tag(t);
      }
      t = RepeaterDayName.scan(token);
      if (t != null) {
        token.tag(t);
      }
      t = RepeaterDayPortion.scan(token);
      if (t != null) {
        token.tag(t);
      }
      t = RepeaterTime.scan(token, tokens, options);
      if (t != null) {
        token.tag(t);
      }
      t = RepeaterUnit.scan(token);
      if (t != null) {
        token.tag(t);
      }
    }
    return tokens;
  }

  public int compareTo(Repeater<?> other) {
    return Integer.valueOf(getWidth()).compareTo(Integer.valueOf(other.getWidth()));
  }

  /**
   * returns the width (in seconds or months) of this repeatable.
   */
  public abstract int getWidth();

  /** 
   * returns the next occurance of this repeatable.
   */
  public MutableInterval nextMutableInterval(Pointer.PointerType pointer) {
    if (getNow() == null) {
      throw new IllegalStateException("Start point must be set before calling #next");
    }
    return _nextMutableInterval(pointer);
  }

  protected abstract MutableInterval _nextMutableInterval(Pointer.PointerType pointer);

  public MutableInterval thisMutableInterval(Pointer.PointerType pointer) {
    if (getNow() == null) {
      throw new IllegalStateException("Start point must be set before calling #this");
    }
    return _thisMutableInterval(pointer);
  }

  protected abstract MutableInterval _thisMutableInterval(Pointer.PointerType pointer);

  public abstract MutableInterval getOffset(MutableInterval MutableInterval, double amount, Pointer.PointerType pointer);

  @Override
  public String toString() {
    return "repeater";
  }
}
