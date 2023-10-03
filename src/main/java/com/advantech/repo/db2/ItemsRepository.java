/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db2;

import com.advantech.model.db2.Items;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface ItemsRepository extends JpaRepository<Items, Integer> {
        
    @Query("SELECT i FROM Items i JOIN FETCH i.orders o JOIN FETCH o.teams t "
            + " WHERE t.plant IS NOT NULL AND i.mrpSync IS FALSE ORDER BY i.id")
    public List<Items> findAllWithPlant();
}
