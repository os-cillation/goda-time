package org.goda.chronic.repeaters;

import java.util.HashMap;
import java.util.Map;
import org.goda.chronic.tags.Pointer;
import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.chronic.utils.Token;
import org.goda.time.DateTime;
import org.goda.time.DateTimeFieldType;
import org.goda.time.MutableInterval;


public class RepeaterDayName extends Repeater<RepeaterDayName.DayName> {
  private static final String MON_PATTERN = "^m[ou]n(day)?$";
  private static final String TUE_PATTERN = "^t(ue|eu|oo|u|)s(day)?$";
  private static final String TUE_PATTERN_1 = "^tue$";
  private static final String WED_PATTERN_1 = "^we(dnes|nds|nns)day$";
  private static final String WED_PATTERN_2 = "^wed$";
  private static final String THU_PATTERN_1 = "^th(urs|ers)day$";
  private static final String THU_PATTERN_2 = "^thu$";
  private static final String FRI_PATTERN = "^fr[iy](day)?$";
  private static final String SAT_PATTERN = "^sat(t?[ue]rday)?$";
  private static final String SUN_PATTERN = "^su[nm](day)?$";

  public static final int DAY_SECONDS = 86400; // (24 * 60 * 60);

  public static enum DayName {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
  }

  private DateTime _currentDayStart;

  public RepeaterDayName(RepeaterDayName.DayName type) {
    super(type);
  }

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    int direction = (pointer == Pointer.PointerType.FUTURE) ? 1 : -1;
    if (_currentDayStart == null) {
      _currentDayStart = Time.ymd(getNow());
      _currentDayStart = _currentDayStart.plusDays(direction);
      System.out.println( "Looking for "+getType().ordinal() );
      int dayNum = getType().ordinal();

      while ((_currentDayStart.get(DateTimeFieldType.dayOfWeek())) != dayNum) {
        _currentDayStart =_currentDayStart.plusDays(direction);
      }
    }
    else {
      _currentDayStart = _currentDayStart.plusDays( direction * 7);
    }
    return new MutableInterval(_currentDayStart, _currentDayStart.plusMonths(1));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    if (pointer == PointerType.NONE) {
      pointer = PointerType.FUTURE;
    }
    return super.nextMutableInterval(pointer);
  }

  @Override
  public MutableInterval getOffset(MutableInterval MutableInterval, double amount, PointerType pointer) {
    throw new IllegalStateException("Not implemented.");
  }

  @Override
  public int getWidth() {
    // WARN: Does not use Calendar
    return RepeaterDayName.DAY_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-dayname-" + getType();
  }

  public static RepeaterDayName scan(Token token) {
    Map<String, RepeaterDayName.DayName> scanner = new HashMap<String, RepeaterDayName.DayName>();
    scanner.put(RepeaterDayName.MON_PATTERN, RepeaterDayName.DayName.MONDAY);
    scanner.put(RepeaterDayName.TUE_PATTERN, RepeaterDayName.DayName.TUESDAY);
    scanner.put(RepeaterDayName.TUE_PATTERN_1, RepeaterDayName.DayName.TUESDAY);
    scanner.put(RepeaterDayName.WED_PATTERN_1, RepeaterDayName.DayName.WEDNESDAY);
    scanner.put(RepeaterDayName.WED_PATTERN_2, RepeaterDayName.DayName.WEDNESDAY);
    scanner.put(RepeaterDayName.THU_PATTERN_1, RepeaterDayName.DayName.THURSDAY);
    scanner.put(RepeaterDayName.THU_PATTERN_2, RepeaterDayName.DayName.THURSDAY);
    scanner.put(RepeaterDayName.FRI_PATTERN, RepeaterDayName.DayName.FRIDAY);
    scanner.put(RepeaterDayName.SAT_PATTERN, RepeaterDayName.DayName.SATURDAY);
    scanner.put(RepeaterDayName.SUN_PATTERN, RepeaterDayName.DayName.SUNDAY);
    for (String scannerItem : scanner.keySet()) {
      if (token.getWord().matches(scannerItem)) {
        return new RepeaterDayName(scanner.get(scannerItem));
      }
    }
    return null;
  }

}
