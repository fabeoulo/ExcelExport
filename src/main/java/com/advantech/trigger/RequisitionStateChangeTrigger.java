/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.trigger;

import com.advantech.helper.MailManager;
import com.advantech.job.SendReport;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class RequisitionStateChangeTrigger {

    private static final Logger logger = LoggerFactory.getLogger(SendReport.class);

    @Autowired
    private MailManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserNotificationService notificationService;

    private final int[] repairUserList = {742, 753, 895, 1024, 1025, 36};
    private final int[] StateChangeList = {2, 5};
    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    public void checkRepair(List<Requisition> r) {
        List<Requisition> checkedList = r.stream()
                .filter(e -> {
                    int userId = e.getUser().getId();
                    int rsId = e.getRequisitionState().getId();
                    return Arrays.stream(repairUserList).anyMatch(i -> i == userId)
                            && Arrays.stream(StateChangeList).anyMatch(i -> i == rsId);
                }).collect(Collectors.toList());

        if (checkedList.isEmpty()) {
            return;
        }
        sendRepairMail(checkedList);
    }

    private String[] findEmail(List<Requisition> list) {
        return list.stream().map(m -> m.getUser().getEmail()).toArray(size -> new String[size]);
    }

    private void sendRepairMail(List<Requisition> r) {
        try {
            String[] mailTargetMy = findEmail(r);
            UserNotification notifiCc = notificationService.findById(16).get();

            String[] mailTarget = {"Justin.Yeh@advantech.com.tw"};
            String[] mailCcTarget = findUsersMail(notifiCc);//{"Justin.Yeh@advantech.com.tw"};

            if (mailTarget.length == 0) {
                logger.info("Trigger sendReport can't find mail target.");
                return;
            }

            String mailBody = generateMailBody(r);
            String mailTitle = "維修到料通知";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);

        } catch (SAXException | InvalidFormatException | IOException | MessagingException ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String generateMailBody(List<Requisition> r) throws IOException, SAXException, InvalidFormatException {
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
        sb.append("<h3>維修到料如下:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>數量</th>");
        sb.append("<th>申請人</th>");
        sb.append("<th>申請狀態</th>");
        sb.append("<th>申請日期</th>");
        sb.append("<th>領料日期</th>");
        sb.append("<th>退料日期</th>");
        sb.append("<th>備註</th>");
        sb.append("</tr>");

        r.forEach(e -> {
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
            sb.append(fmt.print(new DateTime(e.getReceiveDate())));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(fmt.print(new DateTime(e.getReturnDate())));
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

    private String[] findUsersMail(UserNotification notifi) {
        List<User> l = userService.findByUserNotifications(notifi);
        return l.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
    }
}