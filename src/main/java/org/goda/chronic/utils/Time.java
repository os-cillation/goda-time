package org.goda.chronic.utils;

import org.goda.time.DateTime;
import org.goda.time.DateTimeFieldType;
import org.goda.time.MutableInterval;


public class Time {

  public static final int SECOND = 0;
  public static final int DAY_OF_MONTH = 1;
  public static final int MONTH = 2;
  public static final int YEAR = 3;
  public static final int HOUR = 4;
  public static final int MINUTE = 5;

  public static DateTime construct(int year, int month) {
    if (year <= 1900) {
      throw new IllegalArgumentException("Illegal year '" + year + "'");
    }
    return new DateTime(year, month, 1,0,0,0,0);
  }

  public static DateTime construct(int year, int month, int day) {
    return new DateTime(year, month, day,0,0,0,0);
  }

  public static DateTime construct(int year, int month, int day, int hour) {
    return new DateTime(year, month, day, hour,0,0,0);
  }

  public static DateTime construct(int year, int month, int day, int hour, int minute) {
    return new DateTime(year, month, day,hour,minute,0,0);
  }

  public static DateTime construct(int year, int month, int day, int hour, int minute, int second) {
     return new DateTime(year, month, day,hour,minute,second,0);
  }

  public static DateTime construct(int year, int month, int day, int hour, int minute, int second, int millisecond) {
    return new DateTime(year, month, day,hour,minute,second,millisecond);
  }

  public static DateTime y(DateTime basis) {
    return construct(basis.get(DateTimeFieldType.year()), 1);
  }

  public static DateTime yJan1(DateTime basis) {
    DateTime clone = Time.y(basis, 1, 1);
    return clone;
  }

  public static DateTime y(DateTime basis, int month) {
    return construct(basis.get(DateTimeFieldType.year()), month);
  }

  public static DateTime y(DateTime basis, int month, int day) {
    return construct(basis.get(DateTimeFieldType.year()), month, day);
  }

  public static DateTime ym(DateTime basis) {
    return construct(basis.get(DateTimeFieldType.year()), basis.get(DateTimeFieldType.monthOfYear()));
  }

  public static DateTime ymd(DateTime basis) {
    return construct(basis.get(DateTimeFieldType.year()), basis.get(DateTimeFieldType.monthOfYear()), basis.get(DateTimeFieldType.dayOfMonth()));
  }

  public static DateTime ymdh(DateTime basis) {
    return construct(basis.get(DateTimeFieldType.year()), basis.get(DateTimeFieldType.monthOfYear()), basis.get(DateTimeFieldType.dayOfMonth()), basis.get(DateTimeFieldType.hourOfDay()));
  }

  public static DateTime ymdhm(DateTime basis) {
    return construct(basis.get(DateTimeFieldType.year()), basis.get(DateTimeFieldType.monthOfYear()), basis.get(DateTimeFieldType.dayOfMonth()), basis.get(DateTimeFieldType.hourOfDay()), basis.get(DateTimeFieldType.minuteOfHour()));
  }

  public static DateTime cloneAndAdd(DateTime basis, int field, int duration ){
      switch(field){
          case Time.SECOND:
              return basis.plusSeconds(duration);
          case Time.DAY_OF_MONTH:
              return basis.plusDays(duration);
          case Time.MONTH:
              return basis.plusMonths(duration);
          case Time.YEAR:
              return basis.plusYears(duration);
          case Time.HOUR:
              return basis.plusHours(duration);
          case Time.MINUTE:
              return basis.plusMinutes(duration);
          default:
              throw new RuntimeException("Unknown field");
      }
  }

  public static int getWidth(MutableInterval interval){
      return (int) ( interval.getEnd().getMillis() - interval.getStart().getMillis() ) /1000;
  }
  
}
