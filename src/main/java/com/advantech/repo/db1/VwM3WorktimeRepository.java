/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.VwM3Worktime;
import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface VwM3WorktimeRepository extends JpaRepository<VwM3Worktime, Integer>, DataTablesRepository<VwM3Worktime, Integer> {

    public List<VwM3Worktime> findAllByModelNameIn(List<String> modelNames);

}
