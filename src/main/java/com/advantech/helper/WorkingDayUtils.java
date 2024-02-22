/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.advantech.model.db1.IEWorkdayCalendar;
import com.advantech.service.db1.IEWorkdayCalendarService;
import static com.google.common.collect.Sets.newHashSet;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WorkingDayUtils {

    private final static Set<LocalDate> holidays = new HashSet(0);

    private final static Set<Integer> businessDays = newHashSet(
            MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    );

    @Autowired
    private IEWorkdayCalendarService workdayCalendarService;

    public static DateTime findLastBusinessDay(DateTime dt) {
        DateTime d = new DateTime(dt);
        while (!businessDays.contains(d.dayOfWeek().get())) {
            d = d.minusDays(1);
        }
        return d;
    }

    public static double findBusinessDayPercentage(DateTime dt) {
        // I've hardcoded the holidays as LocalDates
        // and put them in a Set
        // For the sake of efficiency, I also put the business days into a Set.
        // In general, a Set has a better lookup speed than a List.

        if (!businessDays.contains(dt.dayOfWeek().get())) {
            return -1d;
        }

        int period = new DateTime(dt).dayOfMonth().getMaximumValue();

        int curr = 0, total = 0;

        dt = new DateTime(dt).withTime(0, 0, 0, 0);
        DateTime d = new DateTime(dt).dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0);

        for (int i = 1; i <= period; i++) {
            if (businessDays.contains(d.dayOfWeek().get())) {
                total++;
                if (d.isEqual(dt)) {
                    curr = total;
                }
            }
            d = d.plusDays(1);
        }

        return curr * 1.0 / total;
    }

    public double findBusinessDayPercentageByDb(DateTime now) {

        DateTime dt = now.minusDays(1);
        List<IEWorkdayCalendar> monthWorkdays = findMonthWorkdays(dt);

        Predicate<IEWorkdayCalendar> isPastWorkday = wc -> new DateTime(wc.getDate()).compareTo(dt) <= 0;
        boolean hasWorkdays = monthWorkdays.stream().anyMatch(isPastWorkday);
        if (!hasWorkdays) {
            DateTime preMonth = dt.minusMonths(1);
            monthWorkdays = findMonthWorkdays(preMonth);
        }

        int curr = 0, total = 0;
        curr = monthWorkdays.stream().filter(isPastWorkday)
                .mapToInt(wc -> 1).sum();
        total = monthWorkdays.size();

        return curr * 1.0 / total;
    }

    private List<IEWorkdayCalendar> findMonthWorkdays(DateTime dt) {
        List<IEWorkdayCalendar> l = workdayCalendarService.findByYearMonth(dt);
        l = updateCalendar(dt, l);
        return l.stream().filter(c -> c.getWorkdayFlag() == 1)
                .collect(Collectors.toList());
    }

    private List<IEWorkdayCalendar> updateCalendar(DateTime dt, List<IEWorkdayCalendar> l) {
        Date lastDayOfMonth = dt.dayOfMonth().withMaximumValue().toLocalDate().toDate();
        boolean hasLastDay = l.stream().anyMatch(c -> c.getDate().compareTo(lastDayOfMonth) == 0);
        if (!hasLastDay) {
            workdayCalendarService.calculateIEWorkdayCalendar();
            l = workdayCalendarService.findByYearMonth(dt);
        }
        return l;
    }
}
