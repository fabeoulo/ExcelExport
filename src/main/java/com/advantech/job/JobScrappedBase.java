/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import org.joda.time.DateTime;

/**
 *
 * @author Justin.Yeh
 */
public abstract class JobScrappedBase extends SendEmailBase {

    protected DateTime lastWeek, thisMon, startDt, endDt, startDtLast26, endDtLast26;
    protected int lastWeekNo, lastWeekYear, lastWeekYw;

    protected void setDateTime(DateTime now) {
        now = now.withTime(0, 0, 0, 0); // Iso week changed at midnight on Monday.
        lastWeek = now.minusWeeks(1);
        thisMon = now.dayOfWeek().withMinimumValue();
        lastWeekNo = lastWeek.getWeekOfWeekyear();
        lastWeekYear = lastWeek.getWeekyear();
        lastWeekYw = lastWeekYear * 100 + lastWeekNo;

        if (lastWeekNo <= 17) {
            startDt = lastWeek.withYear(lastWeekYear - 1).withWeekOfWeekyear(44).dayOfWeek().withMinimumValue();
            endDt = thisMon;
            startDtLast26 = startDt.minusWeeks(26);
            endDtLast26 = startDt;
        } else if (lastWeekNo >= 44) {
            startDt = lastWeek.withWeekOfWeekyear(44).dayOfWeek().withMinimumValue();
            endDt = thisMon;
            startDtLast26 = startDt.minusWeeks(26);
            endDtLast26 = startDt;
        } else //(lastWeekNo > 17 && lastWeekNo < 44) 
        {
            startDt = lastWeek.withWeekOfWeekyear(18).dayOfWeek().withMinimumValue();
            endDt = thisMon;
            startDtLast26 = lastWeek.withYear(lastWeekYear - 1).withWeekOfWeekyear(44).dayOfWeek().withMinimumValue();
            endDtLast26 = startDt;
        }
    }
}
