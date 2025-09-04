/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.model.db1.VwM3Worktime;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.ReturnService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.VwM3WorktimeService;
import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
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
public class SendCheckQualify extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(SendCheckQualify.class);

    @Autowired
    private ReturnService returnService;

    private final DateTime edt = DateTime.now().withTime(17, 0, 0, 0);

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    private VwM3WorktimeService vwM3WorktimeService;

    @Autowired
    private FloorService floorService;

    public void execute() {
        if (!super.isServer()) {
            return;
        }
        filterdRequisition();
    }

    private void filterdRequisition() {
        DateTime sdt = new DateTime(edt).minusDays(edt.getDayOfWeek() == 1 ? 2 : 1);

        List<Requisition> l = returnService.findAllNoGood(sdt, edt);

        checkQualify(l);
    }

// <editor-fold desc="checkQualify.">
    private void checkQualify(List<Requisition> rl) {

        List<Requisition> filterSrc = returnService.filterQualify(rl);
        if (filterSrc.isEmpty()) {
            return;
        }

        List<String> pos = filterSrc.stream().map(Requisition::getPo).collect(Collectors.toList());
        List<String> matNos = filterSrc.stream().map(Requisition::getMaterialNumber).collect(Collectors.toList());
        List<Requisition> returnAll = rservice.findAllByPoAndMatNoWithLazy(pos, matNos)
                .stream().filter(a -> a.getReturnDate() != null && a.getReturnDate().before(edt.toDate()))
                .collect(Collectors.toList());

        List<Requisition> checkedList = returnService.getQualifyCheckedList(returnAll);
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
                logger.info("SendQualifyMail can't find mail target.");
                return;
            }

            List<Requisition> rlWithLazy = rl;

            String mailBody = generateQualifyBody(rlWithLazy);
            String mailTitle = getTitleArea() + "領退料平台-累積退料明細-" + returnService.getFormatDate(edt.toDate());
            String mailSenderName = "領退料平台";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
//            sendByApi(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);

        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String[] findEmailWithQualify(List<Requisition> rl) {
        Set<String> targets = new HashSet<>();

        String[] mailTarget1 = findEmailByNotify("qualify_target");
        targets.addAll(Arrays.asList(mailTarget1));

//        List<String> modelNames = rl.stream().map(Requisition::getModelName).collect(Collectors.toList());
//        Set<String> mailTargetPqe = findEmailInWorktime(modelNames, VwM3Worktime::getQcMail);
//        targets.addAll(mailTargetPqe);
//
        return targets.toArray(new String[0]);
    }

    private String[] findEmailWithQualifyCc(List<Requisition> rl) {
        Set<String> targets = new HashSet<>();

        String[] mailTarget1 = findEmailByNotify("qualify_target_cc");
        targets.addAll(Arrays.asList(mailTarget1));

//        List<String> modelNames = rl.stream().map(Requisition::getModelName).collect(Collectors.toList());
//        Set<String> mailTargetSpe = findEmailInWorktime(modelNames, VwM3Worktime::getSpeMail);
//        targets.addAll(mailTargetSpe);
//        Set<String> mailTargetBpe = findEmailInWorktime(modelNames, VwM3Worktime::getBpeMail);
//        targets.addAll(mailTargetBpe);
//
        return targets.toArray(new String[0]);
    }

    private Set<String> findEmailInWorktime(List<String> modelNames, Function<VwM3Worktime, String> mailProvider) {
        List<VwM3Worktime> vwM3Wt = vwM3WorktimeService.findAllByModelName(modelNames);
        Set<String> mails = vwM3Wt.stream().map(mailProvider).filter(s -> s != null).collect(Collectors.toSet());
        return mails;
    }
// </editor-fold>

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
        sb.append("<h3>Dears：</h3>");
        sb.append("<h3>以下 原材 於同工單已累積相同異常達到三筆(含)以上，請相關單位協助確認。</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>區域</th>");
        sb.append("<th>工單</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>異常分類</th>");
        sb.append("<th>不良數量</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>工單數量</th>");
        sb.append("<th>不良原因</th>");
        sb.append("</tr>");

        rl.forEach(e -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(returnService.getStringSafely(e.getFloor(), Floor::getName, ""));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getPo());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getMaterialNumber());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getRequisitionCateMesCustom());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getAmount());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getModelName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getPoQty().intValue());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getReturnReason());
            sb.append("</td>");
            sb.append("</tr>");
        });

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

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

    private String getTitleArea() {
        return floorService.findAllById(newArrayList(6, 7, 9)).stream()
                .map(f -> f.getName())
                .collect(Collectors.joining("/"));
    }

}
