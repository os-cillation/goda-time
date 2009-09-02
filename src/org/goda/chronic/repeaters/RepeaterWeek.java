package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;



public class RepeaterWeek extends RepeaterUnit {
  public static final int WEEK_SECONDS = 604800; // (7 * 24 * 60 * 60);
  public static final int WEEK_DAYS = 7;

  private DateTime _currentWeekStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentWeekStart == null) {
      if (pointer == PointerType.FUTURE) {
        RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
        sundayRepeater.setStart( new DateTime(getNow()));
        MutableInterval nextSundayMutableInterval = sundayRepeater.nextMutableInterval(Pointer.PointerType.FUTURE);
        _currentWeekStart = nextSundayMutableInterval.getStart();
      }
      else if (pointer == PointerType.PAST) {
        RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
        sundayRepeater.setStart(Time.cloneAndAdd(getNow(), Time.DAY_OF_MONTH, 1));
        sundayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
        MutableInterval lastSundayMutableInterval = sundayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
        _currentWeekStart = lastSundayMutableInterval.getStart();
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
    }
    else {
      int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
      int days =RepeaterWeek.WEEK_DAYS * direction;
      _currentWeekStart = _currentWeekStart.plusDays(days);
    }

    return new MutableInterval(_currentWeekStart, _currentWeekStart.plusDays(RepeaterWeek.WEEK_DAYS));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    MutableInterval thisWeekMutableInterval;
    DateTime thisWeekStart;
    DateTime thisWeekEnd;
    if (pointer == PointerType.FUTURE) {
      thisWeekStart = Time.cloneAndAdd(Time.ymdh(getNow()), Time.HOUR, 1);
      RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
      sundayRepeater.setStart( new DateTime( getNow()));
      MutableInterval thisSundayMutableInterval = sundayRepeater.thisMutableInterval(Pointer.PointerType.FUTURE);
      thisWeekEnd = thisSundayMutableInterval.getStart();
      thisWeekMutableInterval = new MutableInterval(thisWeekStart, thisWeekEnd);
    }
    else if (pointer == PointerType.PAST) {
      thisWeekEnd = Time.ymdh(getNow());
      RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
      sundayRepeater.setStart( new DateTime(getNow()));
      MutableInterval lastSundayMutableInterval = sundayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
      thisWeekStart = lastSundayMutableInterval.getStart();
      thisWeekMutableInterval = new MutableInterval(thisWeekStart, thisWeekEnd);
    }
    else if (pointer == PointerType.NONE) {
      RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
      sundayRepeater.setStart(new DateTime(getNow()));
      MutableInterval lastSundayMutableInterval = sundayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
      thisWeekStart = lastSundayMutableInterval.getStart();
      thisWeekEnd = Time.cloneAndAdd(thisWeekStart, Time.DAY_OF_MONTH, RepeaterWeek.WEEK_DAYS);
      thisWeekMutableInterval = new MutableInterval(thisWeekStart, thisWeekEnd);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return thisWeekMutableInterval;
  }

  @Override
  public MutableInterval getOffset(MutableInterval span, int amount, Pointer.PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    // WARN: Does not use Calendar
    int seconds = direction * amount * RepeaterWeek.WEEK_SECONDS;
    return new MutableInterval( span.getStart().plusSeconds(seconds), span.getEnd().plusSeconds(seconds));
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterWeek.WEEK_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-week";
  }
}
