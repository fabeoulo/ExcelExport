/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.ScrappedRequisition;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface ScrappedRepository extends JpaRepository<Requisition, Integer> {

    @Query(value = "{CALL usp_Excel_Scrapped_M9All (?1, ?2)}", nativeQuery = true)
    public List<ScrappedRequisition> findAllScrapped(Date sD, Date eD);

    @Query(value = "{CALL usp_Excel_Scrapped_M9All_Json (?1, ?2)}", nativeQuery = true)
    public Object findAllScrappedByTbfn(Date sD, Date eD);
}
