/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionState;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.service.db1.ExceptionService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.trigger.RequisitionStateChangeTrigger;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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

        service.testTransaction();
    }

    @Autowired
    private RequisitionStateChangeTrigger trigger;

    @Test
    public void test() {
        System.out.println("Requisition.isPresent= " + rservice.findById(62288).isPresent());

        //Requisition r = rservice.findById(62288).get();
        Requisition r = rservice.findByIdWithLazy(63781);

        r = rservice.findById(63781).orElse(null);
        final int[] checkUserList = {742, 753, 895, 1024, 1025, 36};
        final int[] checkStateList = {2, 5};

        trigger.checkRepair(newArrayList(r));
        List<Requisition> checkedList = newArrayList(r).stream().filter(e -> {
            int userId = e.getUser().getId();
            int rsId = e.getRequisitionState().getId();
            return Arrays.stream(checkUserList).anyMatch(i -> i == userId);
        }).collect(Collectors.toList());

    }

    @Autowired
    private UserNotificationService notificationService;

    @Test
    public void testUserNotificationService() {
        UserNotification un = notificationService.findByName("requisition_state_change_target");
        List<User> ls = userService.findByUserNotifications(un);
        List<Integer> li = ls.stream().map(User::getId).collect(Collectors.toList());

        HibernateObjectPrinter.print(ls);
    }
}
