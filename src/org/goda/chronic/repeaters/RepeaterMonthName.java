package org.goda.chronic.repeaters;

import java.util.HashMap;
import java.util.Map;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;

public class RepeaterMonthName extends Repeater<RepeaterMonthName.MonthName> {
  private static final String JAN_PATTERN = "^jan\\.?(uary)?$";
  private static final String FEB_PATTERN = "^feb\\.?(ruary)?$";
  private static final String MAR_PATTERN = "^mar\\.?(ch)?$";
  private static final String APR_PATTERN = "^apr\\.?(il)?$";
  private static final String MAY_PATTERN = "^may$";
  private static final String JUN_PATTERN = "^jun\\.?e?$";
  private static final String JUL_PATTERN = "^jul\\.?y?$";
  private static final String AUG_PATTERN = "^aug\\.?(ust)?$";
  private static final String SEP_PATTERN = "^sep\\.?(t\\.?|tember)?$";
  private static final String OCT_PATTERN = "^oct\\.?(ober)?$";
  private static final String NOV_PATTERN = "^nov\\.?(ember)?$";
  private static final String DEC_PATTERN = "^dec\\.?(ember)?$";

  private static final int MONTH_SECONDS = 2592000; // 30 * 24 * 60 * 60

  public static enum MonthName {
    _ZERO_MONTH, JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
  }

  private DateTime _currentMonthBegin;

  public RepeaterMonthName(MonthName type) {
    super(type);
  }
  
  public int getIndex() {
    return getType().ordinal();
  }

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentMonthBegin == null) {
      int targetMonth = getType().ordinal();
      System.out.println("Target month: "+targetMonth);
      int nowMonth = getNow().getMonthOfYear();
      if (pointer == PointerType.FUTURE) {
        if (nowMonth < targetMonth) {
          _currentMonthBegin = Time.y(getNow(), targetMonth);
        }
        else if (nowMonth > targetMonth) {
          _currentMonthBegin = Time.cloneAndAdd(Time.y(getNow(), targetMonth), Time.YEAR, 1);
        }
      }
      else if (pointer == PointerType.NONE) {
        if (nowMonth <= targetMonth) {
          _currentMonthBegin = Time.y(getNow(), targetMonth);
        }
        else if (nowMonth > targetMonth) {
          _currentMonthBegin = Time.cloneAndAdd(Time.y(getNow(), targetMonth), Time.YEAR, 1);
        }
      }
      else if (pointer == PointerType.PAST) {
        if (nowMonth > targetMonth) {
          _currentMonthBegin = Time.y(getNow(), targetMonth);
        }
        else if (nowMonth < targetMonth) {
          _currentMonthBegin = Time.cloneAndAdd(Time.y(getNow(), targetMonth), Time.YEAR, -1);
        }
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
      if (_currentMonthBegin == null) {
        throw new IllegalStateException("Current month should be set by now.");
      }
    }
    else {
      if (pointer == PointerType.FUTURE) {
        _currentMonthBegin = Time.cloneAndAdd(_currentMonthBegin, Time.YEAR, 1);
      }
      else if (pointer == PointerType.PAST) {
        _currentMonthBegin = Time.cloneAndAdd(_currentMonthBegin, Time.YEAR, -1);
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
    }
    System.out.println("after that big condit "+ _currentMonthBegin );
    return new MutableInterval(_currentMonthBegin, _currentMonthBegin.plusMonths(1));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    MutableInterval MutableInterval;
    if (pointer == Pointer.PointerType.PAST) {
      MutableInterval = nextMutableInterval(pointer);
    }
    else if (pointer == Pointer.PointerType.FUTURE || pointer == Pointer.PointerType.NONE) {
      MutableInterval = nextMutableInterval(Pointer.PointerType.NONE);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }
    return MutableInterval;
  }

  @Override
  public MutableInterval getOffset(MutableInterval MutableInterval, int amount, PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterMonthName.MONTH_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-monthname-" + getType();
  }

  public static RepeaterMonthName scan(Token token) {
    Map<String, RepeaterMonthName.MonthName> scanner = new HashMap<String, RepeaterMonthName.MonthName>();
    scanner.put(RepeaterMonthName.JAN_PATTERN, RepeaterMonthName.MonthName.JANUARY);
    scanner.put(RepeaterMonthName.FEB_PATTERN, RepeaterMonthName.MonthName.FEBRUARY);
    scanner.put(RepeaterMonthName.MAR_PATTERN, RepeaterMonthName.MonthName.MARCH);
    scanner.put(RepeaterMonthName.APR_PATTERN, RepeaterMonthName.MonthName.APRIL);
    scanner.put(RepeaterMonthName.MAY_PATTERN, RepeaterMonthName.MonthName.MAY);
    scanner.put(RepeaterMonthName.JUN_PATTERN, RepeaterMonthName.MonthName.JUNE);
    scanner.put(RepeaterMonthName.JUL_PATTERN, RepeaterMonthName.MonthName.JULY);
    scanner.put(RepeaterMonthName.AUG_PATTERN, RepeaterMonthName.MonthName.AUGUST);
    scanner.put(RepeaterMonthName.SEP_PATTERN, RepeaterMonthName.MonthName.SEPTEMBER);
    scanner.put(RepeaterMonthName.OCT_PATTERN, RepeaterMonthName.MonthName.OCTOBER);
    scanner.put(RepeaterMonthName.NOV_PATTERN, RepeaterMonthName.MonthName.NOVEMBER);
    scanner.put(RepeaterMonthName.DEC_PATTERN, RepeaterMonthName.MonthName.DECEMBER);
    for (String scannerItem : scanner.keySet()) {
      if (token.getWord().matches(scannerItem)) {
        return new RepeaterMonthName(scanner.get(scannerItem));
      }
    }
    return null;
  }

}
