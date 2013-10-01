package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;


public class RepeaterYear extends RepeaterUnit {
  private DateTime _currentYearStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentYearStart == null) {
      if (pointer == PointerType.FUTURE) {
        _currentYearStart = Time.cloneAndAdd(Time.y(getNow()), Time.YEAR, 1);
      }
      else if (pointer == PointerType.PAST) {
        _currentYearStart = Time.cloneAndAdd(Time.y(getNow()), Time.YEAR, -1);
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
    }
    else {
      int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
      _currentYearStart = _currentYearStart.plusYears( direction);
    }

    return new MutableInterval(_currentYearStart, _currentYearStart.plusYears(1));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    DateTime yearStart;
    DateTime yearEnd;
    if (pointer == PointerType.FUTURE) {
      yearStart = Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, 1);
      yearEnd = Time.cloneAndAdd(Time.yJan1(getNow()), Time.YEAR, 1);
    }
    else if (pointer == PointerType.PAST) {
      yearStart = Time.yJan1(getNow());
      yearEnd = Time.ymd(getNow());
    }
    else if (pointer == PointerType.NONE) {
      yearStart = Time.yJan1(getNow());
      yearEnd = Time.cloneAndAdd(Time.yJan1(getNow()), Time.YEAR, 1);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return new MutableInterval(yearStart, yearEnd);
  }

  @Override
  public MutableInterval getOffset(MutableInterval span, double amount, Pointer.PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    DateTime newBegin = Time.cloneAndAdd(span.getStart(), Time.YEAR, (int)(amount * direction));
    DateTime newEnd = Time.cloneAndAdd(span.getEnd(), Time.YEAR, (int)(amount * direction));
    return new MutableInterval(newBegin, newEnd);
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return (365 * 24 * 60 * 60);
  }

  @Override
  public String toString() {
    return super.toString() + "-year";
  }
}
