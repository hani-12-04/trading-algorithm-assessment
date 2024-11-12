package codingblackfemales.gettingstarted;

import java.time.LocalTime;

public class TradingDayClockService {
    private static LocalTime currentTime = LocalTime.of(8,0);


    public static LocalTime getCurrentTime() {
        return currentTime;
    }

    public static void setCurrentTime(LocalTime newTime) {
        currentTime = newTime;
    }

    public static boolean isEndOfDay() {
        int currentTimeComparison = currentTime.compareTo(LocalTime.of(17,0)) ;
        return currentTimeComparison == 0 ||  currentTimeComparison == 1;
    }
}
