package jrds;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Class used to manage a time interval
 *
 * @author Fabrice Bacchella
 */
public class Period {
    static final private Logger logger = LogManager.getLogger(Period.class);
    static private final String dateRegexpBoth = "((\\d\\d\\d\\d)-?(\\d\\d)-?(\\d\\d))?";
    static private final String timeRegexp = "((\\d?\\d):(\\d\\d))?(:(\\d\\d))?";
    static private final Pattern datePatternBoth = Pattern.compile( dateRegexpBoth+ "[T ]?" + timeRegexp + "(.*)");
    static private final Pattern secondsPattern = Pattern.compile( "\\d+");

    public enum PeriodItem {
        MANUAL("Manual", new org.joda.time.Period()),
        HOUR("Last Hour", org.joda.time.Period.hours(1)),
        HOURS2("Last 2 Hours", org.joda.time.Period.hours(2)),
        HOURS3("Last 3 Hours", org.joda.time.Period.hours(3)),
        HOURS4("Last 4 Hours", org.joda.time.Period.hours(4)),
        HOURS6("Last 6 Hours", org.joda.time.Period.hours(6)),
        HOURS12("Last 12 Hours", org.joda.time.Period.hours(12)),
        DAY("Last Day", org.joda.time.Period.days(1)),
        DAYS2("Last 2 Days", org.joda.time.Period.days(2)),
        WEEK("Last Week", org.joda.time.Period.weeks(1)),
        WEEKS2("Last 2 Weeks", org.joda.time.Period.weeks(2)),
        MONTH("Last Month", org.joda.time.Period.months(1)),
        MONTH2("Last 2 Months", org.joda.time.Period.months(2)),
        MONTH3("Last 3 Months", org.joda.time.Period.months(3)),
        MONTH4("Last 4 Months", org.joda.time.Period.months(4)),
        MONTH6("Last 6 Months", org.joda.time.Period.months(6)),
        YEAR("Last Year", org.joda.time.Period.years(1)),
        YEARS2("Last 2 Years", org.joda.time.Period.years(2));
        public final String name;
        public final org.joda.time.Period p;
        PeriodItem(String name, org.joda.time.Period p) {
            this.name = name;
            this.p = p;
        }
    }

    /**
     * 
     */
    private final DateTime begin;
    private final DateTime end;
    private final int calPeriod;
    private final org.joda.time.Period period;

    public Period() {
        calPeriod = 7;
        period = PeriodItem.values()[calPeriod].p;
        end = new DateTime().minusSeconds(1);
        begin = new DateTime().minus(period);
    }

    public Period(int p) {
        if(p > PeriodItem.values().length) {
            throw new RuntimeException("Period invalid: " + p);
        }
        calPeriod = p;
        period = PeriodItem.values()[calPeriod].p;
        end = new DateTime().minusSeconds(1);
        begin = new DateTime().minus(period);
    }

    public Period(String begin, String end) throws ParseException {
        this.begin = string2Date(begin, true);
        this.end = string2Date(end, false);
        this.calPeriod = 0;
        period = (new org.joda.time.Period(this.begin, this.end.plusSeconds(1)));
        logger.trace(Util.delayedFormatString("Period is %s", period));
    }

    private Period(DateTime begin, DateTime end) {
        this.begin = begin;
        this.end = end;
        this.calPeriod = 0;
        period = (new org.joda.time.Period(this.begin, this.end.plusSeconds(1)));
        logger.trace(Util.delayedFormatString("Period is %s", period));        
    }

    public Period previous() {
        Period next = new Period(begin.minus(period), end.minus(period));
        return next;
    }

    public Period next() {
        Period next = new Period(begin.plus(period), end.plus(period));
        return next;
    }

    /**
     * @return Returns the begin of the period.
     */
    public Date getBegin() {
        return begin.toDate();
    }

    /**
     * @return Returns the end of the period.
     */
    public Date getEnd() {
        return end.toDate();
    }

