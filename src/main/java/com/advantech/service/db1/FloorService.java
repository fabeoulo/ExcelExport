/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Floor;
import com.advantech.repo.db1.FloorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("tx1")
public class FloorService {

    @Autowired
    private FloorRepository repo;

    public List<Floor> findAll() {
        return repo.findAll();
    }

    public List<Floor> findAllEnableState() {
        return repo.findAllByEnableState(1);
    }

    public Optional<Floor> findById(Integer id) {
        return repo.findById(id);
    }

}
