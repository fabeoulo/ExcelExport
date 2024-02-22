/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.IECalendarLinkou;
import com.advantech.repo.db1.IECalendarLinkouRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class IECalendarLinkouService {

    @Autowired
    private IECalendarLinkouRepository repo;

    public List<IECalendarLinkou> findAll() {
        return repo.findAll();
    }

    public DataTablesOutput<IECalendarLinkou> findAll(DataTablesInput dti) {
        return repo.findAll(dti);
    }

    public Optional<IECalendarLinkou> findByDateMark(Date d) {
        return repo.findByDateMark(d);
    }

    public <S extends IECalendarLinkou> S save(S s) {
        return repo.save(s);
    }

    public void delete(IECalendarLinkou t) {
        repo.delete(t);
    }
}
