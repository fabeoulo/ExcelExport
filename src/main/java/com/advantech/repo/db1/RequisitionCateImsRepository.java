/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.RequisitionCateIms;
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
public interface RequisitionCateImsRepository extends JpaRepository<RequisitionCateIms, Integer> {

    @EntityGraph(attributePaths = {"floors"})
    @Query("SELECT ims FROM RequisitionCateIms ims") // prevent jpa from querying by method name "findAllWithFloor"
    public List<RequisitionCateIms> findAllWithFloor();
}
