package org.goda.chronic.handlers;

import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.repeaters.Repeater;
import org.goda.chronic.tags.Ordinal;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.MutableInterval;


public abstract class ORRHandler implements IHandler {
  public MutableInterval handle(List<Token> tokens, MutableInterval outerMutableInterval, Options options) {
    Repeater<?> repeater = tokens.get(1).getTag(Repeater.class);
    repeater.setStart(Time.cloneAndAdd(outerMutableInterval.getStart(), Time.SECOND, -1));
    Number ordinalValue = tokens.get(0).getTag(Ordinal.class).getType();
    MutableInterval MutableInterval = null;
    for (int i = 0; i < ordinalValue.intValue(); i++) {
      MutableInterval = repeater.nextMutableInterval(Pointer.PointerType.FUTURE);
      if (MutableInterval.getStartMillis() > outerMutableInterval.getEndMillis()) {
        MutableInterval = null;
        break;
      }
    }
    return MutableInterval;
  }
}
