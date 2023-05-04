/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.Requisition_;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.repo.db1.RequisitionRepository;
import com.advantech.service.db1.ExceptionService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.trigger.RequisitionStateChangeTrigger;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void test1() {

        service.testTransaction();
    }

    @Autowired
    private RequisitionStateChangeTrigger trigger;

    @Test
    @Transactional
    public void test() {
        System.out.println("Requisition.isPresent= " + rservice.findById(62288).isPresent());

        List<Integer> listInt = Arrays.asList(66124, 66125);
        List<Requisition> rl = rservice.findAllByIdWithUserAndState(listInt);
        HibernateObjectPrinter.print(rl);
        
        String[] stateName = rl.stream().map(l -> l.getUser().getUsername()+"-"+l.getRequisitionState().getName()).toArray(size -> new String[size]);
        String ss = String.join(",", stateName);
        HibernateObjectPrinter.print(ss);
//        HibernateObjectPrinter.print(rl);
//        Map<User, String> mapP0 = rl.stream()
//                .collect(Collectors.groupingBy(Requisition::getUser,
//                        Collectors.collectingAndThen(Collectors.toList(),
//                                list -> list.stream()
//                                        .map(Requisition::getPo)
//                                        .findFirst()
//                                        .orElse(""))));
//        HibernateObjectPrinter.print(mapP0.values());
//        String ss = String.join(",", mapP0.values());
//        HibernateObjectPrinter.print(ss);


        final int[] checkUserList = {742, 753, 895, 1024, 1025, 36};
        final int[] checkStateList = {2, 5};

        trigger.checkRepair(rl);
        List<Requisition> checkedList = rl.stream().filter(e -> {
            int userId = e.getUser().getId();
            int rsId = e.getRequisitionState().getId();
            return Arrays.stream(checkUserList).anyMatch(i -> i == userId);
        }).collect(Collectors.toList());

    }

    @Autowired
    private UserNotificationService notificationService;

    @Test
    @Transactional
    public void testUserNotificationService() {
        UserNotification un = notificationService.findByName("requisition_state_change_target");
        List<User> ls = userService.findByUserNotifications(un);
        List<Integer> li = ls.stream().map(User::getId).collect(Collectors.toList());

        HibernateObjectPrinter.print(ls);
    }
}
