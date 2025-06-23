/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.RequisitionCateIms;
import com.advantech.repo.db1.RequisitionCateImsRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class RequisitionCateImsService {

    @Autowired
    private RequisitionCateImsRepository repo;

    public List<RequisitionCateIms> findAll() {
        return repo.findAll();
    }

    public List<RequisitionCateIms> findAllWithFloor() {
        return repo.findAllWithFloor();
    }

    public RequisitionCateIms getOne(Integer id) {
        return repo.getOne(id);
    }

    public Optional<RequisitionCateIms> findById(Integer id) {
        return repo.findById(id);
    }
}
