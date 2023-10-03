/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db2;

import com.advantech.model.db2.Items;
import com.advantech.repo.db2.ItemsRepository;
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
@Transactional("tx2")
public class ItemsService {

    @Autowired
    private ItemsRepository repo;

    public List<Items> findAll() {
        return repo.findAll();
    }

    public List<Items> findAllWithPlant(){
        return  repo.findAllWithPlant();
    }
    
    public Optional<Items> findById(Integer id) {
        return repo.findById(id);
    }

    public <S extends Items> List<S> saveAll(Iterable<S> entities) {
        return repo.saveAll(entities);
    }

}
