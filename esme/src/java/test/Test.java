import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 2/21/14
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {

        DateTime startFrom = new DateTime();
        DateTime today = new DateTime();
        today = today.plusDays(1);
        if (today.getDayOfMonth() <= 25 ) {
            /* Start from last month. */
            startFrom = startFrom.minusMonths(1);
        }
        startFrom = startFrom.withDayOfMonth(26).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

        System.out.println(startFrom);
    }
}
