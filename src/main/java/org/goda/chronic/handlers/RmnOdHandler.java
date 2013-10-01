package org.goda.chronic.handlers;

import org.goda.chronic.Options;
import org.goda.chronic.repeaters.RepeaterMonthName;
import org.goda.chronic.tags.OrdinalDay;
import org.goda.util.CollectionUtils;
import org.goda.chronic.utils.Token;

import org.goda.time.MutableInterval;

import java.util.List;


public class RmnOdHandler extends MDHandler {
    public MutableInterval handle(List<Token> tokens, Options options) {
        return handle(
            tokens.get(0).getTag(RepeaterMonthName.class), tokens.get(1).getTag(OrdinalDay.class),
            CollectionUtils.subList(tokens, 2, tokens.size()), options);
    }
}
