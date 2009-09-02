package org.goda.chronic.repeaters;

import org.goda.chronic.tags.Pointer.PointerType;
import org.goda.chronic.utils.Time;
import org.goda.time.DateTime;
import org.goda.time.MutableInterval;


public class RepeaterFortnight extends RepeaterUnit {
  public static final int FORTNIGHT_SECONDS = 1209600; // (14 * 24 * 60 * 60)

  private DateTime _currentFortnightStart;

  @Override
  protected MutableInterval _nextMutableInterval(PointerType pointer) {
    if (_currentFortnightStart == null) {
      if (pointer == PointerType.FUTURE) {
        RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
        sundayRepeater.setStart(getNow());
        MutableInterval nextSundayMutableInterval = sundayRepeater.nextMutableInterval(PointerType.FUTURE);
        _currentFortnightStart = nextSundayMutableInterval.getStart();
      }
      else if (pointer == PointerType.PAST) {
        RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
        sundayRepeater.setStart(Time.cloneAndAdd(getNow(), Time.SECOND, RepeaterDay.DAY_SECONDS));
        sundayRepeater.nextMutableInterval(PointerType.PAST);
        sundayRepeater.nextMutableInterval(PointerType.PAST);
        MutableInterval lastSundayMutableInterval = sundayRepeater.nextMutableInterval(PointerType.PAST);
        _currentFortnightStart = lastSundayMutableInterval.getStart();
      }
      else {
        throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
      }
    }
    else {
      int direction = (pointer == PointerType.FUTURE) ? 1 : -1;
      _currentFortnightStart = _currentFortnightStart.plusSeconds(direction * RepeaterFortnight.FORTNIGHT_SECONDS);
    }

    return new MutableInterval(_currentFortnightStart, _currentFortnightStart.plusSeconds( RepeaterFortnight.FORTNIGHT_SECONDS));
  }

  @Override
  protected MutableInterval _thisMutableInterval(PointerType pointer) {
    if (pointer == null) {
      pointer = PointerType.FUTURE;
    }

    MutableInterval MutableInterval;
    if (pointer == PointerType.FUTURE) {
      DateTime thisFortnightStart = Time.cloneAndAdd(Time.ymdh(getNow()), Time.SECOND, RepeaterHour.HOUR_SECONDS);
      RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
      sundayRepeater.setStart(getNow());
      sundayRepeater.thisMutableInterval(PointerType.FUTURE);
      MutableInterval thisSundayMutableInterval = sundayRepeater.thisMutableInterval(PointerType.FUTURE);
      DateTime thisFortnightEnd = thisSundayMutableInterval.getStart();
      MutableInterval = new MutableInterval(thisFortnightStart, thisFortnightEnd);
    }
    else if (pointer == PointerType.PAST) {
      DateTime thisFortnightEnd = Time.ymdh(getNow());
      RepeaterDayName sundayRepeater = new RepeaterDayName(RepeaterDayName.DayName.SUNDAY);
      sundayRepeater.setStart(getNow());
      MutableInterval lastSundayMutableInterval = sundayRepeater.nextMutableInterval(PointerType.PAST);
      DateTime thisFortnightStart = lastSundayMutableInterval.getStart();
      MutableInterval = new MutableInterval(thisFortnightStart, thisFortnightEnd);
    }
    else {
      throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
    }

    return MutableInterval;
  }

  @Override
  public MutableInterval getOffset(MutableInterval mutableInterval, int amount, PointerType pointer) {
    int direction = (pointer == PointerType.FUTURE) ? 1 : -1;
    int seconds = direction * amount * RepeaterFortnight.FORTNIGHT_SECONDS;
    return new MutableInterval( mutableInterval.getStart().plusSeconds(seconds), mutableInterval.getEnd().plusSeconds(seconds));
    
  }

  @Override
  public int getWidth() {
    return RepeaterFortnight.FORTNIGHT_SECONDS;
  }

  @Override
  public String toString() {
    return super.toString() + "-fortnight";
  }

}
