/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.trigger;

import com.advantech.helper.MailManager;
import com.advantech.model.db1.*;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.VwM3WorktimeService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class RequisitionStateChangeTrigger {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionStateChangeTrigger.class);

    @Autowired
    private MailManager manager;

    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private VwM3WorktimeService vwM3WorktimeService;

    private Set<User> repairUsers = new HashSet<>();
    private int[] repairUserIds = {};
    private final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    private final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/MM/dd");

// <editor-fold desc="checkQualify.">
    public void checkQualify(List<Requisition> rForm) {

        final int[] exceptUserIds = {36};
        final int[] stateForQualify = {7};
        final int[] reasonForQualify = {2};
        final int[] typeForQualify = {2, 4};

        List<String> pos = rForm.stream().map(Requisition::getPo).collect(Collectors.toList());
        List<String> matNos = rForm.stream().map(Requisition::getMaterialNumber).collect(Collectors.toList());
        List<Requisition> result = rservice.findAllByPoAndMatNoWithLazy(pos, matNos);

        List<Requisition> checkedList = result.stream()
                .filter(e -> {
                    int userId = e.getUser().getId();
                    int rsId = e.getRequisitionState().getId();
                    int rrId = e.getRequisitionReason().getId();
                    int rtId = e.getRequisitionType().getId();
                    Date returnDate = e.getReturnDate();

                    return returnDate != null
                            && Arrays.stream(exceptUserIds).noneMatch(i -> i == userId)
                            && Arrays.stream(stateForQualify).anyMatch(i -> i == rsId)
                            && Arrays.stream(reasonForQualify).anyMatch(i -> i == rrId)
                            && Arrays.stream(typeForQualify).anyMatch(i -> i == rtId);
                })
                .collect(Collectors.groupingBy(item -> Arrays.asList(item.getPo(), item.getMaterialNumber())))
                .entrySet().stream()
                .filter(entry -> entry.getValue().stream().mapToInt(Requisition::getAmount).sum() > 2)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        if (checkedList.isEmpty()) {
            return;
        }

        sendQualifyMail(checkedList);
    }

    private void sendQualifyMail(List<Requisition> rl) {
        try {
            String[] mailTarget = findEmailWithQualify(rl);
            String[] mailCcTarget = findEmailWithQualifyCc(rl);

            if (mailTarget.length == 0) {
                logger.info("Trigger sendQualifyMail can't find mail target.");
                return;
            }

            List<Requisition> rlWithLazy = rl;

            String mailBody = generateQualifyBody(rlWithLazy);
            String mailTitle = "領退料平台-累積退料明細-" + fmtD.print(new DateTime());

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);
        } catch (SAXException | InvalidFormatException | IOException | MessagingException ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String[] findEmailWithQualify(List<Requisition> rl) {
        Set<String> targets = new HashSet<>();

        String[] mailTarget1 = findEmailByNotify("qualify_target");
        targets.addAll(Arrays.asList(mailTarget1));

        List<String> modelNames = rl.stream().map(Requisition::getModelName).collect(Collectors.toList());
        Set<String> mailTargetPqe = findEmailInWorktime(modelNames, VwM3Worktime::getQcMail);
        targets.addAll(mailTargetPqe);

        return targets.toArray(new String[0]);
    }

    private String[] findEmailWithQualifyCc(List<Requisition> rl) {
        Set<String> targets = new HashSet<>();

        String[] mailTarget1 = findEmailByNotify("qualify_target_cc");
        targets.addAll(Arrays.asList(mailTarget1));

        List<String> modelNames = rl.stream().map(Requisition::getModelName).collect(Collectors.toList());
        Set<String> mailTargetSpe = findEmailInWorktime(modelNames, VwM3Worktime::getSpeMail);
        targets.addAll(mailTargetSpe);
        Set<String> mailTargetBpe = findEmailInWorktime(modelNames, VwM3Worktime::getBpeMail);
        targets.addAll(mailTargetBpe);

        return targets.toArray(new String[0]);
    }

    private Set<String> findEmailInWorktime(List<String> modelNames, Function<VwM3Worktime, String> mailProvider) {
        List<VwM3Worktime> vwM3Wt = vwM3WorktimeService.findAllByModelName(modelNames);
        Set<String> mails = vwM3Wt.stream().map(mailProvider).filter(s -> s != null).collect(Collectors.toSet());
        return mails;
    }

    private String generateQualifyBody(List<Requisition> rl) throws IOException, SAXException, InvalidFormatException {
        StringBuilder sb = new StringBuilder();

        //設定mail格式(css...etc)
        sb.append("<style>");
        sb.append("table {border-collapse: collapse; padding:5px; }");
        sb.append("table, th, td {border: 1px solid black;}");
        sb.append("table th {background-color: yellow;}");
        sb.append("#mailBody {font-family: 微軟正黑體;}");
        sb.append(".highlight {background-color: yellow;}");
        sb.append("</style>");
        sb.append("<div id='mailBody'>");
        sb.append("<h3>Dear user:</h3>");
        sb.append("<h3>累積退料明細:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>數量</th>");
        sb.append("<th>製程</th>");
        sb.append("<th>原因</th>");
        sb.append("<th>區域</th>");
        sb.append("<th>申請狀態</th>");
        sb.append("<th>退料日期</th>");
        sb.append("<th>料號狀態</th>");
        sb.append("<th>備註</th>");
        sb.append("</tr>");

        rl.forEach(e -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(e.getPo());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getModelName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getMaterialNumber());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getAmount());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(getStringSafely(e.getRequisitionFlow(), RequisitionFlow::getName));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(getStringSafely(e.getRequisitionReason(), RequisitionReason::getName));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(getStringSafely(e.getFloor(), Floor::getName));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(getStringSafely(e.getRequisitionState(), RequisitionState::getName));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(fmtD.print(new DateTime(e.getReturnDate())));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(getStringSafely(e.getRequisitionType(), RequisitionType::getName));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getRemark());
            sb.append("</td>");
            sb.append("</tr>");
        });

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }
// </editor-fold>

    private <T> String getStringSafely(T obj, Function<T, String> getter) {
        return obj != null ? getter.apply(obj) : "";
    }

