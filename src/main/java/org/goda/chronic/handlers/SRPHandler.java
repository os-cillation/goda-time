package org.goda.chronic.handlers;

import java.util.List;
import org.goda.chronic.Chronic;
import org.goda.chronic.Options;
import org.goda.chronic.repeaters.Repeater;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Scalar;
import org.goda.chronic.utils.Token;
import org.goda.time.MutableInterval;


public class SRPHandler implements IHandler {

  public MutableInterval handle(List<Token> tokens, MutableInterval MutableInterval, Options options) {
    double distance = ((Number)tokens.get(0).getTag(Scalar.class).getType()).doubleValue();
    Repeater<?> repeater = tokens.get(1).getTag(Repeater.class);
    Pointer.PointerType pointer = tokens.get(2).getTag(Pointer.class).getType();
    return repeater.getOffset(MutableInterval, distance, pointer);
  }

  public MutableInterval handle(List<Token> tokens, Options options) {
    Repeater<?> repeater = tokens.get(1).getTag(Repeater.class);
    // DIFF: Missing fortnight
    /*
    MutableInterval MutableInterval;
    if (repeater instanceof RepeaterYear || repeater instanceof RepeaterSeason || repeater instanceof RepeaterSeasonName || repeater instanceof RepeaterMonth || repeater instanceof RepeaterMonthName || repeater instanceof RepeaterWeek) {
      MutableInterval = chronic.parse("this hour", new Options(chronic.getNow(), false));
    }
    else if (repeater instanceof RepeaterWeekend || repeater instanceof RepeaterDay || repeater instanceof RepeaterDayName || repeater instanceof RepeaterDayPortion || repeater instanceof RepeaterHour) {
      MutableInterval = chronic.parse("this minute", new Options(chronic.getNow(), false));
    }
    else if (repeater instanceof RepeaterMinute || repeater instanceof RepeaterSecond) {
      MutableInterval = chronic.parse("this second", new Options(chronic.getNow(), false));
    }
    else {
      throw new IllegalArgumentException("Invalid repeater: " + repeater);
    }
    */
    MutableInterval MutableInterval = Chronic.parse("this second", new Options(options.getNow(), false));
    return handle(tokens, MutableInterval, options);
  }
}
