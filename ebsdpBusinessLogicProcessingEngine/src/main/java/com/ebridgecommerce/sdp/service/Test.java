package com.ebridgecommerce.sdp.service;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 7/31/12
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");

        DateTime from = new DateTime();
        from = from.withHourOfDay(0);
        from = from.withMinuteOfHour(0);
        from = from.withSecondOfMinute(0);

        DateTime to = new DateTime();
        to = to.withHourOfDay(23);
        to = to.withMinuteOfHour(59);
        to = to.withSecondOfMinute(59);

        System.out.println("No bonus awarded between " + dateFormat.format(from.toDate()) + " " +
                hourFormat.format(from.toDate()) +  " and " + hourFormat.format(to.toDate()));

    }
}
