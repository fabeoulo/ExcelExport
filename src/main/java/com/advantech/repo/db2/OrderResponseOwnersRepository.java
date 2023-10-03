/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.repo.db2;

import com.advantech.model.db2.MaterialMrp;
import com.advantech.model.db2.OrderResponseOwners;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface OrderResponseOwnersRepository extends JpaRepository<OrderResponseOwners, Integer> {
    
    public List<OrderResponseOwners> findByMrpCodeIn(List<String> mrpCodes);    
}
