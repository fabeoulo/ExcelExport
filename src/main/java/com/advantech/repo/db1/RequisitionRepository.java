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
import java.util.Date;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Integer>, DataTablesRepository<Requisition, Integer> {

    @Query(value = "{CALL usp_qryModelMaterialMap(:modelName)}",
            nativeQuery = true)
    public List<ModelMaterialDetails> findModelMaterialDetails(@Param("modelName") String modelName);

    @EntityGraph(attributePaths = {"user", "requisitionState"})
    public List<Requisition> findAllByIdIn(List<Integer> ids);

    @EntityGraph(attributePaths = {"requisitionState", "requisitionReason", "requisitionType", "requisitionFlow", "floor", "user"})
    public List<Requisition> findAllByPoInAndMaterialNumberIn(List<String> po, List<String> modelName);

    @EntityGraph(attributePaths = {"user"})
    public List<Requisition> findAllByCreateDateGreaterThanAndRequisitionState_Id(Date td, int stateId);

    public List<Requisition> findAllByPoIn(List<String> po);

    @EntityGraph(attributePaths = {"user"})
    public List<Requisition> findAllByCreateDateGreaterThanAndRequisitionState_IdAndFloor_IdIn(Date td, int stateId, List<Integer> floorId);

    @EntityGraph(attributePaths = {"requisitionState", "requisitionReason", "requisitionType", "requisitionFlow", "floor", "user", "requisitionCateIms", "requisitionCateMes"})
    public List<Requisition> findAllByReturnDateBetweenAndRequisitionType_IdInAndFloor_IdIn(Date sdt, Date edt, List<Integer> typeIds, List<Integer> floorIds);

    public List<Requisition> findAllByPoInAndFloor_IdIn(List<String> po, List<Integer> floorId);
}
