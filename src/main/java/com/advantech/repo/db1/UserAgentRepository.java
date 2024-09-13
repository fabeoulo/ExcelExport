/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.repo.db1;

import com.advantech.model.db1.UserAgent;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public interface UserAgentRepository extends JpaRepository<UserAgent, Integer>, DataTablesRepository<UserAgent, Integer> {

    public Optional<UserAgent> findByBeginDate(Date d);
}
