/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import static com.advantech.helper.DateConversion.*;
import com.advantech.model.db1.IECalendarLinkou;
import com.advantech.service.db1.IECalendarLinkouService;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.Entry.comparingByKey;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import static org.joda.time.DateTimeConstants.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestClass {

    @Autowired
    private IECalendarLinkouService calendarLinkouService;

    @Autowired
    private Validator validator;

//    @Test
    public void testNotNullCall() {
        testNotnull(null);
    }

    private void testNotnull(@Valid @NotNull String ss) {// NotNull useless and cannot be valid.
        boolean re = ss.equals("");
        HibernateObjectPrinter.print(ss);
    }

//    @Test
    public void testDerCool() {
        new Random().ints(1, 50)
                .distinct()
                .limit(12)
                .boxed()
                .sorted()
                .collect(Collectors.toList())
                .forEach(i -> customP(i));
    }

    public void customP(Integer i) {
        System.out.printf("Number: %d\n", i);
    }

//    @Test
    public void testDateTime() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");

        DateTime d = new DateTime();

        DateTime sDOW = d.minusDays(d.getDayOfWeek() - 1);
        DateTime eDOW = sDOW.plusDays(7 - 1);

        System.out.println(fmt.print(sDOW));
        System.out.println(fmt.print(eDOW));
    }

//    @Test
    public void testAscii() {
        String str = "&#x5c0f;&#x5149;&#x4f60;&#x597d;";
        String s = "";
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            System.out.println(str.charAt(r.nextInt(str.length())));
        }
        System.out.println(s);
    }

//    @Test
    public void testCollection() {
        List<Integer> l = newArrayList(1, 3, 4, 5, 2, 7, 8, 9, 2, 33, 14, 56, 0, 1);
        List<Integer> l2 = newArrayList(2, 9, 8, 21, 3, 9, 5, 11, 2, 14);

        System.out.println(CollectionUtils.subtract(l, l2));
        System.out.println(CollectionUtils.subtract(l2, l));
    }

//    @Test
    public void testDateConversion() {
        assertEquals(startOfDay(2018, 2, 1), fromUSWeekAndYear(2018, 4, 1));
    }

//    @Test
    public void testMapSort() {
        ImmutableMap<String, Integer> map = ImmutableMap
                .<String, Integer>builder()
                .put("一月", 15250)
                .put("七月", 37688)
                .put("三月", 27346)
                .put("九月", 6218)
                .put("二月", 18983)
                .put("五月", 31542)
                .put("六月", 48241)
                .put("八月", 26126)
                .put("四月", 51494)
                .build();

        HibernateObjectPrinter.print(map);

        Map collect = map.entrySet()
                .stream()
                .sorted(comparingByKey())
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        HibernateObjectPrinter.print(collect);

    }

//    @Test
    public void testBigDecimal() {
        String str = "9.680017514E9";
        System.out.println(Long.toString(new BigDecimal(str).longValue()));
    }

//    @Test
    public void testDateTime2() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");

        DateTime lastDateOfWeek = new DateTime().withTime(0, 0, 0, 0).dayOfWeek().withMaximumValue();
        lastDateOfWeek = lastDateOfWeek.minusDays(2);
        System.out.println(fmt.print(lastDateOfWeek));

        DateTime lastDateOfMonth = new DateTime().withTime(0, 0, 0, 0).dayOfMonth().withMaximumValue();
        int lastDateMonthOfWeek = lastDateOfMonth.getDayOfWeek();
        lastDateOfMonth = lastDateOfMonth.minusDays(lastDateMonthOfWeek == 7 ? 2 : (lastDateMonthOfWeek == 6 ? 1 : 0));
        System.out.println(fmt.print(lastDateOfMonth));

        System.out.println(LocalDate.now().compareTo(new LocalDate(lastDateOfWeek)) == 0);
    }

//    @Test
    public void testDateTime3() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");
//        DateTime d = new DateTime("2022-08-01");
//        int days = d.dayOfMonth().getMaximumValue();
//        for (int i = 0; i < days; i++) {
//            double result = WorkingDayUtils.findBusinessDayPercentage(d);            
//            System.out.println(fmt.print(d));
//            System.out.println("\t" + result);
//            System.out.println("--------------------");
//            d = d.plusDays(1);
//        }
        DateTime dt = new DateTime("2022-08-29");
        DateTime lastBusinessDay = WorkingDayUtils.findLastBusinessDay(dt.minusDays(1));
        System.out.println(fmt.print(lastBusinessDay));
    }

//    @Test
    public void testDate() {
        Date d = new Date("2019-10-15T10:45:39+08:00");
        HibernateObjectPrinter.print(d);
        DateTime dt = new DateTime(d);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d H:m:s");
        System.out.println(fmt.print(dt));
    }

    private DateTime findLastBusinessDay(DateTime dt, Set<LocalDate> holidays) {
        final Set<Integer> businessDays = newHashSet(
                MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
        );

        DateTime d = new DateTime(dt);
        while (!businessDays.contains(d.dayOfWeek().get())
                || holidays.contains(d.toLocalDate())) {
            d = d.minusDays(1);
        }
        return d;
    }

//    @Test
    public void firstBusinessDayOfMonth() {

        DateTime dt = new DateTime("2024-02-28");
        // I've hardcoded the holidays as LocalDates
        // and put them in a Set
        Set<LocalDate> holidays = newHashSet();
        List<IECalendarLinkou> l = calendarLinkouService.findAll();
        for (IECalendarLinkou c : l) {
            DateTime holiday = new DateTime(c.getDateMark());
            if (holiday.getYear() == dt.getYear() && holiday.getMonthOfYear() == dt.getMonthOfYear()) {
                holidays.add(holiday.toLocalDate());
            }
        }

        // For the sake of efficiency, I also put the business days into a Set.
        // In general, a Set has a better lookup speed than a List.
        final Set<Integer> businessDays = newHashSet(
                MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
        );

        dt = findLastBusinessDay(dt, holidays);
        if (!businessDays.contains(dt.dayOfWeek().get())) {
//            return -1d;
        }

        int period = new DateTime(dt).dayOfMonth().getMaximumValue();

        int curr = 0, total = 0;

        dt = new DateTime(dt).withTime(0, 0, 0, 0);
        DateTime d = new DateTime(dt).dayOfMonth().withMinimumValue().withTime(0, 0, 0, 0);
        System.out.println(period);

        for (int i = 1; i <= period; i++) {
            if (businessDays.contains(d.dayOfWeek().get())
                    && !holidays.contains(d.toLocalDate())) {
                total++;
                if (d.isEqual(dt)) {
                    curr = total;
                }
            }
            d = d.plusDays(1);
        }

        System.out.println(curr);
        System.out.println(total);

        double r = curr * 1.0 / total;
    }

//    @Test
    public void testString() {
        String str = "000000001700014718";

        System.out.println(str.replaceAll("\\s+$", "0"));
        System.out.println(CharMatcher.is('0').trimLeadingFrom(str));
    }
}
