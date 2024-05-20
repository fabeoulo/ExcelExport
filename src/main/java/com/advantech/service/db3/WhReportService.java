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
        List<WhReport> l = whReportRepository.findDailyWhReportWc(fmt.print(dt));
        return reorder(l);
    }

    public List<WhReport> findWeeklyWhReportWc(DateTime dt) {
        List<WhReport> l = whReportRepository.findWeeklyWhReportWc(fmt.print(dt));
        return reorder(l);
    }

    public List<WhReport> findMonthlyWhReportWc(DateTime dt) {
        List<WhReport> l = whReportRepository.findMonthlyWhReportWc(fmt.print(dt));
        return reorder(l);
    }

    private List<WhReport> reorder(List<WhReport> l) {
        return l.stream()
                .sorted(
                        Comparator.comparing(WhReport::convertPlant)
                                .thenComparing(WhReport::getDateField)
                ).collect(Collectors.toList());
    }
}
