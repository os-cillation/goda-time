package org.goda.chronic.handlers;

import java.util.List;

import org.goda.chronic.Options;
import org.goda.chronic.tags.ScalarDay;
import org.goda.chronic.tags.ScalarMonth;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.DateTimeFieldType;
import org.goda.time.MutableInterval;

public class SmSdHandler implements IHandler {
  public MutableInterval handle(List<Token> tokens, Options options) {
    int month = tokens.get(0).getTag(ScalarMonth.class).getType().intValue();
    int day = tokens.get(1).getTag(ScalarDay.class).getType().intValue();
    DateTime start = Time.construct(options.getNow().get(DateTimeFieldType.year()), month, day);
    DateTime end = Time.cloneAndAdd(start, Time.DAY_OF_MONTH, 1);
    return new MutableInterval(start, end);
  }

}
