package org.goda.chronic.repeaters;

import org.goda.chronic.utils.Token;

import java.util.HashMap;
import java.util.Map;


public abstract class RepeaterUnit extends Repeater<Object> {
    private static final String YEAR_PATTERN = "^years?$";
    private static final String SEASON_PATTERN = "^seasons?$";
    private static final String MONTH_PATTERN = "^months?$";
    private static final String FORTNIGHT_PATTERN = "^fortnights?$";
    private static final String WEEK_PATTERN = "^weeks?$";
    private static final String WEEKEND_PATTERN = "^weekends?$";
    private static final String DAY_PATTERN = "^days?$";
    private static final String HOUR_PATTERN = "^hours?$";
    private static final String MINUTE_PATTERN = "^minutes?$";
    private static final String SECOND_PATTERN = "^seconds?$";
    private static final String HOUR_ABBRV_PATTERN = "^hr.?$";
    private static final String MINUTE_ABBRV_PATTERN = "^min.?$";
    
    
    public RepeaterUnit() {
        super(null);
    }

    public static RepeaterUnit scan(Token token) {
        try {
            Map<String, RepeaterUnit.UnitName> scanner = new HashMap<String, RepeaterUnit.UnitName>();
            scanner.put(RepeaterUnit.YEAR_PATTERN, RepeaterUnit.UnitName.YEAR);
            scanner.put(RepeaterUnit.SEASON_PATTERN, RepeaterUnit.UnitName.SEASON);
            scanner.put(RepeaterUnit.MONTH_PATTERN, RepeaterUnit.UnitName.MONTH);
            scanner.put(RepeaterUnit.FORTNIGHT_PATTERN, RepeaterUnit.UnitName.FORTNIGHT);
            scanner.put(RepeaterUnit.WEEK_PATTERN, RepeaterUnit.UnitName.WEEK);
            scanner.put(RepeaterUnit.WEEKEND_PATTERN, RepeaterUnit.UnitName.WEEKEND);
            scanner.put(RepeaterUnit.DAY_PATTERN, RepeaterUnit.UnitName.DAY);
            scanner.put(RepeaterUnit.HOUR_PATTERN, RepeaterUnit.UnitName.HOUR);
            scanner.put(RepeaterUnit.MINUTE_PATTERN, RepeaterUnit.UnitName.MINUTE);
            scanner.put(RepeaterUnit.SECOND_PATTERN, RepeaterUnit.UnitName.SECOND);
            scanner.put(RepeaterUnit.HOUR_ABBRV_PATTERN, RepeaterUnit.UnitName.HOUR);
            scanner.put(RepeaterUnit.MINUTE_ABBRV_PATTERN, RepeaterUnit.UnitName.MINUTE);
            
            
            for (String scannerItem : scanner.keySet()) {
                if (token.getWord()
                             .matches(scannerItem)) {
                    
                    RepeaterUnit.UnitName unitNameEnum = scanner.get(scannerItem);
                    return unitNameEnum.create();
                }
            }

            return null;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create RepeaterUnit.", t);
        }
    }
    public static enum UnitName {

        DAY, FORTNIGHT, HOUR, MINUTE, MONTH, SEASON, SECOND, WEEK, WEEKEND, YEAR;

        public RepeaterUnit create() {
            switch (this) {
            case YEAR:
                return new RepeaterYear();

            case SEASON:
                return new RepeaterSeason();

            case MONTH:
                return new RepeaterMonth();

            case FORTNIGHT:
                return new RepeaterFortnight();

            case WEEK:
                return new RepeaterWeek();

            case WEEKEND:
                return new RepeaterWeekend();

            case DAY:
                return new RepeaterDay();

            case HOUR:
                return new RepeaterHour();

            case MINUTE:
                return new RepeaterMinute();

            case SECOND:
                return new RepeaterSecond();

            default:
                throw new RuntimeException();
            }
        }
        
    }
}
