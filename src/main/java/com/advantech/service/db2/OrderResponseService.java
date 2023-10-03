/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db2;

import com.advantech.model.db2.OrderResponse;
import com.advantech.repo.db2.OrderResponseRepository;
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
@Transactional("tx2")
public class OrderResponseService {
    
    @Autowired
    private OrderResponseRepository repo;
    
    public List<OrderResponse> findAll() {
        return repo.findAll();
    }

    public Optional<OrderResponse> findById(Integer id) {
        return repo.findById(id);
    }
}
