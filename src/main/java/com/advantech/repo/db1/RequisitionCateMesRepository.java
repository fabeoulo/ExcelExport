/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.RequisitionCateMes;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface RequisitionCateMesRepository extends JpaRepository<RequisitionCateMes, Integer> {

    @EntityGraph(attributePaths = {"requisitionCateImss"})
    @Query("SELECT mes FROM RequisitionCateMes mes") // prevent jpa from querying by method name "findAllWithFloor"
    public List<RequisitionCateMes> findAllWithCateIms();
}
