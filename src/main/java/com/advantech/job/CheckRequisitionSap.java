/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.controller.RequisitionController;
import com.advantech.model.db1.Requisition;
import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.RequisitionService;
import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class CheckRequisitionSap extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(CheckRequisitionSap.class);

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private FloorService floorService;

    private List<Requisition> checkedReq = Arrays.asList();

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final List<Integer> stateIds = newArrayList(4, 6);
    private final String jobNo = "sysop";

    protected final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    @Autowired
    private RequisitionController requisitionController;

    @Transactional // prevent lazyjoin nosession issue.
    public void execute() {
        if (!super.isServer()) {
            return;
        }

        try {
            filterdRequisitionLabel();
            if (!checkedReq.isEmpty()) {
                loginOpration();
            }
        } catch (Exception ex) {
            logger.error("Fail on filterdRequisitionLabel().", ex);
        }
    }

    private void filterdRequisitionLabel() throws Exception {
        DateTime today = new DateTime();
        DateTime sdt = today.minusMonths(1);

        List<Integer> floorIds = floorService.findAllEnableState().stream().map(f -> f.getId()).collect(Collectors.toList());
        List<Requisition> l = rservice.findAllByCreateDateRequisitionStateFloor(sdt, stateIds, floorIds);

        Map<String, List<Requisition>> l_map = l.stream().collect(Collectors.groupingBy(Requisition::getPo));
        for (Map.Entry<String, List<Requisition>> entry : l_map.entrySet()) {
            List<Requisition> val = (List<Requisition>) entry.getValue();
            requisitionController.retrieveSapInfos(val);
        }
        checkedReq = l;
    }

    private void loginOpration() {
        try {
            UserDetails user = customUserDetailsService.loadUserByUsername(jobNo);
            SecurityPropertiesUtils.loginUserManual(user);

            checkedReq.forEach(r -> rservice.save(r, r.getRemark(), "SYSTEM"));

            SecurityPropertiesUtils.logoutUserManual();

            this.sendMail();
        } catch (Exception ex) {
            logger.error("Fail on loginOpration(). ", ex);
        }
    }

    protected void sendMail() throws Exception {

        String[] mailTarget = findEmailByNotifyId(18);
        String[] mailCcTarget = findEmailByNotifyId(18);

        if (mailTarget.length == 0) {
            logger.info("Job CheckRequisitionSap can't find mail target in database table.");
            return;
        }

        DateTime now = new DateTime();
        String mailBody = generateMailBody();
        String mailTitle = fmt.print(now) + " - check Sap Status";
        String mailSenderName = "領退料平台";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
    }

    public String generateMailBody() throws IOException, SAXException, InvalidFormatException {

        StringBuilder sb = new StringBuilder();

        //設定mail格式(css...etc)
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<style>");
        sb.append("table {border-collapse: collapse; padding:5px; }");
        sb.append("table, th, td {border: 1px solid black;}");
        sb.append("table th {background-color: yellow;}");
        sb.append("#mailBody {font-family: 微軟正黑體;}");
        sb.append(".highlight {background-color: yellow;}");
        sb.append(".m3 {background-color: #FFDAC8;}");
        sb.append(".rightAlign {text-align:right;}");
        sb.append(".total {font-weight: bold;}");
        sb.append("</style>");
        sb.append("<div id='mailBody'>");
        sb.append("<h3>Dear User:</h3>");
        sb.append("<h3>Check sap status list:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>ID</th>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>狀態</th>");
        sb.append("<th>日期</th>");
        sb.append("<th>備註</th>");
        sb.append("</tr>");

        List<Requisition> l = checkedReq.stream().sorted(Comparator.comparing(Requisition::getId).reversed()).collect(Collectors.toList());

        for (Requisition r : l) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(r.getId());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getPo());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getModelName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getMaterialNumber());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getRequisitionState().getName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(fmtD.print(r.getCreateDate().getTime()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getRemark());
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        return sb.toString();
    }

}
