package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;



public class RepeaterHour extends RepeaterUnit {
  public static final int HOUR_SECONDS = 3600; // (60 * 60);

  private DateTime _currentDayStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentDayStart == null) {
      if (pointer == PointerType.FUTURE) {
        _currentDayStart = Time.cloneAndAdd(Time.ymdh(getNow()), Time.HOUR, 1);
      }
      else if (pointer == PointerType.PAST) {
        _currentDayStart = Time.cloneAndAdd(Time.ymdh(getNow()), Time.HOUR, -1);
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
    }
    else {
      int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
       _currentDayStart = _currentDayStart.plusHours( direction);
    }
    return new MutableInterval(_currentDayStart, _currentDayStart.plusHours( 1));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    DateTime hourStart;
    DateTime hourEnd;
    if (pointer == PointerType.FUTURE) {
      hourStart = Time.cloneAndAdd(Time.ymdhm(getNow()), Time.MINUTE, 1);
      hourEnd = Time.cloneAndAdd(Time.ymdh(getNow()), Time.HOUR, 1);
    }
    else if (pointer == PointerType.PAST) {
      hourStart = Time.ymdh(getNow());
      hourEnd = Time.ymdhm(getNow());
    }
    else if (pointer == PointerType.NONE) {
      hourStart = Time.ymdh(getNow());
      hourEnd = Time.cloneAndAdd(hourStart, Time.HOUR, 1);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return new MutableInterval(hourStart, hourEnd);
  }

  @Override
  public MutableInterval getOffset(MutableInterval span, double amount, Pointer.PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    // WARN: Does not use Calendar
    int seconds = (int) (direction * amount * RepeaterHour.HOUR_SECONDS);
    return new MutableInterval( span.getStart().plusSeconds(seconds), span.getEnd().plusSeconds(seconds));
    
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterHour.HOUR_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-hour";
  }
}
