/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.ScrappedSummary;
import com.advantech.repo.db1.ScrappedSummaryRepository;
import java.util.List;
import javax.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class ScrappedSummaryService {

    @Autowired
    private ScrappedSummaryRepository repo;

    public List<ScrappedSummary> findAll() {
        return repo.findAll();
    }

    public List<ScrappedSummary> findAllByYkBetween(int from, int to) {
        return repo.findAllByYkBetween(from, to);
    }

    public List<Tuple> findAllReportByYkBetween(int from, int to, int fromLast26, int toLast26) {
        return repo.findAllReportByYkBetween(from, to, fromLast26, toLast26);
    }

    public <S extends ScrappedSummary> S save(S s) {
        return repo.save(s);
    }
}
