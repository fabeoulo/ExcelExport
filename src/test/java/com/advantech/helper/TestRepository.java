/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.Achieving;
import com.advantech.model.Floor;
import com.advantech.model.OvertimeRecord;
import com.advantech.model.Requisition;
import com.advantech.model.ScrappedDetail;
import com.advantech.model.ScrappedDetailCount;
import com.advantech.model.ScrappedDetailWeekGroup;
import com.advantech.model.User;
import com.advantech.model.UserNotification;
import com.advantech.repo.db1.AchievingRepository;
import com.advantech.repo.db1.FloorRepository;
import com.advantech.repo.db1.OvertimeRecordRepository;
import com.advantech.repo.db1.RequisitionRepository;
import com.advantech.repo.db1.ScrappedDetailRepository;
import com.advantech.repo.db1.UserNotificationRepository;
import com.advantech.repo.db1.UserRepository;
import com.advantech.repo.db1.WorkingHoursRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRepository {

    @Autowired
    private ScrappedDetailRepository scrappedRepo;

    @Autowired
    private FloorRepository floorRepo;

    @Autowired
    private ExcelDataTransformer tr;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserNotificationRepository notificationRepo;

//    @Test
    @Transactional
    @Rollback(false)
    public void testScrappedDetailRepo() throws Exception {

        List<Floor> floors = floorRepo.findAll();

        Floor five = floors.stream().filter(f -> f.getName().equals("5F")).findFirst().get();
        assertNotNull(five);

        Floor six = floors.stream().filter(f -> f.getName().equals("6F")).findFirst().get();
        assertNotNull(six);

        List<ScrappedDetail> excelFloorFiveData = tr.getFloorFiveExcelData();
        List<ScrappedDetail> excelFloorSixData = tr.getFloorSixExcelData();

        assertNotEquals(0, excelFloorFiveData.size());
        assertNotEquals(0, excelFloorSixData.size());

        List<ScrappedDetail> floorFiveDataInDb = scrappedRepo.findByFloor(five);
        List<ScrappedDetail> floorSixDataInDb = scrappedRepo.findByFloor(six);

        excelFloorFiveData.forEach(s -> s.setFloor(five));
        excelFloorSixData.forEach(s -> s.setFloor(six));

        List<ScrappedDetail> newData1 = (List<ScrappedDetail>) CollectionUtils.subtract(excelFloorFiveData, floorFiveDataInDb);
        List<ScrappedDetail> newData2 = (List<ScrappedDetail>) CollectionUtils.subtract(excelFloorSixData, floorSixDataInDb);

        System.out.printf("Saving floor five data: %d, floor six data: %d \n", newData1.size(), newData2.size());

        scrappedRepo.saveAll(newData1);
        scrappedRepo.saveAll(newData2);

        //注意最後check兩邊size是否一致, 不一致可能是db有多的資料(使用者delete過), 必須做刪除
        floorFiveDataInDb = scrappedRepo.findByFloor(five);
        floorSixDataInDb = scrappedRepo.findByFloor(six);

        if (floorFiveDataInDb.size() + floorSixDataInDb.size() != excelFloorFiveData.size() + excelFloorSixData.size()) {
            System.out.println("Detect different data, begin remove...");

            List<ScrappedDetail> delData1 = (List<ScrappedDetail>) CollectionUtils.subtract(floorFiveDataInDb, excelFloorFiveData);
            List<ScrappedDetail> delData2 = (List<ScrappedDetail>) CollectionUtils.subtract(floorSixDataInDb, excelFloorSixData);

            System.out.printf("Remove floor five data: %d, floor six data: %d \n", delData1.size(), delData2.size());

            scrappedRepo.deleteAll(delData1);
            scrappedRepo.deleteAll(delData2);
        }

    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testRepo() {
        ScrappedDetail d1 = scrappedRepo.findById(1484).get();

        HibernateObjectPrinter.print(d1);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testSqlView() {
        List<ScrappedDetailWeekGroup> l = scrappedRepo.findAllGroupByWeek();

        HibernateObjectPrinter.print(l);

    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testPaginate() {
        PageRequest req = new PageRequest(1, 10, Sort.Direction.DESC, "createDate", "po");
        List<ScrappedDetail> l = scrappedRepo.findAll(req).getContent();

        HibernateObjectPrinter.print(l);

    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testUserNotification() {
        UserNotification n = notificationRepo.findById(2).get();

        List<User> l = userRepo.findByUserNotifications(n);

        assertEquals(5, l.size());

    }

//    @Test
    @Transactional
    @Rollback(false)
    public void testFixScrappedDetailField() {
        List<ScrappedDetail> l = scrappedRepo.findAll();

        for (ScrappedDetail s : l) {
            try {
                if (s.getModelName().contains(".")) {
                    s.setModelName(Long.toString(new BigDecimal(s.getModelName()).longValue()));
                }
                if (s.getMaterialNumber().contains(".")) {
                    s.setMaterialNumber(Long.toString(new BigDecimal(s.getMaterialNumber()).longValue()));
                }
            } catch (Exception e) {
                System.out.println(e.getCause());
            }
        }

        scrappedRepo.saveAll(l);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testSortAndSearch() {
        //Get price > 500 & current year

        DateTime d = new DateTime("2018-01-01");

        List<ScrappedDetail> l = scrappedRepo.findByPriceGreaterThanAndCreateDateGreaterThan(500, d.toDate());

        assertEquals(89, l.size());

        Map<String, Map<Integer, List<ScrappedDetail>>> map = l.stream()
                .collect(Collectors.groupingBy(ScrappedDetail::getMaterialNumber,
                        Collectors.groupingBy(ScrappedDetail::getPrice)));

        Map<String, Map<Integer, List<ScrappedDetail>>> collect = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .entrySet()
                                .stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(
                                        Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                (a, b) -> a,
                                                LinkedHashMap::new
                                        )
                                ),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        HibernateObjectPrinter.print(collect);
    }

    @Autowired
    public WorkingHoursRepository whRepo;

//    @Test
    @Transactional
    @Rollback(true)
    public void testJpa() {
        List l = scrappedRepo.findAllGroupByWeek();
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testJpa2() {

        DateTime now = new DateTime();

        List l = whRepo.findDailyWhReport(now.toDate());
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);

        List l2 = whRepo.findWeeklyWhReport(now.toDate());
        assertTrue(!l2.isEmpty());
        HibernateObjectPrinter.print(l2);

        List l3 = whRepo.findMonthlyWhReport(now.toDate());
        assertTrue(!l3.isEmpty());
        HibernateObjectPrinter.print(l3);

    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testJpaMix() {

    }

    @Autowired
    private RequisitionRepository requisitionRepo;

//    @Test
    @Transactional
    @Rollback(true)
    public void testRequisition() {
        Requisition r = requisitionRepo.getOne(32);
        assertNotNull(r);

        System.out.println(r.getRequisitionState().getName());
        System.out.println(r.getRequisitionType().getName());
        System.out.println(r.getRequisitionReason().getName());
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testScrappedDetailCount() {
        DateTime sD = new DateTime("2019-04-22");
        DateTime eD = new DateTime("2019-04-26");
        DateTime startDateOfYear = new DateTime("2019-01-01");
        List<ScrappedDetailCount> r = scrappedRepo.findUserScrappedDetailCount(sD.toDate(), eD.toDate(), startDateOfYear.toDate());

        assertTrue(r.size() > 0);

        HibernateObjectPrinter.print(r.get(0));
        HibernateObjectPrinter.print(r.get(1));
    }

    @Autowired
    private OvertimeRecordRepository overtimeRecordRepository;

//    @Test
    @Transactional
    @Rollback(true)
    public void testOvertimeRecordRepository() {
        DateTime sD = new DateTime("2019-06-01");
        DateTime eD = new DateTime("2019-06-20");
        
//        List<OvertimeRecordWeekly> l2 = overtimeRecordRepository.findWeeklyOvertimeRecord(sD.toDate(), eD.toDate());
        List<OvertimeRecord> l2 = overtimeRecordRepository.findOvertimeRecord(sD.toDate(), eD.toDate());
//        List<Map> l3 = overtimeRecordRepository.findOvertimeRecord2(sD.toDate(), eD.toDate());
//
//        List<OvertimeRecord> floorFiveTopN = l2.stream()
//                .sorted((OvertimeRecord o1, OvertimeRecord o2) -> new BigDecimal(o1.getSum()).compareTo(new BigDecimal(o2.getSum())))
//                .filter(o -> o.getSitefloor().equals("5") && Objects.equals(o.getWeekOfMonth(), eD.getWeekOfWeekyear()))
//                .limit(5)
//                .collect(toList());
//
//        List<OvertimeRecord> floorSixTopN = l2.stream()
//                .sorted((OvertimeRecord o1, OvertimeRecord o2) -> new BigDecimal(o1.getSum()).compareTo(new BigDecimal(o2.getSum())))
//                .filter(o -> o.getSitefloor().equals("6") && Objects.equals(o.getWeekOfMonth(), eD.getWeekOfWeekyear()))
//                .limit(5)
//                .collect(toList());
        
        HibernateObjectPrinter.print(l2);
//        HibernateObjectPrinter.print(l3.get(1));
//        HibernateObjectPrinter.print(floorFiveTopN);
//        HibernateObjectPrinter.print(floorSixTopN);
    }
    
    @Autowired
    private AchievingRepository achievingRepository;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testAchievingRepository(){
        Achieving pojo = achievingRepository.getOne(1);
        assertNotNull(pojo);
        assertEquals("TWM2", pojo.getFactory());
    }

}
