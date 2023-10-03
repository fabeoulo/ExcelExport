/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.ModelMaterialDetails;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.Requisition_;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.model.db2.Items;
import com.advantech.model.db2.MaterialMrp;
import com.advantech.model.db2.OrderResponse;
import com.advantech.model.db2.OrderResponseOwners;
import com.advantech.model.db2.Orders;
import com.advantech.sap.SapService;
import com.advantech.sap.SapMrpTbl;
import com.advantech.service.db1.ExceptionService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.service.db2.ItemsService;
import com.advantech.service.db2.MaterialMrpService;
import com.advantech.service.db2.OrderResponseOwnersService;
import com.advantech.service.db2.OrderResponseService;
import com.advantech.service.db2.OrdersService;
import com.advantech.trigger.RequisitionStateChangeTrigger;
import com.advantech.webservice.Factory;
import static com.google.common.base.Preconditions.checkArgument;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
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
public class TestService {

    @Autowired
    private ExceptionService service;

    @Autowired
    private UserService userService;

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private SapService sapService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private OrderResponseOwnersService orderResponseOwnersService;

    @Autowired
    private OrderResponseService orderResponseService;

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testFindAll() throws Exception {
        List<OrderResponseOwners> lro = orderResponseOwnersService.findAll();
        List<OrderResponse> lr = orderResponseService.findAll();
    }

//    @Test
    public void test1() {

//        service.testTransaction();
//        Factory f = Factory.getEnum("PD03");
//        HibernateObjectPrinter.print(f);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testMatchMrp() throws Exception {
        List<Orders> orders = ordersService.findAllOpenWithoutReply();
        List<String> plants = orders.stream().map(o -> o.getTeams().getPlant()).collect(Collectors.toList());
        List<String> materials = orders.stream()
                .map(o -> {
                    Items i = o.getItemses().stream().findFirst().get();
                    return i.getLabel3();
                }).collect(Collectors.toList());

        Map<String, String> map = materialMrpService.getMrpMap(plants, materials);
        List<Orders> updateL = orders.stream()
                .filter(o -> {
                    Items i = o.getItemses().stream().findFirst().get();
                    String key = o.getTeams().getPlant() + i.getLabel3();
                    if (map.containsKey(key)) {
                        o.setOwnerId(map.get(key));
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

        HibernateObjectPrinter.print(updateL);
        ordersService.saveAll(updateL);
    }

    @Autowired
    private MaterialMrpService materialMrpService;

    @Test
    @Transactional
    @Rollback(false)
    public void testMaterialMrpService() throws Exception {
        List<Items> itemOrders = itemsService.findAllWithPlant();
        List<Items> subL = itemOrders.subList(0, 1000);

        List<SapMrpTbl> tblin = subL.stream().map(i -> {
            return new SapMrpTbl(i.getLabel3(), i.getOrders().getTeams().getPlant());
        }).collect(Collectors.toList());

        Map<String, String> mrpMap = sapService.getMrpCodeByTblin(tblin);
        List<MaterialMrp> newMms = new ArrayList<>();
        mrpMap.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                String[] keys = k.split(",");
                MaterialMrp mm = new MaterialMrp(keys[1], keys[0], v, new DateTime().toDate());
                newMms.add(mm);
            }
        });

        List<MaterialMrp> dbMms = materialMrpService.findAll();
        List<MaterialMrp> mergeList = new ArrayList<>();
        for (MaterialMrp newMm : newMms) {
            MaterialMrp mm = findMaterialInDb(dbMms, newMm);
            if (mm == null) {
                mergeList.add(newMm);
            } else if (!mm.getMrpCode().equals(newMm.getMrpCode())) {
                mm.setMrpCode(newMm.getMrpCode());
                mm.setUpdateTime(newMm.getUpdateTime());
                mergeList.add(mm);
            }
        }
        materialMrpService.saveAll(mergeList);

        List<Items> syncItems = subL.stream()
                .map(i -> {
                    String k = i.getLabel3() + "," + i.getOrders().getTeams().getPlant();
                    String code = mrpMap.getOrDefault(k, "");
                    i.setMrpCode(code);
                    i.setMrpSync(true);
                    return i;
                })
                .collect(Collectors.toList());
        itemsService.saveAll(syncItems);
    }

    private MaterialMrp findMaterialInDb(List<MaterialMrp> list, MaterialMrp material) {
        return list.stream()
                .filter(
                        m -> m.getPlant().equals(material.getPlant())
                        && m.getMatName().equals(material.getMatName())
                )
                .findFirst()
                .orElse(null);
    }

    private Date sD, eD;

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testToPMC() {
//        setDatetime(sD, eD);

//        List<Requisition> rl = rservice.findAllByHalfdayWithUserAndState();
//        return;
    }

