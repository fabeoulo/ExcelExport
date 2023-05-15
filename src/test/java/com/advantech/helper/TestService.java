/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.ModelMaterialDetails;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.service.db1.ExceptionService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.trigger.RequisitionStateChangeTrigger;
import com.advantech.webservice.Factory;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void test1() {

//        service.testTransaction();
//        Factory f = Factory.getEnum("PD03");
//        HibernateObjectPrinter.print(f);
    }

//    @Autowired
//    private RequisitionStateChangeTrigger trigger;
//    
    @Test
    @Transactional
    @Rollback(true)
    public void testTrigger() {
//        System.out.println("Requisition.isPresent= " + rservice.findById(88).isPresent());

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
//    
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
}
