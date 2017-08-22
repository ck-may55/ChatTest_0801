package com.example.chie.notifitest0429;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by chie on 2017/08/22.
 */

public class DateUtils {
    /**
     *
     * @param date
     * @return
     */
    public static int getTimeNumberFromDate(Date date) {

        TimeZone tz = TimeZone.getTimeZone("UTC");

        Date dStart = new Date(0);

        int jStartNum = (int)(dStart.getTime() / 1000L);

        Calendar calIStart = Calendar.getInstance(tz);
        calIStart.set(2001, 0, 1, 0, 0, 0);
        Date iStart = calIStart.getTime();
        int iStartNum = (int)(iStart.getTime() / 1000L);

        int diffSeconds = iStartNum - jStartNum;

        int timeNumber = (int)(date.getTime() / 1000L) - diffSeconds;

        //Log.d("DateUtils", "getTimeNumberFromDate timeNumber diffSeconds jStart iStart " + timeNumber + ":" + diffSeconds + ":" + jStartNum + ":" + iStartNum);
        return timeNumber;
    }
}
