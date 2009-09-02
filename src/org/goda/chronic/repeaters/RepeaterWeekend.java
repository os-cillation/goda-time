package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;


public class RepeaterWeekend extends RepeaterUnit {
  public static final int WEEKEND_SECONDS = 172800; // (2 * 24 * 60 * 60);

  private DateTime _currentWeekStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentWeekStart == null) {
      if (pointer == Pointer.PointerType.FUTURE) {
        RepeaterDayName saturdayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SATURDAY);
        saturdayRepeater.setStart( new DateTime(getNow()));
        MutableInterval nextSaturdayMutableInterval = saturdayRepeater.nextMutableInterval(Pointer.PointerType.FUTURE);
        _currentWeekStart = nextSaturdayMutableInterval.getStart();
      }
      else if (pointer == Pointer.PointerType.PAST) {
        RepeaterDayName saturdayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SATURDAY);
        saturdayRepeater.setStart(Time.cloneAndAdd(getNow(), Time.SECOND, RepeaterDay.DAY_SECONDS));
        MutableInterval lastSaturdayMutableInterval = saturdayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
        _currentWeekStart = lastSaturdayMutableInterval.getStart();
      }
    }
    else {
      int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
      _currentWeekStart = Time.cloneAndAdd(_currentWeekStart, Time.SECOND, direction * RepeaterWeek.WEEK_SECONDS);
    }
    return new MutableInterval(_currentWeekStart, Time.cloneAndAdd(_currentWeekStart, Time.SECOND, RepeaterWeekend.WEEKEND_SECONDS));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    MutableInterval thisMutableInterval;
    if (pointer == Pointer.PointerType.FUTURE || pointer == Pointer.PointerType.NONE) {
      RepeaterDayName saturdayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SATURDAY);
      saturdayRepeater.setStart(new DateTime(getNow()));
      MutableInterval thisSaturdayMutableInterval = saturdayRepeater.nextMutableInterval(Pointer.PointerType.FUTURE);
      thisMutableInterval = new MutableInterval(thisSaturdayMutableInterval.getStart(), Time.cloneAndAdd(thisSaturdayMutableInterval.getStart(), Time.SECOND, RepeaterWeekend.WEEKEND_SECONDS));
    }
    else if (pointer == Pointer.PointerType.PAST) {
      RepeaterDayName saturdayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SATURDAY);
      saturdayRepeater.setStart( new DateTime( getNow()));
      MutableInterval lastSaturdayMutableInterval = saturdayRepeater.nextMutableInterval(Pointer.PointerType.PAST);
      thisMutableInterval = new MutableInterval(lastSaturdayMutableInterval.getStart(), Time.cloneAndAdd(lastSaturdayMutableInterval.getStart(), Time.SECOND, RepeaterWeekend.WEEKEND_SECONDS));
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return thisMutableInterval;
  }

  @Override
  public MutableInterval getOffset(MutableInterval span, int amount, PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    RepeaterWeekend weekend = new RepeaterWeekend();
    weekend.setStart(span.getStart());
    DateTime start = Time.cloneAndAdd(weekend.nextMutableInterval(pointer).getStart(), Time.SECOND, (amount - 1) * direction * RepeaterWeek.WEEK_SECONDS);
    return new MutableInterval(start, Time.cloneAndAdd(start, Time.SECOND, Time.getWidth(span)));
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterWeekend.WEEKEND_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-weekend";
  }
}
