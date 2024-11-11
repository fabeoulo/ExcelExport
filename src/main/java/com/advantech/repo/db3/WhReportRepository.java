/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.advantech.repo.db3;

import com.advantech.model.db3.JpaAbstractEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.advantech.model.db3.WhReport;

/**
 *
 * @author Justin.Yeh // target tables doesn't have PK field.
 */
@Repository
public interface WhReportRepository extends JpaRepository<JpaAbstractEntity, Integer> {

    public static final String WHGROUP = WorkingHoursViewRepository.WORKHOURGROUP;
    public static final String OVGROUP = OutputValueViewRepository.OUTPUTVALUEGROUP;
    public static final String PASTDAYS = WorkingHoursViewRepository.PASTDAYS;
    public static final String PASTWEEKS = WorkingHoursViewRepository.PASTWEEKS;
    public static final String PASTMONTH = WorkingHoursViewRepository.PASTMONTH;

    public static final String JOINVIEWS
            = "SELECT GETWEEK(TO_LOCALDATE('yyyyMMdd', \"BUDAT\")) AS \"WK\", \"BUDAT\", "
            + "\"Quantity\", \"Workhour\", \"StandardCost\", \"Plant\" , \"SWERK\" "
            + "FROM(" + WHGROUP + ") AS wh JOIN (" + OVGROUP + ") AS ov "
            + "ON wh.\"BUDAT\" = ov.\"ERDAT\" AND wh.\"Plant\" = ov.\"SWERK\" ";

    public static final String JOINWHERE
            = "WHERE wh.\"Plant\" IN (?2) ";

    public static final String SELECTCLAUSE
            = "SELECT SUM(\"Quantity\") AS \"quantity\", SUM(\"Workhour\") AS \"sapWorktime\""
            + ", SUM(\"StandardCost\") AS \"sapOutputValue\", \"Plant\" AS \"plant\" ";

    public static final String ORDERSELECT
            = "SELECT * FROM( ";

    public static final String ORDERCLAUSE
            = ")resultTb ORDER BY \"plant\", \"dateField\" ";

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"BUDAT\" AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWS + "JOIN (" + PASTDAYS + ") AS dt ON wh.\"BUDAT\" = dt.\"TargetDates\" "
            + JOINWHERE
            + ") subTb "
            + "GROUP BY \"BUDAT\", \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findDailyWhReport(String date, List<String> plants);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"WK\" AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWS + "JOIN (" + PASTWEEKS + ") AS dt ON wh.\"BUDAT\" = dt.\"TargetDates\" "
            + JOINWHERE
            + ") subTb "
            + "GROUP BY \"WK\", \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findWeeklyWhReport(String date, List<String> plants);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", SUBSTRING(\"BUDAT\",0,6) AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWS + "JOIN (" + PASTMONTH + ") AS dt ON wh.\"BUDAT\" = dt.\"TargetDates\" "
            + JOINWHERE
            + ") subTb "
            + "GROUP BY SUBSTRING(\"BUDAT\", 0, 6), \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findMonthlyWhReport(String date, List<String> plants);

// <editor-fold desc="Query by work center. At least either Workhour or StandardCost.">
    public static final String WHGROUPWC = WorkingHoursViewRepository.WORKHOURGROUPWC;
    public static final String OVGROUPWC = OutputValueViewRepository.OUTPUTVALUEGROUPWC;

    public static final String FULLJOINVIEWSWC
            = " SELECT *, CASE WHEN \"StandardCost\" IS NOT NULL THEN \"ERDAT\" ELSE \"BUDAT\" END AS \"JoinDates\" , "
            + "CASE WHEN \"StandardCost\" IS NOT NULL THEN \"SWERK1\" ELSE \"Plant\" END AS \"JoinPlant\"  "
            + "FROM(" + WHGROUPWC + ") AS wh FULL JOIN (" + OVGROUPWC + ") AS ov "
            + "ON wh.\"BUDAT\" = ov.\"ERDAT\" AND wh.\"Plant\" = ov.\"SWERK1\" ";

    public static final String FULLJOINSELECT
            = "SELECT GETWEEK(TO_LOCALDATE('yyyyMMdd', \"JoinDates\")) AS \"WK\", \"JoinDates\" AS \"BUDAT\", "
            + "\"Quantity\", \"Workhour\", \"StandardCost\", \"JoinPlant\" AS \"Plant\" "
            + "FROM(" + FULLJOINVIEWSWC + ") ";

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"BUDAT\" AS \"dateField\" "
            + "FROM ( "
            + FULLJOINSELECT + "AS fj WHERE fj.\"JoinDates\" BETWEEN ?1 AND ?2 "
            + ") subTb "
            + "GROUP BY \"dateField\", \"plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findDailyWhReportWc(String sd, String ed);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"WK\" AS \"dateField\" "
            + "FROM ( "
            + FULLJOINSELECT + "AS fj WHERE fj.\"JoinDates\" BETWEEN ?1 AND ?2 "
            + ") subTb "
            + "GROUP BY \"dateField\", \"plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findWeeklyWhReportWc(String sd, String ed);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", SUBSTRING(\"BUDAT\",0,6) AS \"dateField\" "
            + "FROM ( "
            + FULLJOINSELECT + "AS fj WHERE fj.\"JoinDates\" BETWEEN ?1 AND ?2 "
            + ") subTb "
            + "GROUP BY \"dateField\", \"plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findMonthlyWhReportWc(String sd, String ed);
// </editor-fold>

}
