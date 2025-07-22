/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.UserAgent;
import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserAgentService;
import com.advantech.webservice.WareHourseService;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WareHouseAgent extends JobBase {

    private static final Logger logger = LoggerFactory.getLogger(WareHouseAgent.class);

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private WareHourseService wareHourseService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserAgentService userAgentService;

    private final DateTime today = new DateTime().withTime(LocalTime.MIDNIGHT);
    private final int rState = 4;
    private final List<Integer> floorIds = newArrayList(9);

    // prevent lazyjoin nosession issue.
    @Transactional
    public void execute() {
        if (!super.isServer()) {
            return;
        }

        List<UserAgent> ul = userAgentService.findAllInDateWithUser(today.toDate());
        if (ul.isEmpty()) {
            return;
        }

        String jobNo = ul.get(0).getUser().getJobnumber();
        insertEflowAgent(jobNo);
    }

    private void insertEflowAgent(String jobNo) {
        DateTime sdt = today;
        List<Requisition> l = rservice.findAllByCreateAndStateAndFloor(sdt, rState, floorIds);
        l.stream().forEach(r -> r.setAgent("SYSTEM"));

        try {
            UserDetails user = customUserDetailsService.loadUserByUsername(jobNo);
            SecurityPropertiesUtils.loginUserManual(user);
            String result = wareHourseService.insertEflowWithUserRemark(l, jobNo);
            SecurityPropertiesUtils.logoutUserManual();
        } catch (Exception ex) {
            logger.error("WareHourse agent fail. ", ex);
        }
    }
}
