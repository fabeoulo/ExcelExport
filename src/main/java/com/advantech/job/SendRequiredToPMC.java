/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db1.Requisition;
import com.advantech.sap.SapService;
import com.advantech.service.db1.RequisitionService;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
public class SendRequiredToPMC extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(SendRequiredToPMC.class);

    private Date sD, eD;

    protected final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    @Autowired
    private RequisitionService rservice;

    @Autowired
    private SapService sapService;

    public void execute() {
        try {
            this.sendMail();
        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    protected void sendMail() throws Exception {

        String[] mailTarget = findEmailByNotifyId(19);
        String[] mailCcTarget = findEmailByNotifyId(18);

        if (mailTarget.length == 0) {
            logger.info("Job SendLackWithStock can't find mail target in database table.");
            return;
        }

        String mailBody = generateMailBody();
        String mailTitle = fmt.print(new DateTime()) + " - 半日領料通知 " + titleFloor;
        String fromName = "領退料平台";

//        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, fromName);
        super.sendByApi(mailTarget, mailCcTarget, mailTitle, mailBody, fromName);
    }

    private void setDatetime() {
        DateTime dt = new DateTime();
        DateTime sdt, edt;
        if (dt.getHourOfDay() < 16) {
            sdt = dt.minusDays(1).withTime(16, 0, 0, 1);
            edt = dt.withTime(11, 0, 0, 0);
        } else {
            sdt = dt.withTime(11, 0, 0, 1);
            edt = dt.withTime(16, 0, 0, 0);
        }
        sD = sdt.toDate();
        eD = edt.toDate();
    }

    public String generateMailBody() throws IOException, SAXException, InvalidFormatException, Exception {

        setDatetime();
        List<Requisition> rl = rservice.findAllByHalfdayWithUserAndState(sD, eD);
        Map<String, String> mrpCodeMap = sapService.getMrpCodeMap(rl);

        List<Map.Entry<Requisition, String>> list = rl.stream()
                .map(r -> new AbstractMap.SimpleEntry<>(r, mrpCodeMap.get(r.getMaterialNumber() + "," + r.getWerk())))
                .sorted(Comparator
                        .comparing((Map.Entry<Requisition, String> e) -> e.getKey().getWerk())
                        .thenComparing(Map.Entry.comparingByValue())
                        .thenComparing((Map.Entry<Requisition, String> e) -> e.getKey().getMaterialNumber())
                        .thenComparing((Map.Entry<Requisition, String> e) -> e.getKey().getPo())
                )
                .collect(Collectors.toList());

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
        sb.append("<h3>半日領料如下:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>時間</th>");
        sb.append("<th>工單</th>");
        sb.append("<th>成品料號</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>數量</th>");
        sb.append("<th>廠區</th>");
        sb.append("<th>MRP_Code</th>");
        sb.append("</tr>");

        for (Map.Entry<Requisition, String> item : list) {
            Requisition r = item.getKey();
            String mrpCode = item.getValue();

            if ("TWM3".equals(r.getWerk()) || "TWM9".equals(r.getWerk())) {
                sb.append("<tr class='m3'>");
            } else {
                sb.append("<tr>");
            }
            sb.append("<td>");
            sb.append(fmtD.print(r.getReceiveDate().getTime()));
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
            sb.append(Math.abs(r.getAmount()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(r.getWerk());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.isBlank(mrpCode) ? "" : mrpCode);
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        return sb.toString();
    }

}
