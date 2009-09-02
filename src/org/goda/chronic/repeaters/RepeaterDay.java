package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;



public class RepeaterDay extends RepeaterUnit {
  public static final int DAY_SECONDS = 86400; // (24 * 60 * 60);

  private DateTime _currentDayStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentDayStart == null) {
      _currentDayStart = Time.ymd(getNow());
    }

    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
     _currentDayStart = _currentDayStart.plusDays(direction);

    return new MutableInterval(_currentDayStart, _currentDayStart.plusDays(1));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    DateTime dayBegin;
    DateTime dayEnd;
    if (pointer == PointerType.FUTURE) {
      dayBegin = Time.cloneAndAdd(Time.ymdh(getNow()), Time.HOUR, 1);
      dayEnd = Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, 1);
    }
    else if (pointer == PointerType.PAST) {
      dayBegin = Time.ymd(getNow());
      dayEnd = Time.ymdh(getNow());
    }
    else if (pointer == PointerType.NONE) {
      dayBegin = Time.ymd(getNow());
      dayEnd = Time.cloneAndAdd(Time.ymdh(getNow()), Time.SECOND, RepeaterDay.DAY_SECONDS);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return new MutableInterval(dayBegin, dayEnd);
  }

  @Override
  public MutableInterval getOffset(MutableInterval mutableInterval, int amount, Pointer.PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    // WARN: Does not use Calendar
    int seconds = direction * amount * RepeaterDay.DAY_SECONDS;
    return new MutableInterval( mutableInterval.getStart().plusSeconds(seconds), mutableInterval.getEnd().plusSeconds(seconds));
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterDay.DAY_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-day";
  }
}
