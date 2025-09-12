/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.controller.RequisitionController;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.UserAgent;
import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserAgentService;
import com.advantech.webservice.WareHourseService;
import static com.google.common.collect.Lists.newArrayList;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
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
public class WareHouseAgentLabel extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(WareHouseAgentLabel.class);

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private WareHourseService wareHourseService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserAgentService userAgentService;

    private static final int INIT_STATE = 4;
    private final List<Integer> floorIds = newArrayList(8, 9, 10);
    private List<String> labelStorages;
    private String jobNo;

    @Autowired
    private RequisitionController requisitionController;

    private static final BigDecimal QTYRATIO = new BigDecimal(0.5);
    private static final String QTYRATIOSTR = new DecimalFormat("#%").format(QTYRATIO);

    @PostConstruct
    public void setLabelStorages() {
        this.labelStorages = requisitionController.getLabelStorages();
    }

    // prevent lazyjoin nosession issue.
    @Transactional
    public void execute() {
        if (!super.isServer()) {
            return;
        }

        List<UserAgent> ul = userAgentService.findAllLabelAgentWithUser();
        if (ul.isEmpty()) {
            return;
        }

        jobNo = ul.get(0).getUser().getJobnumber();
        filterdRequisitionLabel();
    }

    private void filterdRequisitionLabel() {
        DateTime today = new DateTime();
        DateTime sdt = today.minusDays(today.getDayOfWeek() == 1 ? 2 : 1);

        List<Requisition> l = rservice.findAllByCreateDateRequisitionStateFloor(sdt, INIT_STATE, floorIds);
        l = filterLabelMaterial(l);

        List<String> pos = l.stream().map(r -> r.getPo()).collect(Collectors.toList());
        List<Requisition> history = rservice.findAllByPo(pos);
        List<Requisition> historyLabel = filterLabelMaterial(history);

        List<List<Requisition>> resultLabel = findRequisitionLabel(historyLabel);
        this.loginOpration(resultLabel);
    }

    private void loginOpration(List<List<Requisition>> l) {
        try {
            UserDetails user = customUserDetailsService.loadUserByUsername(jobNo);
            SecurityPropertiesUtils.loginUserManual(user);

            String result = wareHourseService.insertEflowWithoutUserRemark(l.get(0), jobNo, "單據作業");
            logger.info(result);
            rservice.updateWithStateAndEvent(l.get(1), 2);

            SecurityPropertiesUtils.logoutUserManual();

            this.sendMail();
        } catch (Exception ex) {
            logger.error("Fail. ", ex);
        }
    }

    private List<Requisition> filterLabelMaterial(List<Requisition> l) {
        this.checkSapInfo(l);
        return l.stream().filter(i
                -> i.getMaterialNumber().startsWith("20")
                && floorIds.contains(i.getFloor().getId())
                && labelStorages.stream().anyMatch(ls -> i.getStorageSpaces().contains(ls))
        ).collect(Collectors.toList());
    }

    private void checkSapInfo(List<Requisition> l) {
        Map<String, List<Requisition>> map = l.stream()
                .filter(r -> r.getPoQty() == null || r.getMaterialQty() == null || r.getStorageSpaces() == null)
                .collect(Collectors.groupingBy(r -> r.getPo()));
        try {
            for (List<Requisition> values : map.values()) {
                requisitionController.retrieveSapInfos(values);
            }
        } catch (Exception ex) {
            logger.error("checkSapInfo fail. ", ex.toString());
        }
    }

    private static List<List<Requisition>> findRequisitionLabel(List<Requisition> historyLabel) {
        List<List<Requisition>> allResult = new ArrayList<>();

        // 1. Group
        Map<List<String>, List<Requisition>> grouped = historyLabel.stream()
                .collect(Collectors.groupingBy(item -> newArrayList(item.getPo(), item.getMaterialNumber())));

        List<Requisition> passResult = new ArrayList<>();
        List<Requisition> blockResult = new ArrayList<>();
        for (Map.Entry<List<String>, List<Requisition>> entry : grouped.entrySet()) {
            List<Requisition> group = entry.getValue();
            List<Requisition> targetGroup = group.stream().filter(r -> r.getRequisitionState().getId() == INIT_STATE).collect(Collectors.toList());

            int doneQty = group.stream()
                    .filter(r -> r.getRequisitionState().getId() == 5)
                    .mapToInt(r -> Math.abs(r.getAmount())).sum();
            // 2. Find minimum qty * QTYRATIO
            BigDecimal mostQty = group.stream()
                    .map(Requisition::getMaterialQty)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO)
                    .multiply(QTYRATIO);
            BigDecimal restQty = mostQty.subtract(new BigDecimal(doneQty)).max(BigDecimal.ZERO);

            // 3. Sort group descending by amount
            List<Requisition> sortedGroup = targetGroup.stream()
                    .sorted(Comparator.comparingInt((Requisition r) -> Math.abs(r.getAmount())).reversed())
                    .collect(Collectors.toList());

            // 4. Greedy approach to pick Rs whose amount sum is closest to restQty (without exceeding)
            BigDecimal selecteSum = BigDecimal.ZERO;
            for (Requisition r : sortedGroup) {
                BigDecimal tempSum = selecteSum.add(BigDecimal.valueOf(r.getAmount()).abs());
                if (tempSum.compareTo(restQty) <= 0) {
                    passResult.add(r);
                    selecteSum = tempSum;
                } else {
                    r.setRemark(r.getRemark() + " #超過" + QTYRATIOSTR);
                    blockResult.add(r);
                }

                r.setAgent("SYSTEM");
            }
        }

        allResult.add(passResult);
        allResult.add(blockResult);
        return allResult;
    }

    private void sendMail() throws Exception {

        String[] mailTarget = findEmailByNotifyId(18);
        String[] mailCcTarget = findEmailByNotifyId(18);

        if (mailTarget.length == 0) {
            logger.info("Job can't find mail target in database table.");
            return;
        }

        DateTime now = new DateTime();
        String mailBody = this.getClass().getSimpleName() + ", job successful.";
        String mailTitle = fmt.print(now) + " - 20階標籤代理通知";
        String mailSenderName = "領退料平台";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
    }
}
