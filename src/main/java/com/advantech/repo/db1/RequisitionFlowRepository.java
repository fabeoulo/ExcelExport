/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.RequisitionFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface RequisitionFlowRepository extends JpaRepository<RequisitionFlow, Integer> {
}
