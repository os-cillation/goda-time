package org.goda.chronic.repeaters;

import java.util.LinkedList;
import java.util.List;
import org.goda.chronic.Options;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.StringUtils;
import org.goda.chronic.utils.Tick;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;



public class RepeaterTime extends Repeater<Tick> {
  private static final String TIME_PATTERN = "^\\d{1,2}(:?\\d{2})?([\\.:]?\\d{2})?$";

  public RepeaterTime(String time) {
    super(null);
    String t = time.replaceAll(":", "");
    Tick type;
    int length = t.length();
    if (length <= 2) {
      double hours = Double.parseDouble(t);
      int hoursInSeconds = (int) Math.round(hours * 60D * 60D);
      if (hours == 12) {
        type = new Tick(0 * 60 * 60, true);
      }
      else {
        type = new Tick(hoursInSeconds, true);
      }
    }
    else if (length == 3) {
      int hoursInSeconds = (int) (Double.parseDouble(t.substring(0, 1)) * 60 * 60);
      int minutesInSeconds = (int) (Double.parseDouble(t.substring(1)) * 60D);
      type = new Tick(hoursInSeconds + minutesInSeconds, true);
    }
    else if (length == 4) {
      boolean ambiguous = (time.contains(":") && Integer.parseInt(t.substring(0, 1)) != 0 && Integer.parseInt(t.substring(0, 2)) <= 12);
      double hours = Double.parseDouble(t.substring(0, 2));
      int hoursInSeconds = (int)(hours * 60 * 60);
      int minutesInSeconds = (int)(Double.parseDouble(t.substring(2)) * 60);
      if (hours == 12) {
        type = new Tick(0 * 60 * 60 + minutesInSeconds, ambiguous);
      }
      else {
        type = new Tick(hoursInSeconds + minutesInSeconds, ambiguous);
      }
    }
    else if (length == 5) {
      int hoursInSeconds = (int)(Double.parseDouble(t.substring(0, 1)) * 60 * 60);
      int minutesInSeconds =(int)(Double.parseDouble(t.substring(1, 3)) * 60);
      int seconds = (int) Double.parseDouble(t.substring(3));
      type = new Tick(hoursInSeconds + minutesInSeconds + seconds, true);
    }
    else if (length == 6) {
      boolean ambiguous = (time.contains(":") && Integer.parseInt(t.substring(0, 1)) != 0 && Integer.parseInt(t.substring(0, 2)) <= 12);
      int hours = Integer.parseInt(t.substring(0, 2));
      int hoursInSeconds = hours * 60 * 60;
      int minutesInSeconds = Integer.parseInt(t.substring(2, 4)) * 60;
      int seconds = Integer.parseInt(t.substring(4, 6));
      //type = new Tick(hoursInSeconds + minutesInSeconds + seconds, ambiguous);
      if (hours == 12) {
        type = new Tick(0 * 60 * 60 + minutesInSeconds + seconds, ambiguous);
      }
      else {
        type = new Tick(hoursInSeconds + minutesInSeconds + seconds, ambiguous);
      }
    }
    else {
      throw new IllegalArgumentException("Time cannot exceed six digits");
    }
    setType(type);
  }

  private DateTime _currentTime;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    int halfDay = RepeaterDay.DAY_SECONDS / 2;
    int fullDay = RepeaterDay.DAY_SECONDS;

    DateTime now = getNow();
    Tick tick = getType();
    boolean first = false;
    if (_currentTime == null) {
      first = true;
      DateTime midnight = Time.ymd(now);
      DateTime yesterdayMidnight = midnight.minusSeconds(fullDay);
      DateTime tomorrowMidnight = midnight.plusSeconds(fullDay);
      boolean done = false;
      if (pointer == Pointer.PointerType.FUTURE) {
        if (tick.isAmbiguous()) {
          List<DateTime> futureDates = new LinkedList<DateTime>();
          futureDates.add( midnight.plusSeconds(tick.intValue()));
          futureDates.add(midnight.plusSeconds( halfDay + tick.intValue()));
          futureDates.add(tomorrowMidnight.plusSeconds(tick.intValue()));
          for (DateTime futureDate : futureDates) {
            if (futureDate.getMillis() > now.getMillis() || futureDate.equals(now)) {
              _currentTime = futureDate;
              done = true;
              break;
            }
          }
        }
        else {
          List<DateTime> futureDates = new LinkedList<DateTime>();
          futureDates.add(midnight.plusSeconds(tick.intValue()));
          futureDates.add(tomorrowMidnight.plusSeconds(tick.intValue()));
          for (DateTime futureDate : futureDates) {
            if (futureDate.getMillis() > now.getMillis() || futureDate.equals(now)) {
              _currentTime = futureDate;
              done = true;
              break;
            }
          }
        }
      }
      else {
        if (tick.isAmbiguous()) {
          List<DateTime> pastDates = new LinkedList<DateTime>();
          pastDates.add(midnight.plusSeconds(halfDay + tick.intValue()));
          pastDates.add(midnight.plusSeconds(tick.intValue()));
          pastDates.add(yesterdayMidnight.plusSeconds(tick.intValue() * 2));
          for (DateTime pastDate : pastDates) {
            if (pastDate.getMillis() > now.getMillis() || pastDate.equals(now)) {
              _currentTime = pastDate;
              done = true;
              break;
            }
          }
        }
        else {
          List<DateTime> pastDates = new LinkedList<DateTime>();
          pastDates.add(midnight.plusSeconds( tick.intValue()));
          pastDates.add(yesterdayMidnight.plusSeconds( tick.intValue()));
          for (DateTime pastDate : pastDates) {
            if (pastDate.getMillis() > now.getMillis() || pastDate.equals(now)) {
              _currentTime = pastDate;
              done = true;
              break;
            }
          }
        }
      }

      if (!done && _currentTime == null) {
        throw new IllegalStateException("Current time cannot be null at this point.");
      }
    }

    if (!first) {
      int increment = (tick.isAmbiguous()) ? halfDay : fullDay;
      int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
      _currentTime = _currentTime.plusSeconds( direction * increment);
    }

    return new MutableInterval(_currentTime, _currentTime.plusSeconds(getWidth()));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    if (pointer == Pointer.PointerType.NONE) {
      pointer = Pointer.PointerType.FUTURE;
    }
    return nextMutableInterval(pointer);
  }

  @Override
  public MutableInterval getOffset(MutableInterval MutableInterval, double amount, PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  public int getWidth() {
    return 1;
  }

  @Override
  public String toString() {
    return super.toString() + "-time-" + getType();
  }

  public static RepeaterTime scan(Token token, List<Token> tokens, Options options) {
    if (token.getWord().matches( RepeaterTime.TIME_PATTERN)) {
      return new RepeaterTime(token.getWord());
    }
    Integer intStrValue = StringUtils.integerValue(token.getWord());
    if (intStrValue != null) {
      return new RepeaterTime(intStrValue.toString());
    }
    return null;
  }
}
