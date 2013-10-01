package org.goda.chronic.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.goda.chronic.Options;
import org.goda.chronic.repeaters.EnumRepeaterDayPortion;
import org.goda.chronic.repeaters.IntegerRepeaterDayPortion;
import org.goda.chronic.repeaters.Repeater;
import org.goda.chronic.repeaters.RepeaterDayName;
import org.goda.chronic.repeaters.RepeaterDayPortion;
import org.goda.chronic.repeaters.RepeaterMonthName;
import org.goda.chronic.repeaters.RepeaterTime;
import org.goda.chronic.tags.Grabber;
import org.goda.chronic.tags.Ordinal;
import org.goda.chronic.tags.OrdinalDay;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.tags.Scalar;
import org.goda.chronic.tags.ScalarDay;
import org.goda.chronic.tags.ScalarMonth;
import org.goda.chronic.tags.ScalarYear;
import org.goda.chronic.tags.Separator;
import org.goda.chronic.tags.SeparatorAt;
import org.goda.chronic.tags.SeparatorComma;
import org.goda.chronic.tags.SeparatorIn;
import org.goda.chronic.tags.SeparatorSlashOrDash;
import org.goda.chronic.tags.Tag;
import org.goda.chronic.tags.TimeZone;
import org.goda.util.CollectionUtils;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;


public class Handler {
  private static Map<Handler.HandlerType, List<Handler>> _definitions;

  public static enum HandlerType {
    TIME, DATE, ANCHOR, ARROW, NARROW
  }

  private HandlerPattern[] _patterns;
  private IHandler _handler;
  private boolean _compatible;

  public Handler(IHandler handler, HandlerPattern... patterns) {
    this(handler, true, patterns);
  }
  
  public Handler(IHandler handler, boolean compatible, HandlerPattern... patterns) {
    _handler = handler;
    _compatible = compatible;
    _patterns = patterns;
  }
  
  public boolean isCompatible(Options options) {
    return !options.isCompatibilityMode() || _compatible;
  }

  public IHandler getHandler() {
    return _handler;
  }

  public boolean match(List<Token> tokens, Map<Handler.HandlerType, List<Handler>> definitions) {
    //System.out.println("Handler.match: " + this);
    int tokenIndex = 0;
    for (HandlerPattern pattern : _patterns) {
      boolean optional = pattern.isOptional();
      if (pattern instanceof TagPattern) {
        boolean match = (tokenIndex < tokens.size() && tokens.get(tokenIndex).getTags(((TagPattern) pattern).getTagClass()).size() > 0);
        //System.out.println("Handler.match:   " + ((TagPattern) pattern).getTagClass() + "=" + match);
        if (!match && !optional) {
          return false;
        }
        if (match) {
          tokenIndex++;
        }
        // next if !match && optional ?
      }
      else if (pattern instanceof HandlerTypePattern) {
        if (optional && tokenIndex == tokens.size()) {
          return true;
        }
        List<Handler> subHandlers = definitions.get(((HandlerTypePattern) pattern).getType());
        for (Handler subHandler : subHandlers) {
          if (subHandler.match(CollectionUtils.subList(tokens,tokenIndex, tokens.size()), definitions)) {
            return true;
          }
        }
        return false;
      }
    }
    if (tokenIndex != tokens.size()) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "[Handler: " + _handler + "]";
  }

