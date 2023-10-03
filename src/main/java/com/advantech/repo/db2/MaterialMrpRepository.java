/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.repo.db2;

import com.advantech.model.db2.MaterialMrp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Justin.Yeh
 */
public interface MaterialMrpRepository extends JpaRepository<MaterialMrp, Integer> {

    public List<MaterialMrp> findByPlantInAndMatNameIn(List<String> plants, List<String> materials);
}
