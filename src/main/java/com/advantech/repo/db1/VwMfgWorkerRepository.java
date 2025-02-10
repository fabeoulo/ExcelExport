/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.VwMfgWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface VwMfgWorkerRepository extends JpaRepository<VwMfgWorker, Integer>, CrudRepository<VwMfgWorker, Integer> {

    public VwMfgWorker findByJobnumber(String jobnumber);
}
