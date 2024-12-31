/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.VwM3Worktime;
import com.advantech.repo.db1.VwM3WorktimeRepository;
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
public class VwM3WorktimeService {

    @Autowired
    private VwM3WorktimeRepository repo;

    public List<VwM3Worktime> findAll() {
        return repo.findAll();
    }

    public List<VwM3Worktime> findAllByModelName(List<String> modelNames) {
        return repo.findAllByModelNameIn(modelNames);
    }

}
