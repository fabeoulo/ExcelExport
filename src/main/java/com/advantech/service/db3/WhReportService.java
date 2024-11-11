/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db3;

import com.advantech.repo.db3.WhReportRepository;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.advantech.model.db3.WhReport;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx3")
public class WhReportService {

    @Autowired
    private WhReportRepository whReportRepository;

    private final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    public List<WhReport> findDailyWhReport(DateTime dt, List<String> plants) {
        return whReportRepository.findDailyWhReport(fmt.print(dt), plants);
    }

    public List<WhReport> findWeeklyWhReport(DateTime dt, List<String> plants) {
        return whReportRepository.findWeeklyWhReport(fmt.print(dt), plants);
    }

    public List<WhReport> findMonthlyWhReport(DateTime dt, List<String> plants) {
        return whReportRepository.findMonthlyWhReport(fmt.print(dt), plants);
    }

    public List<WhReport> findDailyWhReportWc(DateTime dt) {
        List<String> pastDays = findSdEd(dt, 7);
        List<WhReport> l = whReportRepository.findDailyWhReportWc(pastDays.get(0), pastDays.get(1));
        return reorder(l);
    }

    public List<WhReport> findWeeklyWhReportWc(DateTime dt) {
        List<String> pastDays = findSdEd(dt, 28);
        List<WhReport> l = whReportRepository.findWeeklyWhReportWc(pastDays.get(0), pastDays.get(1));
        return reorder(l);
    }

    public List<WhReport> findMonthlyWhReportWc(DateTime dt) {
        List<String> pastDays = findPastMonth(dt);
        List<WhReport> l = whReportRepository.findMonthlyWhReportWc(pastDays.get(0), pastDays.get(1));
        return reorder(l);
    }

    private List<String> findSdEd(DateTime dt, int interval) {
        List<String> days = Lists.newArrayList();

        days.add(fmt.print(dt.minusDays(interval)));
        days.add(fmt.print(dt.minusDays(1)));
        return days;
    }

    private List<String> findPastMonth(DateTime dt) {
        List<String> days = Lists.newArrayList();

        int dayOfWeek = dt.getDayOfWeek();
        int dayOfMonth = dt.getDayOfMonth();
        if (dayOfMonth == 2 && dayOfWeek == 1) { // 2th-day on monday
            return findPastMonth(dt.minusDays(1));
        }

        DateTime endDate = dt.minusDays(1);
        String sd = fmt.print(endDate.dayOfMonth().withMinimumValue());
        String ed = fmt.print(endDate);

        days.add(sd);
        days.add(ed);
        return days;
    }

    private List<WhReport> reorder(List<WhReport> l) {
        return l.stream()
                .sorted(
                        Comparator.comparing(WhReport::convertPlant)
                                .thenComparing(WhReport::getDateField)
                ).collect(Collectors.toList());
    }
}
