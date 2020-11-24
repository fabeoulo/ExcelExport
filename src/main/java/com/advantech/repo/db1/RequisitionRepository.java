/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.Requisition;
import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.advantech.model.db1.ModelMaterialDetails;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Integer>, DataTablesRepository<Requisition, Integer> {

    @Query(value = "{CALL usp_qryModelMaterialMap(:modelName)}",
            nativeQuery = true)
    public List<ModelMaterialDetails> findModelMaterialDetails(@Param("modelName") String modelName);
    
}