// <editor-fold desc="checkRepair.">
    public void checkRepair(List<Requisition> rl) {
        setRepairUserList();
        final int[] stateForRepair = {2, 5};

        List<Requisition> checkedList = rl.stream()
                .filter(e -> {
                    int userId = e.getUser().getId();
                    int rsId = e.getRequisitionState().getId();
                    return Arrays.stream(repairUserIds).anyMatch(i -> i == userId)
                            && Arrays.stream(stateForRepair).anyMatch(i -> i == rsId);
                }).collect(Collectors.toList());

        if (checkedList.isEmpty()) {
            return;
        }
        sendRepairMail(checkedList);
    }

    private void setRepairUserList() {
        Optional<UserNotification> oUn = notificationService.findByNameWithUser("repair_state_change_target");
        if (oUn.isPresent()) {
            repairUsers = oUn.get().getUsers();
            repairUserIds = repairUsers.stream().mapToInt(u -> u.getId()).toArray();
        } else {
            logger.info("Repair notification not found.");
        }
    }

    private void sendRepairMail(List<Requisition> rl) {
        try {
            String[] mailTarget = findEmailInRepair(rl);
            String[] mailCcTarget = findEmailByNotify("repair_state_change_target_cc");

            if (mailTarget.length == 0) {
                logger.info("Trigger sendRepairMail can't find mail target.");
                return;
            }

            List<Integer> rIds = rl.stream().map(t -> t.getId()).collect(Collectors.toList());
            List<Requisition> rlWithLazy = rservice.findAllByIdWithUserAndState(rIds);

            String mailBody = generateMailBody(rlWithLazy);
            String mailTitle = "維修用料已申請通知-" + getFirstPoByGroupUser(rlWithLazy);

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);
        } catch (SAXException | InvalidFormatException | IOException | MessagingException ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String generateMailBody(List<Requisition> rl) throws IOException, SAXException, InvalidFormatException {
        StringBuilder sb = new StringBuilder();

        //設定mail格式(css...etc)
        sb.append("<style>");
        sb.append("table {border-collapse: collapse; padding:5px; }");
        sb.append("table, th, td {border: 1px solid black;}");
        sb.append("table th {background-color: yellow;}");
        sb.append("#mailBody {font-family: 微軟正黑體;}");
        sb.append(".highlight {background-color: yellow;}");
        sb.append("</style>");
        sb.append("<div id='mailBody'>");
        sb.append("<h3>Dear user:</h3>");
        sb.append("<h3>維修用料申請如下:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>數量</th>");
        sb.append("<th>申請人</th>");
        sb.append("<th>申請狀態</th>");
        sb.append("<th>申請日期</th>");
        sb.append("<th>備註</th>");
        sb.append("</tr>");

        rl.forEach(e -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(e.getPo());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getModelName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getMaterialNumber());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getAmount());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getUser().getUsername());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getRequisitionState().getName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(fmt.print(new DateTime(e.getCreateDate())));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getRemark());
            sb.append("</td>");
            sb.append("</tr>");
        });

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

    private String[] findEmailInRepair(List<Requisition> rl) {
        List<Integer> userIds = rl.stream().map(r -> r.getUser().getId()).collect(Collectors.toList());
        Stream<User> users = repairUsers.stream().filter(u -> userIds.contains(u.getId()));
        return users.map(u -> u.getEmail()).toArray(size -> new String[size]);
    }

    private String getFirstPoByGroupUser(List<Requisition> rl) {
        Map<User, String> mapP0 = rl.stream()
                .collect(Collectors.groupingBy(Requisition::getUser,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream()
                                        .map(Requisition::getPo)
                                        .findFirst()
                                        .orElse(""))));
        return String.join(",", mapP0.values());
    }
// </editor-fold>

    private String[] findEmailByNotify(String name) {
        String[] emails = {};
        Optional<UserNotification> oUn = notificationService.findByNameWithUser(name);
        if (oUn.isPresent()) {
            Set<User> users = oUn.get().getUsers();
            emails = users.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
        } else {
            logger.info("Notification name: " + name + " not found.");
        }

        return emails;
    }
}