    private void setDate() {
        DateTime dt = new DateTime();
        DateTime sdt;
        DateTime edt;
        if (dt.getHourOfDay() < 17) {
            sdt = dt.minusDays(1).withTime(17, 0, 0, 1);
            edt = dt.withTime(12, 0, 0, 0);
        } else {
            sdt = dt.withTime(12, 0, 0, 1);
            edt = dt.withTime(17, 0, 0, 0);
        }
        sD = sdt.toDate();
        eD = edt.toDate();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDataTablesOutput() {
        setDate();

        DataTablesInput input = new DataTablesInput();
        DataTablesOutput<Requisition> dto
                = //rservice.findAll(input);
                rservice.findAll(input, (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
                    Path<Date> dateEntryPath = root.get(Requisition_.createDate);
//                if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER")) {
                    return cb.between(dateEntryPath, sD, eD);
//                } else {
//                    Join<Requisition, User> userJoin = root.join(Requisition_.user, JoinType.INNER);
//                    return cq.where(cb.and(cb.between(dateEntryPath, sD, eD), cb.equal(userJoin.get(User_.FLOOR), floor))).getRestriction();
//                }
                });
        List<Requisition> filteredData = dto.getData().stream()
                .filter(r -> r.getUser().getId() == 53)
                .collect(Collectors.toList());

        HibernateObjectPrinter.print(filteredData);
        dto.setData(filteredData);
    }

//    @Autowired
//    private RequisitionStateChangeTrigger trigger;

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testTrigger() {
//        System.out.println("Requisition.isPresent= " + rservice.findById(88).isPresent());

//        final int[] checkUserList = {742, 753, 895, 1024, 1025, 36};
//        final int[] checkStateList = {2, 5};
//
//        trigger.checkRepair(rl);
//        List<Requisition> checkedList = rl.stream().filter(e -> {
//            int userId = e.getUser().getId();
//            int rsId = e.getRequisitionState().getId();
//            return Arrays.stream(checkUserList).anyMatch(i -> i == userId);
//        }).collect(Collectors.toList());
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void testLackStock() throws Exception {
        Integer teamId = 2;// 1 for test
        List<Orders> lackOrders = ordersService.findAllLackWithUserItem(teamId);//order by id
//        if (lackOrders.isEmpty()) {
//            return;
//        }

        List<Requisition> lackReq = getReqFromLack(lackOrders, new ArrayList<>());
        Map<String, BigDecimal> stockMap = sapService.getStockMapWithGoodLgort(lackReq);

        List<Orders> checkedOrders = lackOrders.stream().filter(o -> {
            Items firstItem = o.getItemses().stream().findFirst().orElse(null);
            if (firstItem != null && stockMap.containsKey(firstItem.getLabel3())) {
                String key = firstItem.getLabel3();
                BigDecimal stock = stockMap.get(key);
                BigDecimal require = new BigDecimal(o.getNumber());
                BigDecimal restStock = stock.subtract(require);
                if (restStock.compareTo(new BigDecimal(0)) == 1) {
                    stockMap.put(key, restStock);
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

//        checkedOrders = new ArrayList<>();
        lackReq = getReqFromLack(checkedOrders, lackReq);
        List<String> lackMails = checkedOrders.stream().map(l -> l.getUsers().getMail())
                .filter(e -> e != null && !e.trim().equals("")).distinct().collect(Collectors.toList());
        List<String> reqMails = lackReq.stream().map(r -> r.getUser().getEmail())
                .filter(e -> e != null && !e.trim().equals("")).distinct().collect(Collectors.toList());
        lackMails.addAll(reqMails);
        String[] findEMailFromLack = lackMails.stream().toArray(size -> new String[size]);
        HibernateObjectPrinter.print(findEMailFromLack);
    }

    private List<Requisition> getReqFromLack(List<Orders> lackOrders, List<Requisition> lackReq) {
        if (lackOrders.isEmpty()) {
            return Arrays.asList();
        }
        List<Integer> listInt = lackOrders.stream().map(l -> l.getRequisionId()).collect(Collectors.toList());

        return lackReq.isEmpty() ? rservice.findAllByIdWithUserAndState(listInt)
                : lackReq.stream().filter(r -> listInt.contains(r.getId())).collect(Collectors.toList());
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void test() {
//        System.out.println("Requisition.isPresent= " + rservice.findById(62288).isPresent());

        List<Integer> listInt = Arrays.asList(88, 90);
        List<Requisition> rl = rservice.findAllByIdWithUserAndState(listInt);
        HibernateObjectPrinter.print(rl);

        List<Requisition> rl3 = rl.stream().filter(i -> {
            Boolean boo = i.getId() == 66124;
            if (!boo) {
                i.setRemark(i.getRemark() + "NoStock");
            }
            return boo;
        }).collect(Collectors.toList());
        List<Requisition> rl4 = rl.stream().filter(i -> !rl3.contains(i)).collect(Collectors.toList());

        String[] stateName = rl.stream().map(l -> l.getUser().getUsername() + "-" + l.getRequisitionState().getName()).toArray(size -> new String[size]);
        String ss = String.join(",", stateName);
        HibernateObjectPrinter.print(ss);
//        HibernateObjectPrinter.print(rl);
//        
//        String[] stateName = rl.stream().map(l -> l.getUser().getUsername()+"-"+l.getRequisitionState().getName()).toArray(size -> new String[size]);
//        String ss = String.join(",", stateName);
//        HibernateObjectPrinter.print(ss);

        final int[] checkUserList = {742, 753, 895, 1024, 1025, 36};
        final int[] checkStateList = {2, 5};

//        trigger.checkRepair(rl);
        List<Requisition> checkedList = rl.stream().filter(e -> {
            int userId = e.getUser().getId();
            int rsId = e.getRequisitionState().getId();
            return Arrays.stream(checkUserList).anyMatch(i -> i == userId);
        }).collect(Collectors.toList());
    }	
	
//    @Autowired
//    private UserNotificationService notificationService;
//    
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testUserNotificationService() {
//        UserNotification un = notificationService.findByName("requisition_state_change_target");
//        List<User> ls = userService.findByUserNotifications(un);
//        List<Integer> li = ls.stream().map(User::getId).collect(Collectors.toList());
//        
//        HibernateObjectPrinter.print(ls);
//    }

//    @Test
//    @Transactional
//    @Rollback(false)
    public void testUserNotificationService() {
        userService.saveUserWithNameByProc("A-9095", "Asryder.Wang@advantech.com.tw", "王彥喆");
    }
}
