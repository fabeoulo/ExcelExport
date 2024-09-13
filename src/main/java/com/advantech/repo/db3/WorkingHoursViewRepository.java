/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.repo.db3;

import com.advantech.model.db3.JpaAbstractEntity;
import com.advantech.model.db3.WorkingHoursView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh // target tables doesn't have PK field.
 */
@Repository
public interface WorkingHoursViewRepository extends JpaRepository<JpaAbstractEntity, Integer> {

    public static final String PASTDAYS
            = "SELECT \"BUDAT\" AS \"DataDates\" FROM( "
            + "SELECT *, ROW_NUMBER() OVER (ORDER BY \"BUDAT\" DESC) row_num FROM ( "
            + "SELECT DISTINCT \"BUDAT\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE \"SWERK\" LIKE 'TWM%' AND \"BUDAT\" < ?1 "
            + ") AS distinct_table "
            + ") AS rowNum_table "
            + "WHERE row_num <= 7";

    public static final String PASTWEEKS
            = "SELECT DISTINCT \"BUDAT\" AS \"DataDates\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE \"BUDAT\" >= FIRSTDAYOFWEEK(ADDWEEK(TO_LOCALDATE('yyyyMMdd', ?1), -4)) AND \"BUDAT\" < FIRSTDAYOFWEEK(TO_LOCALDATE('yyyyMMdd', ?1)) ";

    public static final String FIRSTDAYSUNDAYCLAUSE
            = "CASE WHEN GETDAYOFWEEK(TO_LOCALDATE('yyyyMMdd', ?1)) = 2 AND "
            + "ADDDAY(TO_LOCALDATE('yyyyMMdd', ?1), -1) = FIRSTDAYOFMONTH(TO_LOCALDATE('yyyyMMdd', ?1)) "
            + "THEN FIRSTDAYOFMONTH(ADDDAY(TO_LOCALDATE('yyyyMMdd', ?1), -2)) "
            + "ELSE FIRSTDAYOFMONTH(ADDDAY(TO_LOCALDATE('yyyyMMdd', ?1), -1)) "
            + "END ";

    public static final String PASTMONTH
            = "SELECT DISTINCT \"BUDAT\" AS \"DataDates\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE  \"BUDAT\" >= "
            + FIRSTDAYSUNDAYCLAUSE + " AND \"BUDAT\" < ?1 ";

    public static final String WORKHOURGROUP
            = "SELECT \"BUDAT\", SUM(ROUND(\"ISM02\"/60, 2)) AS \"Workhour\", \"SWERK\" AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "GROUP BY \"BUDAT\", \"SWERK\" ";

    public static final String WORKHOURGROUPWC
            = "SELECT \"BUDAT\", SUM(ROUND(\"ISM02\"/60, 2)) AS \"Workhour\", \"Plant\" "
            + "FROM ( "
            + "SELECT *"
            + ", CASE WHEN \"SWERK\"='TWM3' OR \"ARBPL\"='ASS-01' OR \"ARBPL\"='ES' THEN 'TWM3' "
            + "WHEN \"ARBPL\"='ASSY-A' OR \"ARBPL\"='ES-M9' THEN 'TWM9' "
            + "WHEN \"SWERK\"='TWM6' THEN 'TWM6' END AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE \"SWERK\" IN ('TWM3','TWM6') OR \"ARBPL\" IN ('ASS-01','ES','ASSY-A','ES-M9')"
            + ") AS filterTb "
            + "GROUP BY \"BUDAT\", \"Plant\" ";

    @Query(value = PASTDAYS, nativeQuery = true)
    public List<String> findPastDays(String qDate);

    @Query(value = PASTWEEKS + "AND \"SWERK\" IN ('TWM9', 'TWM3', 'TWM6') ", nativeQuery = true)
    public List<String> findPastWeeks(String qDate);

    @Query(value = PASTMONTH + "AND \"SWERK\" IN ('TWM9', 'TWM3', 'TWM6') ", nativeQuery = true)
    public List<String> findPastMonth(String qDate);

    @Query(value
            = "SELECT \"BUDAT\", SUM(ROUND(\"ISM02\"/60, 2)) AS \"Workhour\", \"SWERK\" AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE \"BUDAT\" IN ?1 AND \"SWERK\" IN ?2 "
            + "GROUP BY \"BUDAT\", \"SWERK\" ",
            nativeQuery = true)
    public List<WorkingHoursView> findGroupByDateInAndPlantIn(List<String> dates, List<String> plants);

    @Query(value
            = "SELECT \"BUDAT\", SUM(ROUND(\"ISM02\"/60, 2)) AS \"Workhour\", \"SWERK\" AS \"Plant\" "
            + "FROM rv_biprd_ztpp_zrpp89s "
            + "WHERE \"BUDAT\" IN ?1 AND \"ARBPL\" IN ?2 "
            + "GROUP BY \"BUDAT\", \"SWERK\" ",
            nativeQuery = true)
    public List<WorkingHoursView> findGroupByDateInAndWcIn(List<String> dates, List<String> workCenters);
}
