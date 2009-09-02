package org.goda.chronic.repeaters;

import java.util.HashMap;
import java.util.Map;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Range;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;


public abstract class RepeaterDayPortion<T> extends Repeater<T> {
  private static final String AM_PATTERN = "^ams?$";
  private static final String PM_PATTERN = "^pms?$";
  private static final String MORNING_PATTERN = "^mornings?$";
  private static final String AFTERNOON_PATTERN = "^afternoons?$";
  private static final String EVENING_PATTERN = "^evenings?$";
  private static final String NIGHT_PATTERN = "^(night|nite)s?$";

  private static final int FULL_DAY_SECONDS = 60 * 60 * 24;

  public static enum DayPortion {
    AM, PM, MORNING, AFTERNOON, EVENING, NIGHT
  }

  private Range _range;
  private MutableInterval _currentMutableInterval;

  public RepeaterDayPortion(T type) {
    super(type);
    _range = createRange(type);
  }

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    DateTime rangeStart;
    DateTime rangeEnd;
    if (_currentMutableInterval == null) {
      long nowSeconds = (getNow().getMillis() - Time.ymd(getNow()).getMillis()) / 1000;
      if (nowSeconds < _range.getBegin()) {
        if (pointer == Pointer.PointerType.FUTURE) {
          rangeStart = Time.cloneAndAdd(Time.ymd(getNow()), Time.SECOND, (int) _range.getBegin());
        }
        else if (pointer == Pointer.PointerType.PAST) {
          rangeStart = Time.cloneAndAdd(Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, -1), Time.SECOND, (int) _range.getBegin());
        }
        else {
          throw new IllegalArgumentException("Unable to handle pointer type " + pointer);
        }
      }
      else if (nowSeconds > _range.getBegin()) {
        if (pointer == Pointer.PointerType.FUTURE) {
          rangeStart = Time.cloneAndAdd(Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, 1), Time.SECOND, (int)_range.getBegin());
        }
        else if (pointer == Pointer.PointerType.PAST) {
          rangeStart = Time.cloneAndAdd(Time.ymd(getNow()), Time.SECOND,(int)_range.getBegin());
        }
        else {
          throw new IllegalArgumentException("Unable to handle pointer type " + pointer);
        }
      }
      else {
        if (pointer == Pointer.PointerType.FUTURE) {
          rangeStart = Time.cloneAndAdd(Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, 1), Time.SECOND, (int)_range.getBegin());
        }
        else if (pointer == Pointer.PointerType.PAST) {
          rangeStart = Time.cloneAndAdd(Time.cloneAndAdd(Time.ymd(getNow()), Time.DAY_OF_MONTH, -1), Time.SECOND,(int) _range.getBegin());
        }
        else {
          throw new IllegalArgumentException("Unable to handle pointer type " + pointer);
        }
      }

      _currentMutableInterval = new MutableInterval(rangeStart, Time.cloneAndAdd(rangeStart, Time.SECOND,(int) _range.getWidth()));
    }
    else {
      if (pointer == Pointer.PointerType.FUTURE) {
        // WARN: Does not use DateTime
        _currentMutableInterval = new MutableInterval( _currentMutableInterval.getStart().plusSeconds(FULL_DAY_SECONDS),
                _currentMutableInterval.getEnd().plusSeconds(FULL_DAY_SECONDS));
      }
      else if (pointer == Pointer.PointerType.PAST) {
        // WARN: Does not use DateTime
        _currentMutableInterval = new MutableInterval( _currentMutableInterval.getStart().minusSeconds(FULL_DAY_SECONDS),
                _currentMutableInterval.getEnd().minusSeconds(FULL_DAY_SECONDS));
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer type " + pointer);
      }
    }
    return _currentMutableInterval;
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    DateTime rangeStart = Time.cloneAndAdd(Time.ymd(getNow()), Time.SECOND, (int) _range.getBegin());
    _currentMutableInterval = new MutableInterval(rangeStart, Time.cloneAndAdd(rangeStart, Time.SECOND,(int) _range.getWidth()));
    return _currentMutableInterval;
  }

  @Override
  public MutableInterval getOffset(MutableInterval mutableInterval, int amount, PointerType pointer) {
    setStart(mutableInterval.getStart());
    MutableInterval portionMutableInterval = nextMutableInterval(pointer);
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    int seconds = direction * (amount - 1) * RepeaterDay.DAY_SECONDS;
    portionMutableInterval = new MutableInterval( portionMutableInterval.getStart().plusSeconds(seconds), portionMutableInterval.getEnd().plusSeconds(seconds));
    return portionMutableInterval;
  }

  @Override
  public int getWidth() {
    if (_range == null) {
      throw new IllegalStateException("Range has not been set");
    }
    int width;
    if (_currentMutableInterval != null) {
      width = (int) ( _currentMutableInterval.getEndMillis() - _currentMutableInterval.getStartMillis())/1000 ;
    }
    else {
      width = _getWidth(_range);
    }
    return width;
  }
  
  protected abstract int _getWidth(Range range);
  
  protected abstract Range createRange(T type);

  @Override
  public String toString() {
    return super.toString() + "-dayportion-" + getType();
  }

  public static EnumRepeaterDayPortion scan(Token token) {
    Map<String, RepeaterDayPortion.DayPortion> scanner = new HashMap<String, RepeaterDayPortion.DayPortion>();
    scanner.put(RepeaterDayPortion.AM_PATTERN, RepeaterDayPortion.DayPortion.AM);
    scanner.put(RepeaterDayPortion.PM_PATTERN, RepeaterDayPortion.DayPortion.PM);
    scanner.put(RepeaterDayPortion.MORNING_PATTERN, RepeaterDayPortion.DayPortion.MORNING);
    scanner.put(RepeaterDayPortion.AFTERNOON_PATTERN, RepeaterDayPortion.DayPortion.AFTERNOON);
    scanner.put(RepeaterDayPortion.EVENING_PATTERN, RepeaterDayPortion.DayPortion.EVENING);
    scanner.put(RepeaterDayPortion.NIGHT_PATTERN, RepeaterDayPortion.DayPortion.NIGHT);
    for (String scannerItem : scanner.keySet()) {
      if (token.getWord().matches(scannerItem)) {
        return new EnumRepeaterDayPortion(scanner.get(scannerItem));
      }
    }
    return null;
  }

}
