package org.goda.chronic.handlers;

import org.goda.chronic.Options;
import org.goda.chronic.repeaters.Repeater;
import org.goda.chronic.tags.Tag;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;

import org.goda.time.DateTime;
import org.goda.time.DateTimeFieldType;
import org.goda.time.MutableInterval;

import java.util.List;


public abstract class MDHandler implements IHandler {
    public MutableInterval handle(Repeater<?> month, Tag<Integer> day, List<Token> timeTokens, Options options) {
        month.setStart(new DateTime(options.getNow().getMillis()));

        MutableInterval mutableInterval = month.thisMutableInterval(options.getContext());
        DateTime dayStart = Time.construct(
                mutableInterval.getStart().get(DateTimeFieldType.year()),
                mutableInterval.getStart().get(DateTimeFieldType.monthOfYear()) , day.getType().intValue());

        return Handler.dayOrTime(dayStart, timeTokens, options);
    }
}
