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
            + JOINVIEWS + "JOIN (" + PASTDAYS + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
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
            + JOINVIEWS + "JOIN (" + PASTWEEKS + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
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
            + JOINVIEWS + "JOIN (" + PASTMONTH + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
            + JOINWHERE
            + ") subTb "
            + "GROUP BY SUBSTRING(\"BUDAT\", 0, 6), \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findMonthlyWhReport(String date, List<String> plants);

    /**
     *
     * query by work center.
     */
    public static final String WHGROUPWC = WorkingHoursViewRepository.WORKHOURGROUPWC;
    public static final String OVGROUPWC = OutputValueViewRepository.OUTPUTVALUEGROUPWC;

    public static final String JOINVIEWSWC
            = "SELECT GETWEEK(TO_LOCALDATE('yyyyMMdd', \"BUDAT\")) AS \"WK\", \"BUDAT\", "
            + "\"Quantity\", \"Workhour\", \"StandardCost\", \"Plant\" , \"SWERK1\" "
            + "FROM(" + WHGROUPWC + ") AS wh JOIN (" + OVGROUPWC + ") AS ov "
            + "ON wh.\"BUDAT\" = ov.\"ERDAT\" AND wh.\"Plant\" = ov.\"SWERK1\" ";

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"BUDAT\" AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWSWC + "JOIN (" + PASTDAYS + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
            + ") subTb "
            + "GROUP BY \"BUDAT\", \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findDailyWhReportWc(String date);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", \"WK\" AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWSWC + "JOIN (" + PASTWEEKS + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
            + ") subTb "
            + "GROUP BY \"WK\", \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findWeeklyWhReportWc(String date);

    @Query(value
            = ORDERSELECT
            + SELECTCLAUSE + ", SUBSTRING(\"BUDAT\",0,6) AS \"dateField\" "
            + "FROM ( "
            + JOINVIEWSWC + "JOIN (" + PASTMONTH + ") AS dt ON wh.\"BUDAT\" = dt.\"DataDates\" "
            + ") subTb "
            + "GROUP BY SUBSTRING(\"BUDAT\", 0, 6), \"Plant\" "
            + ORDERCLAUSE,
            nativeQuery = true)
    public List<WhReport> findMonthlyWhReportWc(String date);

}