    /**
     * @return the calPeriod
     */
    public int getScale() {
        return calPeriod;
    }

    /**
     * Calculate date from string parameters coming from the URL
     *
     * @param sbegin String
     * @param send String
     * @param begin The calculated begin date
     * @param end The calculated end date
     * @throws ParseException 
     */
    private DateTime string2Date(String date, boolean isBegin) throws ParseException{
        if(date == null) {
            throw new ParseException("Null string to parse", 0);
        }
        Matcher dateMatcher = datePatternBoth.matcher(date);
        if("NOW".compareToIgnoreCase(date) == 0) {
            return new DateTime();
        }
        else if(secondsPattern.matcher(date).matches()) {
            return new DateTime(Util.parseStringNumber(date, Long.MIN_VALUE).longValue());
        }
        else if(date.length() >= 4 && dateMatcher.find()) {
            try {
                if(logger.isTraceEnabled()) {
                    logger.trace("Matching " + date);
                    for(int i = 1; i <= dateMatcher.groupCount(); i++) {
                        logger.trace(i +": " + "'" + dateMatcher.group(i) + "'");
                    }
                }
                String dateFound = dateMatcher.group(1);
                String timeFound = dateMatcher.group(5);
                String secondFound = dateMatcher.group(8);
                String timeZoneFound = dateMatcher.group(10);
                if(dateFound == null && timeFound == null && secondFound == null) {
                    throw new ParseException("Invalid string to parse: " + date, 0);
                }

                DateTimeZone tz = DateTimeZone.getDefault();

                if( timeZoneFound != null &&  ! "".equals(timeZoneFound)) {
                    tz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneFound));
                }

                LocalDate jdate = new LocalDate(tz);
                if(dateFound != null && ! "".equals(dateFound)) {
                    int year = jrds.Util.parseStringNumber(dateMatcher.group(2), 1970);
                    int month = jrds.Util.parseStringNumber(dateMatcher.group(3), 1);
                    int day = jrds.Util.parseStringNumber(dateMatcher.group(4), 1);
                    jdate = new LocalDate(year, month, day);
                }

                int hour;
                int minute;
                int second;
                if(timeFound == null || "".equals(timeFound)) {
                    if(isBegin) {
                        hour = 0;
                        minute = 0;
                    }
                    else {
                        hour = 23;
                        minute = 59;
                    }
                }
                else {
                    hour = jrds.Util.parseStringNumber(dateMatcher.group(6), 0);
                    minute = jrds.Util.parseStringNumber(dateMatcher.group(7), 0);
                }

                if(secondFound == null || "".equals(secondFound)) {
                    if(isBegin)
                        second = 0;
                    else
                        second = 59;
                }
                else {
                    second = jrds.Util.parseStringNumber(dateMatcher.group(9), 0);
                }
                LocalTime jtime = new LocalTime(hour, minute, second);
                DateTime dt = jdate.toDateTime(jtime, tz);
                return dt;
            } catch (Exception e) {
                ParseException newex = new ParseException("Invalid string to parse: " + date, 0);
                newex.initCause(e);
                throw newex;
            }
        }
        else {
            throw new ParseException("Invalid string to parse: " + date, 0);
        }
    }

    /**
     * Return the list of period label
     * 
     * @return a list of string label
     */
    static public List<String> getPeriodNames() {
        List<String> periodName = new ArrayList<String>(PeriodItem.values().length);
        for(PeriodItem pi: PeriodItem.values()) 
            periodName.add(pi.name);
        return periodName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((begin == null) ? 0 : begin.hashCode());
        result = PRIME * result + calPeriod;
        result = PRIME * result + ((end == null) ? 0 : end.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Period other = (Period) obj;
        if (begin == null) {
            if (other.begin != null)
                return false;
        } else if (!begin.equals(other.begin))
            return false;
        if (calPeriod != other.calPeriod)
            return false;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "b=" + begin + ", e=" + end + ", s=" + PeriodItem.values()[calPeriod].name;
    }

}