  public static synchronized Map<Handler.HandlerType, List<Handler>> definitions() {
    if (_definitions == null) {
      Map<Handler.HandlerType, List<Handler>> definitions = new HashMap<Handler.HandlerType, List<Handler>>();

      List<Handler> timeHandlers = new LinkedList<Handler>();
      timeHandlers.add(new Handler(null, new TagPattern(RepeaterTime.class), new TagPattern(RepeaterDayPortion.class, true)));
      definitions.put(Handler.HandlerType.TIME, timeHandlers);

      List<Handler> dateHandlers = new LinkedList<Handler>();
      dateHandlers.add(new Handler(new RdnRmnSdTTzSyHandler(), new TagPattern(RepeaterDayName.class), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarDay.class), new TagPattern(RepeaterTime.class), new TagPattern(TimeZone.class), new TagPattern(ScalarYear.class)));
      // DIFF: We add an optional comma to MDY
      dateHandlers.add(new Handler(new RmnSdSyHandler(), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarDay.class), new TagPattern(SeparatorComma.class, true), new TagPattern(ScalarYear.class)));
      dateHandlers.add(new Handler(new RmnSdSyHandler(), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarDay.class), new TagPattern(ScalarYear.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new RmnSdHandler(), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarDay.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new RmnOdHandler(), new TagPattern(RepeaterMonthName.class), new TagPattern(OrdinalDay.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new RmnSyHandler(), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarYear.class)));
      dateHandlers.add(new Handler(new SdRmnSyHandler(), new TagPattern(ScalarDay.class), new TagPattern(RepeaterMonthName.class), new TagPattern(ScalarYear.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new SmSdSyHandler(), new TagPattern(ScalarMonth.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarDay.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarYear.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new SdSmSyHandler(), new TagPattern(ScalarDay.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarMonth.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarYear.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      dateHandlers.add(new Handler(new SySmSdHandler(), new TagPattern(ScalarYear.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarMonth.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarDay.class), new TagPattern(SeparatorAt.class, true), new HandlerTypePattern(Handler.HandlerType.TIME, true)));
      // DIFF: We make 05/06 interpret as month/day before month/year
      dateHandlers.add(new Handler(new SmSdHandler(), false, new TagPattern(ScalarMonth.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarDay.class)));
      dateHandlers.add(new Handler(new SmSyHandler(), new TagPattern(ScalarMonth.class), new TagPattern(SeparatorSlashOrDash.class), new TagPattern(ScalarYear.class)));
      definitions.put(Handler.HandlerType.DATE, dateHandlers);

      // tonight at 7pm
      List<Handler> anchorHandlers = new LinkedList<Handler>();
      anchorHandlers.add(new Handler(new RHandler(), new TagPattern(Grabber.class, true), new TagPattern(Repeater.class), new TagPattern(SeparatorAt.class, true), new TagPattern(Repeater.class, true), new TagPattern(Repeater.class, true)));
      anchorHandlers.add(new Handler(new RHandler(), new TagPattern(Grabber.class, true), new TagPattern(Repeater.class), new TagPattern(Repeater.class), new TagPattern(SeparatorAt.class, true), new TagPattern(Repeater.class, true), new TagPattern(Repeater.class, true)));
      anchorHandlers.add(new Handler(new RGRHandler(), new TagPattern(Repeater.class), new TagPattern(Grabber.class), new TagPattern(Repeater.class)));
      definitions.put(Handler.HandlerType.ANCHOR, anchorHandlers);

      // 3 weeks from now, in 2 months
      List<Handler> arrowHandlers = new LinkedList<Handler>();
      arrowHandlers.add(new Handler(new SRPHandler(), new TagPattern(Scalar.class), new TagPattern(Repeater.class), new TagPattern(Pointer.class)));
      arrowHandlers.add(new Handler(new PSRHandler(), new TagPattern(Pointer.class), new TagPattern(Scalar.class), new TagPattern(Repeater.class)));
      arrowHandlers.add(new Handler(new SRPAHandler(), new TagPattern(Scalar.class), new TagPattern(Repeater.class), new TagPattern(Pointer.class), new HandlerTypePattern(Handler.HandlerType.ANCHOR)));
      definitions.put(Handler.HandlerType.ARROW, arrowHandlers);

      // 3rd week in march
      List<Handler> narrowHandlers = new LinkedList<Handler>();
      narrowHandlers.add(new Handler(new ORSRHandler(), new TagPattern(Ordinal.class), new TagPattern(Repeater.class), new TagPattern(SeparatorIn.class), new TagPattern(Repeater.class)));
      narrowHandlers.add(new Handler(new ORGRHandler(), new TagPattern(Ordinal.class), new TagPattern(Repeater.class), new TagPattern(Grabber.class), new TagPattern(Repeater.class)));
      definitions.put(Handler.HandlerType.NARROW, narrowHandlers);
      _definitions = definitions;
    }
    return _definitions;
  }

  public static MutableInterval tokensToMutableInterval(List<Token> tokens, Options options) {
    if (options.isDebug()) {
      System.out.println("Chronic.tokensToMutableInterval: " + tokens);
    }

    // maybe it's a specific date
    Map<Handler.HandlerType, List<Handler>> definitions = definitions();
    for (Handler handler : definitions.get(Handler.HandlerType.DATE)) {
      if (handler.isCompatible(options) && handler.match(tokens, definitions)) {
        if (options.isDebug()) {
          System.out.println("Chronic.tokensToMutableInterval: date");
        }
        List<Token> goodTokens = new LinkedList<Token>();
        for (Token token : tokens) {
          if (token.getTag(Separator.class) == null) {
            goodTokens.add(token);
          }
        }
        return handler.getHandler().handle(goodTokens, options);
      }
    }

    // I guess it's not a specific date, maybe it's just an anchor
    for (Handler handler : definitions.get(Handler.HandlerType.ANCHOR)) {
      if (handler.isCompatible(options) && handler.match(tokens, definitions)) {
        if (options.isDebug()) {
          System.out.println("Chronic.tokensToMutableInterval: anchor");
        }
        List<Token> goodTokens = new LinkedList<Token>();
        for (Token token : tokens) {
          if (token.getTag(Separator.class) == null) {
            goodTokens.add(token);
          }
        }
        return handler.getHandler().handle(goodTokens, options);
      }
    }

    // not an anchor, perhaps it's an arrow
    for (Handler handler : definitions.get(Handler.HandlerType.ARROW)) {
      if (handler.isCompatible(options) && handler.match(tokens, definitions)) {
        if (options.isDebug()) {
          System.out.println("Chronic.tokensToMutableInterval: arrow");
        }
        List<Token> goodTokens = new LinkedList<Token>();
        for (Token token : tokens) {
          if (token.getTag(SeparatorAt.class) == null && token.getTag(SeparatorSlashOrDash.class) == null && token.getTag(SeparatorComma.class) == null) {
            goodTokens.add(token);
          }
        }
        return handler.getHandler().handle(goodTokens, options);
      }
    }

    // not an arrow, let's hope it's a narrow
    for (Handler handler : definitions.get(Handler.HandlerType.NARROW)) {
      if (handler.isCompatible(options) && handler.match(tokens, definitions)) {
        if (options.isDebug()) {
          System.out.println("Chronic.tokensToMutableInterval: narrow");
        }
        List<Token> goodTokens = new LinkedList<Token>();
        for (Token token : tokens) {
        if (token.getTag(Separator.class) == null) {
          goodTokens.add(token);
        }
        }
        return handler.getHandler().handle(tokens, options);
      }
    }

    // I guess you're out of luck!
    if (options.isDebug()) {
      System.out.println("Chronic.tokensToMutableInterval: none");
    }
    return null;
  }

  public static List<Repeater<?>> getRepeaters(List<Token> tokens) {
    List<Repeater<?>> repeaters = new LinkedList<Repeater<?>>();
    for (Token token : tokens) {
      Repeater<?> tag = token.getTag(Repeater.class);
      if (tag != null) {
        repeaters.add(tag);
      }
    }
    Collections.sort(repeaters);
    Collections.reverse(repeaters);
    return repeaters;
  }

  public static MutableInterval getAnchor(List<Token> tokens, Options options) {
    Grabber grabber = new Grabber(Grabber.Relative.THIS);
    Pointer.PointerType pointer = Pointer.PointerType.FUTURE;

    List<Repeater<?>> repeaters = getRepeaters(tokens);
    for (int i = 0; i < repeaters.size(); i++) {
      tokens.remove(tokens.size() - 1);
    }

    if (!tokens.isEmpty() && tokens.get(0).getTag(Grabber.class) != null) {
      grabber = tokens.get(0).getTag(Grabber.class);
      tokens.remove(tokens.size() - 1);
    }

    Repeater<?> head = repeaters.remove(0);
    head.setStart( new DateTime(options.getNow().getMillis()));

    MutableInterval outerMutableInterval;
    Grabber.Relative grabberType = grabber.getType();
    if (grabberType == Grabber.Relative.LAST) {
      outerMutableInterval = head.nextMutableInterval(Pointer.PointerType.PAST);
    }
    else if (grabberType == Grabber.Relative.THIS) {
      if (repeaters.size() > 0) {
        outerMutableInterval = head.thisMutableInterval(PointerType.NONE);
      }
      else {
        outerMutableInterval = head.thisMutableInterval(options.getContext());
      }
    }
    else if (grabberType == Grabber.Relative.NEXT) {
      outerMutableInterval = head.nextMutableInterval(Pointer.PointerType.FUTURE);
    }
    else {
      throw new IllegalArgumentException("Invalid grabber type " + grabberType + ".");
    }

    if (options.isDebug()) {
      System.out.println("Chronic.getAnchor: outerMutableInterval = " + outerMutableInterval + "; repeaters = " + repeaters);
    }

    MutableInterval anchor = findWithin(repeaters, outerMutableInterval, pointer, options);
    return anchor;
  }

  public static MutableInterval dayOrTime(DateTime dayStart, List<Token> timeTokens, Options options) {
    MutableInterval outerMutableInterval = new MutableInterval(dayStart, Time.cloneAndAdd(dayStart, Time.DAY_OF_MONTH, 1));
    if (!timeTokens.isEmpty()) {

//      /** SUPER HACK MODE FOR TIMES **/
//      Tag<?> dayPortionTag = null;
//      Tag<?> timeTag = null;
//      for (Token token : timeTokens) {
//        Tag<?> tempDayPortionTag = token.getTag(RepeaterDayPortion.class);
//        if (tempDayPortionTag != null) {
//          dayPortionTag = tempDayPortionTag;
//        }
//
//        Tag<?> tempTimeTag = token.getTag(RepeaterTime.class);
//        if (tempTimeTag != null) {
//          timeTag = tempTimeTag;
//        }
//      }
//      
//      if (timeTag != null && dayPortionTag != null) {
//        Tick tick = (Tick)timeTag.getType();
//        RepeaterDayPortion.DayPortion dayPortion = (RepeaterDayPortion.DayPortion)dayPortionTag.getType();
//        if (tick.intValue() <= (RepeaterDay.DAY_SECONDS / 2)) {
//          if (dayPortion == RepeaterDayPortion.DayPortion.PM) {
//            if (tick.intValue() == (12 * 60 * 60)) {
//              Calendar exactTime = Time.cloneAndAdd(dayStart, Calendar.SECOND, tick.intValue());
//              return new MutableInterval(exactTime, exactTime);
//            }
//            Calendar exactTime = Time.cloneAndAdd(dayStart, Calendar.SECOND, tick.intValue() + RepeaterDay.DAY_SECONDS / 2);
//            return new MutableInterval(exactTime, exactTime);
//          }
//          else if (dayPortion == RepeaterDayPortion.DayPortion.AM) {
//            if (tick.intValue() == (12 * 60 * 60)) {
//              Calendar exactTime = dayStart;
//              return new MutableInterval(exactTime, exactTime);
//            }
//            Calendar exactTime = Time.cloneAndAdd(dayStart, Calendar.SECOND, tick.intValue());
//            return new MutableInterval(exactTime, exactTime);
//          }
//        }
//      }
//      /** SUPER HACK MODE FOR TIMES **/

      options.setNow(outerMutableInterval.getStart());
      MutableInterval time = getAnchor(dealiasAndDisambiguateTimes(timeTokens, options), options);
      return time;
    }
    return outerMutableInterval;
  }

  /**
   * Recursively finds repeaters within other repeaters.
   * Returns a MutableInterval representing the innermost time MutableInterval
   * or nil if no repeater union could be found
   */
  public static MutableInterval findWithin(List<Repeater<?>> tags, MutableInterval mutableInterval, Pointer.PointerType pointer, Options options) {
    if (options.isDebug()) {
      System.out.println("Chronic.findWithin: " + tags + " in " + mutableInterval);
    }
    if (tags.isEmpty()) {
      return mutableInterval;
    }
    Repeater<?> head = tags.get(0);
    List<Repeater<?>> rest = (tags.size() > 1) ? CollectionUtils.subList(tags,1, tags.size()) : new LinkedList<Repeater<?>>();
    head.setStart((pointer == Pointer.PointerType.FUTURE) ? mutableInterval.getStart() : mutableInterval.getEnd());
    MutableInterval h = head.thisMutableInterval(PointerType.NONE);

    if (mutableInterval.contains(h.getStart()) || mutableInterval.contains(h.getEnd())) {
      return findWithin(rest, h, pointer, options);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static List<Token> dealiasAndDisambiguateTimes(List<Token> tokens, Options options) {
    // handle aliases of am/pm
    // 5:00 in the morning => 5:00 am
    // 7:00 in the evening => 7:00 pm

    int dayPortionIndex = -1;
    int tokenSize = tokens.size();
    for (int i = 0; dayPortionIndex == -1 && i < tokenSize; i++) {
      Token t = tokens.get(i);
      if (t.getTag(RepeaterDayPortion.class) != null) {
        dayPortionIndex = i;
      }
    }

    int timeIndex = -1;
    for (int i = 0; timeIndex == -1 && i < tokenSize; i++) {
      Token t = tokens.get(i);
      if (t.getTag(RepeaterTime.class) != null) {
        timeIndex = i;
      }
    }

    if (dayPortionIndex != -1 && timeIndex != -1) {
      Token t1 = tokens.get(dayPortionIndex);
      Tag<RepeaterDayPortion<?>> t1Tag = t1.getTag(RepeaterDayPortion.class);

      Object t1TagType = t1Tag.getType();
      if (RepeaterDayPortion.DayPortion.MORNING.equals(t1TagType)) {
        if (options.isDebug()) {
          System.out.println("Chronic.dealiasAndDisambiguateTimes: morning->am");
        }
        t1.untag(RepeaterDayPortion.class);
        t1.tag(new EnumRepeaterDayPortion(RepeaterDayPortion.DayPortion.AM));
      }
      else if (RepeaterDayPortion.DayPortion.AFTERNOON.equals(t1TagType) || RepeaterDayPortion.DayPortion.EVENING.equals(t1TagType) || RepeaterDayPortion.DayPortion.NIGHT.equals(t1TagType)) {
        if (options.isDebug()) {
          System.out.println("Chronic.dealiasAndDisambiguateTimes: " + t1TagType + "->pm");
        }
        t1.untag(RepeaterDayPortion.class);
        t1.tag(new EnumRepeaterDayPortion(RepeaterDayPortion.DayPortion.PM));
      }
    }

//    int tokenSize = tokens.size();
//    for (int i = 0; i < tokenSize; i++) {
//      Token t0 = tokens.get(i);
//      if (i < tokenSize - 1) {
//        Token t1 = tokens.get(i + 1);
//        RepeaterDayPortion<?> t1Tag = t1.getTag(RepeaterDayPortion.class);
//        if (t1Tag != null && t0.getTag(RepeaterTime.class) != null) {
//          if (t1Tag.getType() == RepeaterDayPortion.DayPortion.MORNING) {
//            t1.untag(RepeaterDayPortion.class);
//            t1.tag(new EnumRepeaterDayPortion(RepeaterDayPortion.DayPortion.AM));
//          }
//          else if (t1Tag.getType() == RepeaterDayPortion.DayPortion.AFTERNOON || t1Tag.getType() == RepeaterDayPortion.DayPortion.EVENING || t1Tag.getType() == RepeaterDayPortion.DayPortion.NIGHT) {
//            t1.untag(RepeaterDayPortion.class);
//            t1.tag(new EnumRepeaterDayPortion(RepeaterDayPortion.DayPortion.PM));
//          }
//        }
//      }
//    }

    // handle ambiguous times if :ambiguous_time_range is specified
    if (options.getAmbiguousTimeRange() != 0) {
      List<Token> ttokens = new LinkedList<Token>();
      for (int i = 0; i < tokenSize; i++) {
        Token t0 = tokens.get(i);
        ttokens.add(t0);
        Token t1 = null;
        if (i < tokenSize - 1) {
          t1 = tokens.get(i + 1);
        }
        if (t0.getTag(RepeaterTime.class) != null && t0.getTag(RepeaterTime.class).getType().isAmbiguous() && (t1 == null || t1.getTag(RepeaterDayPortion.class) == null)) {
          Token distoken = new Token("disambiguator");
          distoken.tag(new IntegerRepeaterDayPortion(Integer.valueOf(options.getAmbiguousTimeRange())));
          ttokens.add(distoken);
        }
      }
      tokens = ttokens;
    }

    if (options.isDebug()) {
      System.out.println("Chronic.dealiasAndDisambiguateTimes: " + tokens);
    }

    return tokens;
  }
}
