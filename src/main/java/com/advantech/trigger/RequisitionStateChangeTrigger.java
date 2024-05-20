/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.trigger;

import com.advantech.helper.MailManager;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.UserNotificationService;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    private Set<User> repairUsers = new HashSet<>();
    private int[] repairUserIds = {};
    private final int[] StateChangeList = {2, 5};
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    public void checkRepair(List<Requisition> rl) {
        setRepairUserList();
        List<Requisition> checkedList = rl.stream()
                .filter(e -> {
                    int userId = e.getUser().getId();
                    int rsId = e.getRequisitionState().getId();
                    return Arrays.stream(repairUserIds).anyMatch(i -> i == userId)
                            && Arrays.stream(StateChangeList).anyMatch(i -> i == rsId);
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
                logger.info("Trigger sendReport can't find mail target.");
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
        sb.append("<h3>Dear User:</h3>");
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
            String Remark = e.getRemark();//""
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

    private String[] findEmailByNotify(String name) {
        String[] emails = {};
        Optional<UserNotification> oUn = notificationService.findByNameWithUser(name);
        if (oUn.isPresent()) {
            Set<User> users = oUn.get().getUsers();
            emails = users.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
        } else {
            logger.info("Repair CC notification not found.");
        }
        
        return emails;
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
}
