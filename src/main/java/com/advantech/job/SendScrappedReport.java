/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.helper.StringUtils;
import com.advantech.model.db1.ScrappedRequisition;
import com.advantech.model.db1.ScrappedSummary;
import com.advantech.service.db1.ScrappedService;
import com.advantech.service.db1.ScrappedSummaryService;
import static com.google.common.collect.Lists.newArrayList;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
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
public class SendScrappedReport extends JobScrappedBase {

    private static final Logger logger = LoggerFactory.getLogger(SendScrappedReport.class);

    @Autowired
    private ScrappedService scrappedService;

    @Autowired
    private ScrappedSummaryService scrappedSummaryService;

    private Map<Integer, List<ScrappedRequisition>> groupedUp = new HashMap<>();

    private final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/MM/dd");

    private String scrappedTotal;

    public void execute() {
        if (!super.isServer()) {
            return;
        }

        super.setDateTime(DateTime.now());

        getWeekData();

        sendTargetMail();

        this.sendReplyMail();
    }

    // repeat save is OK.
    private void getWeekData() {

        DateTime sD = lastWeek.dayOfWeek().withMinimumValue();
        DateTime eD = thisMon;

        List<ScrappedRequisition> rl = scrappedService.findAllScrapped(sD, eD, lastWeekYw);

        Predicate<ScrappedRequisition> prUp = sr -> new BigDecimal(100).compareTo(sr.getUnitPrice()) < 0;
        groupedUp = scrappedService.getWeeklyGroup(rl, prUp);
    }

