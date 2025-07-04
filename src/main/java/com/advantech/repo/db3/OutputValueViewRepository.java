/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.repo.db3;

import com.advantech.model.db3.JpaAbstractEntity;
import com.advantech.model.db3.OutputValueView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh // target tables doesn't have PK field.
 */
@Repository
public interface OutputValueViewRepository extends JpaRepository<JpaAbstractEntity, Integer> {

    public static final String OUTPUTVALUEGROUP
            = "SELECT \"ERDAT\", ROUND(SUM(\"ZACTCST\"),0) AS \"StandardCost\", ROUND(SUM(\"MENGE\"),0) AS \"Quantity\" , \"SWERK\" "
            + "FROM rv_biprd_ztpp_zrpp87s GROUP BY \"ERDAT\", \"SWERK\" ";

    public static final String OUTPUTVALUEGROUPWC
            = "SELECT \"ERDAT\", ROUND(SUM(\"ZACTCST\"),0) AS \"StandardCost\", ROUND(SUM(\"MENGE\"),0) AS \"Quantity\" , \"SWERK1\" "
            + "FROM ( "
            + "SELECT *"
            + ", CASE WHEN \"SWERK\"='TWM3' OR \"ZZCFTNO\" LIKE 'MS3F%' THEN 'TWM3' "
            + "WHEN \"SWERK\"='TWM6' THEN 'TWM6' "
            + "WHEN \"ZZCFTNO\" LIKE 'MS4F%' THEN 'TWM9' END AS \"SWERK1\" "
            + "FROM rv_biprd_ztpp_zrpp87s "
            + "WHERE \"SWERK\" IN ('TWM3','TWM6') OR \"ZZCFTNO\" LIKE 'MS3F%' OR \"ZZCFTNO\" LIKE 'MS4F%' "
            + ") AS filterTb  "
            + "GROUP BY \"ERDAT\", \"SWERK1\" ";

    @Query(value
            = "SELECT \"ERDAT\", ROUND(SUM(\"ZACTCST\"),0) AS \"StandardCost\", ROUND(SUM(\"MENGE\"),0) AS \"Quantity\" , \"SWERK\" AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp87s "
            + "WHERE \"SWERK\" IN ?1 AND \"ERDAT\" IN ?2 "
            + "GROUP BY \"ERDAT\", \"SWERK\" ",
            nativeQuery = true)
    public List<OutputValueView> findGroupByDateInAndPlantIn(List<String> plants, List<String> dates);

    @Query(value
            = "SELECT \"ERDAT\", ROUND(SUM(\"ZACTCST\"),0) AS \"StandardCost\", ROUND(SUM(\"MENGE\"),0) AS \"Quantity\" , \"SWERK\" AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp87s "
            + "WHERE \"ZZCFTNO\" IN ?1 AND \"ERDAT\" IN ?2 "
            + "GROUP BY \"ERDAT\", \"SWERK\" ",
            nativeQuery = true)
    public List<OutputValueView> findGroupByDateInAndWcIn(List<String> workCenters, List<String> dates);

}
