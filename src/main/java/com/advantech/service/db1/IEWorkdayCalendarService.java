/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.IEWorkdayCalendar;
import com.advantech.repo.db1.IEWorkdayCalendarRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class IEWorkdayCalendarService {

    @Autowired
    private IEWorkdayCalendarRepository repo;

    public List<IEWorkdayCalendar> findAll() {
        return repo.findAll();
    }

    public DataTablesOutput<IEWorkdayCalendar> findAll(DataTablesInput dti) {
        return repo.findAll(dti);
    }

    public Optional<IEWorkdayCalendar> findByDate(Date d) {
        return repo.findByDate(d);
    }

    public List<IEWorkdayCalendar> findByYearMonth(DateTime dt) {
        String ym = dt.getYear() + "/" + String.format("%02d", dt.getMonthOfYear());
        return repo.findByYearMonth(ym);
    }

    public <S extends IEWorkdayCalendar> S save(S s) {
        return repo.save(s);
    }

    public void delete(IEWorkdayCalendar t) {
        repo.delete(t);
    }

    public void calculateIEWorkdayCalendar() {
        repo.calculateIEWorkdayCalendar();
    }
}
