package codingblackfemales.gettingstarted;


import java.time.LocalTime;

public class TradingDayClockService {

    // Represents the current time in the trading day
    private static LocalTime currentTime = LocalTime.of(8,0); // Default start time at 8:00 AM

    // Returns the current time in the trading day
    public static LocalTime getCurrentTime() {
        return currentTime;
    }

    // Sets a new time for the trading day.
    public static void setCurrentTime(LocalTime newTime) {
        currentTime = newTime;
    }

    /**
     * Checks if the current time has reached or passed the end of the trading day.
     * The end of the trading day is at 5:00 PM (17:00).
     * @return True if the time is 5:00 PM or later; otherwise, false.
     * currentTime is exactly 5:00 PM (returns 0), or later than 5:00 PM (returns 1)
     */
    public static boolean isEndOfDay() {
        int currentTimeComparison = currentTime.compareTo(LocalTime.of(17,0)) ; //compares currentTime to 5:00 PM
        return currentTimeComparison == 0 ||  currentTimeComparison == 1;
    }
}