    private void sendTargetMail() {
        try {
            String[] mailTarget = findEmailByNotify("scrapped_target");
            String[] mailCcTarget = findEmailByNotifyId(18);

            if (mailTarget.length == 0) {
                logger.info("SendScrapped can't find mail target.");
                return;
            }

            String mailBody = generateBody();
            String mailTitle = "MFG W" + lastWeekNo + " 製損週報, 報廢金額為" + scrappedTotal;
            String mailSenderName = "領退料平台";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
//            sendByApi(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);

        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private String generateBody() {
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
        sb.append("<h3>");
        sb.append(lastWeekYear);
        sb.append(" MFG W");
        sb.append(lastWeekNo);
        sb.append(" 製損報廢清單及金額明細如下說明");
        sb.append(":</h3>");

        this.addTable(sb);
        //Generate weekly table
        int startDtYw = startDt.getWeekyear() * 100 + startDt.getWeekOfWeekyear();
        int endDtYw = endDt.getWeekyear() * 100 + endDt.getWeekOfWeekyear();
        int startDtYwLast26 = startDtLast26.getWeekyear() * 100 + startDtLast26.getWeekOfWeekyear();
        int endDtYwLast26 = endDtLast26.getWeekyear() * 100 + endDtLast26.getWeekOfWeekyear();
        List<Tuple> reports = scrappedSummaryService.findAllReportByYkBetween(startDtYw, endDtYw, startDtYwLast26, endDtYwLast26);
        this.addTableWeekly(sb, reports);

        //Generate detail table
        List<ScrappedRequisition> lastWeekList = groupedUp.getOrDefault(lastWeekYw, Arrays.asList());
        List<ScrappedRequisition> gl3f = scrappedService.getFilterByFloor(lastWeekList, 9);
        List<ScrappedRequisition> gl4f = scrappedService.getFilterByFloor(lastWeekList, 10);
        sb.append("<h5>MFG W");
        sb.append(lastWeekNo);
        sb.append(" 報廢明細如下:</h5>");
        this.addTableDetail(sb, gl4f, "4F 報廢明細");
        this.addTableDetail(sb, gl3f, "3F 報廢明細");

        return sb.toString();
    }

    private String addTable(StringBuilder sb) {

        List<ScrappedSummary> lastWeekSummarys = scrappedSummaryService.findAllByYkBetween(lastWeekYw, lastWeekYw);
        ScrappedSummary lastWeek4F = getFilterByArea(lastWeekSummarys, "M9_4F");
        ScrappedSummary lastWeek3F = getFilterByArea(lastWeekSummarys, "M9_3F");
        ScrappedSummary lastWeekM6 = getFilterByArea(lastWeekSummarys, "M6");
        List<ScrappedSummary> dataList = Arrays.asList(lastWeek4F, lastWeek3F, lastWeekM6);

        sb.append("<table>");

        sb.append("<tr><th></th><th colspan='6'>超耗金額</th><th colspan='2'>報廢金額</th></tr>");
        sb.append("<tr><th></th><th colspan='2'>短少(單價100以下)</th><th colspan='2'>短少(單價100以上)</th><th colspan='2'>製損(100元以下或標籤)</th><th colspan='2'>製損(100元以上)</th></tr>");
        sb.append("<tr>");
        sb.append("<th>樓層</th><th>不良數量</th><th>金額</th><th>不良數量</th><th>金額</th><th>不良數量</th><th>金額</th><th>不良數量</th><th>金額</th>");
        sb.append("</tr>");

        for (int i = 0; i < dataList.size(); i++) {
            ScrappedSummary v = dataList.get(i);

            sb.append("<tr>");

            String colName = "<td>" + v.getArea() + "</td>";
            sb.append(colName);

            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getShortPcsHundredDown()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getShortSumHundredDown()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getShortPcsHundredUp()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getShortSumHundredUp()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getScrapPcsHundredDown()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getScrapSumHundredDown()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getScrapPcsHundredUp()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(v.getScrapSumHundredUp()));
            sb.append("</td>");

            sb.append("</tr>");
        }

        sb.append("<tr class='total'>");
        String colName = "<td>Total</td>";
        sb.append(colName);

        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getShortPcsHundredDown()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getShortSumHundredDown()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getShortPcsHundredUp()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getShortSumHundredUp()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getScrapPcsHundredDown()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getScrapSumHundredDown()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getScrapPcsHundredUp()).sum()));
        sb.append("</td>");
        sb.append("<td>");
        scrappedTotal = StringUtils.formatNumber(dataList.stream().mapToInt(ss -> ss.getScrapSumHundredUp()).sum());
        sb.append(scrappedTotal);
        sb.append("</td>");

        sb.append("</tr>");

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

    private String addTableWeekly(StringBuilder sb, List<Tuple> reports) {

        Tuple tFirst = reports.stream().findFirst().orElse(null);
        if (tFirst == null) {
            return sb.toString();
        }

        sb.append("<table>");

        sb.append("<tr>");
        sb.append("<th>週別</th>");

        for (TupleElement<?> e : tFirst.getElements()) {
            String name = e.getAlias();
            if (!"area".equals(name)) {
                String header = "<th>W" + name + "</th>";
                sb.append(header);
            }
        }
        sb.append("</tr>");

        for (Tuple t : reports) {
            sb.append("<tr>");
            for (TupleElement<?> e : t.getElements()) {
                Object value = t.get(e);

                sb.append("<td>");
                sb.append(StringUtils.formatNumber(value));
                sb.append("</td>");

            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        sb.append("<hr />");

        return sb.toString();
    }

    private ScrappedSummary getFilterByArea(List<ScrappedSummary> gl, String area) {
        return gl.stream().filter(all -> all.getArea().equals(area)).findFirst().orElse(new ScrappedSummary());
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
            sb.append(StringUtils.formatNumber(e.getAmount()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(e.getReturnReason());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(e.getUnitPrice()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(StringUtils.formatNumber(total));
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

    private void sendReplyMail() {

        try {
            String[] mailTarget = findEmailByNotify("scrapped_target_reply");
            String[] mailCcTarget = findEmailByNotify("scrapped_target_reply_cc");

            if (mailTarget.length == 0) {
                logger.info("SendScrapped can't find mail target.");
                return;
            }

            List<ScrappedRequisition> lastWeekList = groupedUp.getOrDefault(lastWeekYw, Arrays.asList());
            List<ScrappedRequisition> gl3f = this.getListToReply(scrappedService.getFilterByFloor(lastWeekList, 9));
            List<ScrappedRequisition> gl4f = this.getListToReply(scrappedService.getFilterByFloor(lastWeekList, 10));

            if (gl3f.isEmpty() && gl4f.isEmpty()) {
                mailTarget = findEmailByNotifyId(18);
                mailCcTarget = findEmailByNotifyId(18);
            }

            String mailBody = generateBodyReply(gl3f, gl4f);
            String mailTitle = "MFG W" + lastWeekNo + " 待回覆製損清單明細";
            String mailSenderName = "領退料平台";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);
//            sendByApi(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);

        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private List<ScrappedRequisition> getListToReply(List<ScrappedRequisition> srl) {

        List<ScrappedRequisition> result = newArrayList();

        Map<String, Integer> amountSumMap = new HashMap<>();

        for (ScrappedRequisition item : srl) {
            String key = item.getMaterialNumber();
            amountSumMap.put(key, amountSumMap.getOrDefault(key, 0) + item.getAmount());
        }

        for (ScrappedRequisition item : srl) {
            if (amountSumMap.get(item.getMaterialNumber()) >= 3
                    || BigDecimal.valueOf(1000).compareTo(item.getUnitPrice()) <= 0) {
                result.add(item);
            }
        }

        return result;
    }

    private String generateBodyReply(List<ScrappedRequisition> gl3f, List<ScrappedRequisition> gl4f) {
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
        sb.append("<h3>Dear Rain and Che,</h3>");
        sb.append("<h3>請本週四下班前提供相關製損說明及改善對策</h3>");

        sb.append("<h5>@Rain,</h5>");
        this.addTableDetail(sb, gl3f, "3F 報廢明細");

        sb.append("<h5>@Che,</h5>");
        this.addTableDetail(sb, gl4f, "4F 報廢明細");

        return sb.toString();
    }
}
