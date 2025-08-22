/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.Achieving;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.OvertimeRecord;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.ScrappedDetail;
import com.advantech.model.db1.ScrappedDetailCount;
import com.advantech.model.db1.ScrappedDetailWeekGroup;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
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
import com.advantech.model.db1.ModelMaterialDetails;
import com.advantech.model.db1.Requisition_;
import com.advantech.model.db1.WorkingHoursReport;
import com.advantech.model.db2.Items;
import com.advantech.model.db2.OrderTypes;
import com.advantech.model.db2.Orders;
import com.advantech.model.db2.Teams;
import com.advantech.model.db2.Users;
import com.advantech.repo.db2.ItemsRepository;
import com.advantech.repo.db2.OrderTypesRepository;
import com.advantech.repo.db2.OrdersRepository;
import com.advantech.repo.db2.TeamsRepository;
import com.advantech.repo.db2.UsersRepository;
import com.advantech.service.db1.RequisitionService;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import com.advantech.repo.db3.OutputValueViewRepository;
import com.advantech.model.db3.OutputValueView;
import com.advantech.model.db3.WorkingHoursView;
import com.advantech.repo.db3.WhReportRepository;
import com.advantech.repo.db3.WorkingHoursViewRepository;
import com.advantech.model.db3.WhReport;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    @Autowired
    private OutputValueViewRepository outputValueViewRepository;

    @Autowired
    private WorkingHoursViewRepository workingHoursViewRepository;

    @Autowired
    private WhReportRepository whReportRepository;

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
        List<ScrappedDetailWeekGroup> l = scrappedRepo.findAllGroupByWeek();
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testJpa2() {

        DateTime now = new DateTime();

        List<WorkingHoursReport> l = whRepo.findDailyWhReport(now.toDate());
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);

        List<WorkingHoursReport> l2 = whRepo.findWeeklyWhReport(now.toDate());
        assertTrue(!l2.isEmpty());
        HibernateObjectPrinter.print(l2);

        List<WorkingHoursReport> l3 = whRepo.findMonthlyWhReport(now.toDate());
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

    @Test
    @Transactional
    @Rollback(true)
    public void testRequisition() {
        Requisition r = requisitionRepo.getOne(63781);
        assertNotNull(r);
        System.out.println(r.getRequisitionState().getName());
//        System.out.println(r.getRequisitionType().getName());
//        System.out.println(r.getRequisitionReason().getName());
//        
//        Requisition i = requisitionRepo.findById(99).orElse(null);
//        System.out.println(i.getRequisitionState().getName());

        List<Integer> listInt = Arrays.asList(66214, 66215, 66206, 66174);
        List<Requisition> rll = requisitionRepo.findAllById(listInt);

        List<Requisition> rl = requisitionRepo.findAll((Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            root.fetch(Requisition_.USER, JoinType.LEFT);
            root.fetch(Requisition_.REQUISITION_STATE, JoinType.LEFT);
            Path<Integer> idEntryPath = root.get(Requisition_.ID);
            return cb.and(idEntryPath.in(listInt));
        });
        HibernateObjectPrinter.print(rl);

        String[] sa = rl.stream().map(l -> l.getUser().getUsername() + "-" + l.getRequisitionState().getName()).toArray(size -> new String[size]);
        String ss = String.join(",", sa);
        HibernateObjectPrinter.print(ss);
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

    private List<String> findSdEd(DateTime dt, int interval) {
        List<String> days = Lists.newArrayList();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

        days.add(fmt.print(dt.minusDays(interval)));
        days.add(fmt.print(dt.minusDays(1)));
        return days;
    }

    private List<String> findPastMonth(DateTime dt) {
        List<String> days = Lists.newArrayList();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

        int dayOfWeek = dt.getDayOfWeek();
        int dayOfMonth = dt.getDayOfMonth();
        if (dayOfMonth == 2 && dayOfWeek == 1) { // 2th-day on monday
            return findPastMonth(dt.minusDays(1));
        }

        DateTime endDate = dt.minusDays(1);
        String sd = fmt.print(endDate.dayOfMonth().withMinimumValue());
        String ed = fmt.print(endDate);

        days.add(sd);
        days.add(ed);
        return days;
    }

//    @Test
    @Transactional
    public void testWhReport() {
        List<String> workCentersM3 = Lists.newArrayList("ASS-01", "ES");
        List<String> plantsM3 = Lists.newArrayList("TWM3", "TWM6");
        List<String> productionTypeM3 = Lists.newArrayList("");

        List<String> pastDays3 = findSdEd(new DateTime(), 7);
        List<WhReport> wc3 = whReportRepository.findDailyWhReportWc(pastDays3.get(0), pastDays3.get(1));
        HibernateObjectPrinter.print(wc3);
//
        List<String> pastDays23 = findSdEd(new DateTime(2024, 12, 9, 0, 0), 28);
        List<WhReport> wc23 = whReportRepository.findWeeklyWhReportWc(pastDays23.get(0), pastDays23.get(1));
        HibernateObjectPrinter.print(wc23);
//
        List<String> pastDays33test = findPastMonth(new DateTime(2025, 2, 28, 0, 0));
        List<String> pastDays33 = findPastMonth(new DateTime(2024, 12, 2, 0, 0));
        List<WhReport> wc33 = whReportRepository.findMonthlyWhReportWc(pastDays33.get(0), pastDays33.get(1));
        HibernateObjectPrinter.print(wc33);
//        
//        List<String> plants = Lists.newArrayList("TWM3", "TWM6");
//        
//        List<WhReport> r = whReportRepository.findDailyWhReport("20240520", plants);
//        assertTrue(r.size() > 0);
//        HibernateObjectPrinter.print(r.get(0));
//
//        List<WhReport> r1 = whReportRepository.findWeeklyWhReport("20240520", plants);
//        assertTrue(r1.size() > 0);
//        HibernateObjectPrinter.print(r1.get(0));
//
//        List<WhReport> r2 = whReportRepository.findMonthlyWhReport("20240520", plants);
//        assertTrue(r2.size() > 0);
//        HibernateObjectPrinter.print(r2.get(0));
//        WhReport w1 = r2.get(0);
//        BigDecimal dd = wh.getWorktimeEstimated();
//        wh.setWorktimeEstimated(new BigDecimal(1));
//         dd = wh.getWorktimeEstimated();
    }

//    @Test
    @Transactional
    public void testWorkingHoursView() {
        List<String> plants = Lists.newArrayList("TWM3");
        List<String> dates = Lists.newArrayList("20240520");
        List<String> workCentersM3 = Lists.newArrayList("ASS-01", "ES");
        List<String> workCentersM6 = Lists.newArrayList("LCD_ENHS", "LCD_ES");

//        List<String> days = workingHoursViewRepository.findPastDays("20240520");
//        assertTrue(days.size() > 0);
//        HibernateObjectPrinter.print(days);
//        
//        List<WorkingHoursView> r = workingHoursViewRepository.findGroupByDateInAndPlantIn(days, plants);
//        assertTrue(r.size() > 0);
//        HibernateObjectPrinter.print(r);
//
        List<WorkingHoursView> r2 = workingHoursViewRepository.findGroupByDateInAndWcIn(dates, workCentersM3);
        assertTrue(r2.size() > 0);
        HibernateObjectPrinter.print(r2);

//        HibernateObjectPrinter.print(r.get(1));
    }

//    @Test
    @Transactional
    public void testOutputValueViewRepository() {
        List<String> plants = Lists.newArrayList("TWM3");
        List<String> dates = Lists.newArrayList("20240520");
        List<String> workCentersM3 = Lists.newArrayList("ASS-01", "ES");
        List<String> workCentersM6 = Lists.newArrayList("LCD_ENHS", "LCD_ES");

        List<OutputValueView> r = outputValueViewRepository.findGroupByDateInAndPlantIn(dates, plants);
        assertTrue(r.size() > 0);
        HibernateObjectPrinter.print(r);

//        HibernateObjectPrinter.print(r.get(1));
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

//    @Test
    @Transactional
    @Rollback(true)
    public void testAchievingRepository() {
        Achieving pojo = achievingRepository.getOne(1);
        assertNotNull(pojo);
        assertEquals("TWM2", pojo.getFactory());
    }

    @Autowired
    private RequisitionRepository requisitionRepository;

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testFindAllByPoAndMat() {
//        List< Requisition> l = requisitionRepository.findAllByPoInAndMaterialNumberIn(Lists.newArrayList("TPO000146ZA"), Lists.newArrayList("2000023012-11"));
//        List< Requisition> l = requisitionRepository.findAllByCreateDateGreaterThanAndRequisitionState_IdAndFloor_IdIn(DateTime.now().plusHours(-8).toDate(), 5, Lists.newArrayList(9));
        List< Requisition> l = requisitionRepository.findAllByReturnDateBetweenAndRequisitionType_IdInAndFloor_IdIn(
                DateTime.now().plusDays(-1).toDate(),
                DateTime.now().toDate(),
                Lists.newArrayList(2),
                Lists.newArrayList(9)
        );

        assertTrue(!l.isEmpty());

        HibernateObjectPrinter.print(l);
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testPoMaterialDetails() {
        List<ModelMaterialDetails> l = requisitionRepository.findModelMaterialDetails("FII1282ZA");

        assertEquals(12, l.size());

        HibernateObjectPrinter.print(l.get(0));
    }

    @Autowired
    private OrdersRepository ordersRepo;

    @Autowired
    private OrderTypesRepository orderTypesRepo;

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private TeamsRepository teamsRepo;

    @Test
    @Transactional("tx2")
    @Rollback(false)
    public void testOrder() {
        System.out.println("testOrder");

        OrderTypes od = orderTypesRepo.getOne(1);
        Users u = usersRepo.getOne("test2");
        Teams t = teamsRepo.getOne(1);

        Orders o = new Orders(od, t, u, 1, "1", new DateTime().toDate(), null, new DateTime().toDate(), null);
        o.setRequisionId(12345678);

        ordersRepo.save(o);

        List<Integer> li = Lists.newArrayList(o.getId());
        ordersRepo.updateTimeStampToZeroByIdIn(li);

        System.out.println("testOrder complete");
    }

}
