/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db2;

import com.advantech.model.db2.OrderResponseOwners;
import com.advantech.repo.db2.OrderResponseOwnersRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx2")
public class OrderResponseOwnersService {

    @Autowired
    private OrderResponseOwnersRepository repo;

    public List<OrderResponseOwners> findAll() {
        return repo.findAll();
    }

    public Optional<OrderResponseOwners> findById(Integer id) {
        return repo.findById(id);
    }

    public List<OrderResponseOwners> findByMrpCodeIn(List<String> mrpCodes) {
        return repo.findByMrpCodeIn(mrpCodes);
    }

    public Map<String, String> getMrpOwnerMap(List<String> mrpCodes) {
        List<OrderResponseOwners> l = this.findByMrpCodeIn(mrpCodes);
        return l.stream().collect(Collectors.toMap(
                OrderResponseOwners::getMrpCode,
                o -> o.getUsers().getId(),
                (oldValue, newValue) -> newValue
        ));
    }
}
