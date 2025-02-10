/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.VwMfgWorker;
import com.advantech.repo.db1.VwMfgWorkerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("tx1")
public class VwMfgWorkerService {

    @Autowired
    private VwMfgWorkerRepository repo;

    public List<VwMfgWorker> findAll() {
        return repo.findAll();
    }

    public VwMfgWorker findByJobnumber(String jobnumber) {
        return repo.findByJobnumber(jobnumber);
    }
}
