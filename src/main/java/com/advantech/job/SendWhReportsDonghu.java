/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.job;

import com.advantech.model.db1.UserNotification;
import com.advantech.model.db3.WhReport;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author Justin.Yeh Send wh report for M3, M6
 */
@Component
public class SendWhReportsDonghu extends SendWhReports {

    private static final Logger logger = LoggerFactory.getLogger(SendWhReportsDonghu.class);

    private final List<String> plants = Lists.newArrayList("TWM3", "TWM6");

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected List<String> getPlants() {
        return plants;
    }

    @Override
    protected void sendMail() throws Exception {

        UserNotification notifi = notificationService.findById(6).get();
        UserNotification notifiCc = notificationService.findById(7).get();

        String[] mailTarget = findUsersMail(notifi);
        String[] mailCcTarget = findUsersMail(notifiCc);

        if (mailTarget.length == 0) {
            logger.info("Job sendReport can't find mail target in database table.");
            return;
        }

        DateTime now = new DateTime();
        String mailBody = generateMailBody(now);
        String mailTitle = fmt.print(now) + " - SAP產值/工時資料";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);

    }

    @Override
    public String generateMailBody(DateTime dt) throws IOException, SAXException, InvalidFormatException {

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
        sb.append("<h3>SAP產值/工時資料如下(");
        sb.append(fmt.print(dt));
        sb.append("):</h3>");

        super.setPlantAchievingMap();
                
        //Generate DailyWhReport table
        List<WhReport> daliyList = whReportService.findDailyWhReportWc(dt);
        sb.append("<h5>Daily report(7日)</h5>");
        addTable("日期", daliyList, sb);

        //Generate weekly table
        DateTime firstDateOfWeek = dt.withTime(0, 0, 0, 0).dayOfWeek().withMinimumValue();
        if (dt.toLocalDate().compareTo(new LocalDate(firstDateOfWeek)) == 0) {
            List<WhReport> weeklyList = whReportService.findWeeklyWhReportWc(dt);
            sb.append("<h5>Weekly report(4週)</h5>");
            addTable("週別", weeklyList, sb);
        }

        //Generate monthly table
        List<WhReport> monthlyList = whReportService.findMonthlyWhReportWc(dt);
        sb.append("<h5>Monthly report(當月累計)</h5>");
        addTable2("月份", dt, monthlyList, sb);

        return sb.toString();

    }
}
