/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.RequisitionFlow;
import com.advantech.repo.db1.RequisitionFlowRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class RequisitionFlowService {

    @Autowired
    private RequisitionFlowRepository repo;

    public List<RequisitionFlow> findAll() {
        return repo.findAll();
    }

    public RequisitionFlow getOne(Integer id) {
        return repo.getOne(id);
    }

}
