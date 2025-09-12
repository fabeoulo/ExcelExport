/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db1.ScrappedRequisition;
import com.advantech.service.db1.ScrappedService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class SendScrapped extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(SendScrapped.class);

    @Autowired
    private ScrappedService scrappedService;

    private DateTime endDt, startDt;
    private Integer lastWeekNo;

    private final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/MM/dd");

    private void setDateTime() {
        endDt = DateTime.now().withTime(8, 30, 0, 0).dayOfWeek().withMinimumValue();
        startDt = new DateTime("2025-09-01");
        lastWeekNo = endDt.plusWeeks(-1).getWeekOfWeekyear();
    }

    public void execute() {
        if (!super.isServer()) {
            return;
        }

        setDateTime();

        List<ScrappedRequisition> targetAll = scrappedService.findAllTarget(startDt, endDt);

        sendTargetMail(targetAll);
    }

    private void sendTargetMail(List<ScrappedRequisition> rl) {
        try {
            String[] mailTarget = findEmailByNotify("scrapped_target");
            String[] mailCcTarget = findEmailByNotifyId(18);

            if (mailTarget.length == 0) {
                logger.info("SendScrapped can't find mail target.");
                return;
            }

            String mailBody = generateBody(rl);
            String mailTitle = "MFG W" + lastWeekNo + " 製損週報";
            String mailSenderName = "領退料平台";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
//            sendByApi(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);

        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String generateBody(List<ScrappedRequisition> rl) {
        StringBuilder sb = new StringBuilder();

        //設定mail格式(css...etc)
        sb.append("<style>");
        sb.append("table {border-collapse: collapse; padding:5px; }");
        sb.append("table, th, td {border: 1px solid black;}");
        sb.append("table th {background-color: yellow;}");
        sb.append("#mailBody {font-family: 微軟正黑體;}");
        sb.append(".highlight {background-color: yellow;}");
        sb.append(".total {font-weight: bold;}");
        sb.append("</style>");
        sb.append("<div id='mailBody'>");
        sb.append("<h3>Hi All:</h3>");
        sb.append("<h3>MFG W");
        sb.append(lastWeekNo);
        sb.append(" 製損報廢清單及金額明細如下說明");
        sb.append(":</h3>");

        //Generate weekly table
        Map<Integer, List<ScrappedRequisition>> grouped = scrappedService.getWeeklyGroup(rl);
        List<Map<Integer, List<Integer>>> dataList = scrappedService.getPriceMap(grouped);
        this.addTable(sb, dataList);
        this.addTableWeekly(sb, dataList);

        //Generate detail table
        List<ScrappedRequisition> lastWeekList = grouped.getOrDefault(lastWeekNo, Arrays.asList());
        List<ScrappedRequisition> gl3f = scrappedService.getFilterByFloor(lastWeekList, 9);
        List<ScrappedRequisition> gl4f = scrappedService.getFilterByFloor(lastWeekList, 10);
        sb.append("<h5>MFG W");
        sb.append(lastWeekNo);
        sb.append(" 製損明細如下:</h5>");
        this.addTableDetail(sb, gl4f, "4F 製損明細");
        this.addTableDetail(sb, gl3f, "3F 製損明細");

        return sb.toString();
    }

    private String addTable(StringBuilder sb, List<Map<Integer, List<Integer>>> dataList) {

        sb.append("<table>");

        sb.append("<tr><th></th><th colspan='2'>製損(100元以上)</th></tr>");
        sb.append("<tr>");
        sb.append("<th>分類</th>");
        sb.append("<th>不良數量</th>");
        sb.append("<th>金額</th>");
        sb.append("</tr>");

        List<String> leftColumn = Arrays.asList("4F", "3F", "Total");
        for (int i = 0; i < leftColumn.size(); i++) {
            if (i < 2) {
                sb.append("<tr>");
            } else {
                sb.append("<tr class='total'>");
            }

            String colName = "<td>" + leftColumn.get(i) + "</td>";
            sb.append(colName);

            dataList.get(i).forEach((k, v) -> {
                if (lastWeekNo.equals(k)) {
                    sb.append("<td>");
                    sb.append(v.get(1));
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(v.get(0));
                    sb.append("</td>");
                }
            });

            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

    private String addTableWeekly(StringBuilder sb, List<Map<Integer, List<Integer>>> dataList) {

        sb.append("<table>");

        sb.append("<tr>");
        sb.append("<th>週別</th>");

        dataList.get(0).keySet().forEach(k -> {
            String header = "<th>W" + k + "</th>";
            sb.append(header);
        });
        sb.append("</tr>");

        List<String> leftColumn = Arrays.asList("4F報廢金額", "3F報廢金額", "3&4F合計", "累計報廢金額");
        for (int i = 0; i < leftColumn.size(); i++) {
            if (i < 2) {
                sb.append("<tr>");
            } else {
                sb.append("<tr class='total'>");
            }

            String colName = "<td>" + leftColumn.get(i) + "</td>";
            sb.append(colName);

            dataList.get(i).forEach((k, v) -> {
                sb.append("<td>");
                sb.append(v.get(0));
                sb.append("</td>");
            });

            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

    private String addTableDetail(StringBuilder sb, List<ScrappedRequisition> rl, String tableHeader) {

        sb.append("<table>");
        sb.append("<tr><th colspan='8'>");
        sb.append(tableHeader);
        sb.append("</th></tr>");
        sb.append("<tr>");
        sb.append("<th>報廢日期</th>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>不良數量</th>");
        sb.append("<th>不良原因</th>");
        sb.append("<th>單價</th>");
        sb.append("<th>Total</th>");
//        sb.append("<th>申請人</th>");
        sb.append("</tr>");

        rl.forEach(e -> {
            int total = scrappedService.getPriceSum(Arrays.asList(e));

            sb.append("<tr>");
            sb.append("<td>");
            sb.append(fmtD.print(new DateTime(e.getReturnDate())));
            sb.append("</td>");
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
            sb.append(e.getReturnReason());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getUnitPrice());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(total);
            sb.append("</td>");
//            sb.append("<td>");
//            sb.append(e.getUser().getUsername());
//            sb.append("</td>");
            sb.append("</tr>");
        });

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

}
