/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db2;

import com.advantech.model.db2.Orders;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    @Modifying(clearAutomatically = true)
    @Query(value = "update orders set time_close = 0, estimate_date = 0 where orders.id =:entryId",
            nativeQuery = true)
    public void updateTimeStampToZero(@Param("entryId") Integer entryId);

    @Modifying
    @Query(value = "UPDATE orders SET time_close = 0, estimate_date = 0 WHERE id IN ?1",
            nativeQuery = true)
    public void updateTimeStampToZeroByIdIn(List<Integer> ids);
    
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.users LEFT JOIN FETCH o.itemses LEFT JOIN FETCH o.teams"
            + " WHERE o.timeClose = 0 AND o.requisionId IS NOT NULL AND o.teams.id =:teamsId")
    public List<Orders> findAllLackWithUserItem(@Param("teamsId") Integer teamsId);

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.replieses JOIN FETCH o.itemses JOIN FETCH o.teams"
            + " WHERE o.timeClose = 0 AND SIZE(o.replieses) = 0 AND o.orderResponse.id IN (2,3)")
    public List<Orders> findAllOpenWithoutReply();
}
