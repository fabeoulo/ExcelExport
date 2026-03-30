/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.ScrappedSummary;
import java.util.List;
import javax.persistence.Tuple;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Justin.Yeh
 */
public interface ScrappedSummaryRepository extends JpaRepository<ScrappedSummary, Integer>, DataTablesRepository<ScrappedSummary, Integer> {

    public List<ScrappedSummary> findAllByYkBetween(int from, int to);

    @Query(value = "{CALL usp_Excel_Scrapped_Report_M9All (?1, ?2, ?3, ?4)}", nativeQuery = true)
    public List<Tuple> findAllReportByYkBetween(int from, int to, int fromLast26, int toLast26);
}
