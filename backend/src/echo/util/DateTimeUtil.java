package echo.util;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtil {
    // static method for convert String to LocalDateTime
    private static final Pattern pattern = Pattern.compile("Date: (\\d{4})-(\\d{2})-(\\d{2}), Time: (\\d{2}):(\\d{2}):(\\d{2})");
    public static LocalDateTime parse(String dateTime) {
        Matcher matcher = pattern.matcher(dateTime);
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));
        int hour = Integer.parseInt(matcher.group(4));
        int minute = Integer.parseInt(matcher.group(5));
        int second = Integer.parseInt(matcher.group(6));
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    // static method for convert LocalDateTime to String
    public static String format(LocalDateTime time) {
        return String.format("Date: %04d-%02d-%02d, Time: %02d:%02d:%02d",
                                    time.getYear(), time.getMonthValue(), time.getDayOfMonth(),
                                    time.getHour(), time.getMinute(), time.getSecond());
    }

    // static method to get current date and time
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // static method for checking user account lock
    public static boolean isFinished(LocalDateTime time) {
        return LocalDateTime.now().isAfter(time);
    } 

    // static method for lock user account
    public static LocalDateTime plusMinutes(LocalDateTime currentTime, long minutes) {
        return currentTime.plusMinutes(minutes);
    }
}
