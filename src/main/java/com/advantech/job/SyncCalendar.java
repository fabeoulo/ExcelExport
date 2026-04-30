/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.controller.IECalendarController;
import com.advantech.model.db1.IECalendarLinkou;
import com.advantech.webscraper.EzScraperClient;
import com.advantech.webscraper.model.EzCalendar;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class SyncCalendar extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(SyncCalendar.class);

    @Autowired
    private EzScraperClient ezScraperClient;

    @Autowired
    private IECalendarController iECalendarController;

    private final int months = 12;
    private final String HOLIDAY = "Holiday", NOT_HOLIDAY = "Not Holiday";
    private List<String> result;

    public void execute() {
        List<EzCalendar> l = getEzCalendar();
        saveEzCalendar(l);

        sendMail();
    }

    private List<EzCalendar> getEzCalendar() {
        DateTime now = DateTime.now();
        List<EzCalendar> la = new ArrayList<>();
        try {
            ezScraperClient.loginAndSetClient();
            for (int i = 1; i <= months; i++) {
                now = now.plusMonths(1);
                int y = now.year().get();
                int m = now.monthOfYear().get();

                List<EzCalendar> temp = ezScraperClient.getCalendarInfo(y, m);
                la.addAll(temp);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return la;
    }

    private List<String> saveEzCalendar(List<EzCalendar> l) {
        result = new ArrayList<>();
        for (EzCalendar ezCalendar : l) {
            String classify = ezCalendar.getTitle().equals(HOLIDAY) ? HOLIDAY : NOT_HOLIDAY;

            IECalendarLinkou pojo = new IECalendarLinkou();
            pojo.setDateMark(ezCalendar.getDate());
            pojo.setDateName(classify);

            result.add(iECalendarController.save(pojo));
        }
        return result;
    }

    private void sendMail() {
        try {
            String[] mailTarget = findEmailByNotifyId(18);
            String[] mailCcTarget = findEmailByNotifyId(18);

            if (mailTarget.length == 0) {
                logger.info("SendScrapped can't find mail target.");
                return;
            }

            String mailBody = generateBody();
            String mailTitle = "同步EZ行事曆(" + months + "個月)";
            String mailSenderName = "領退料平台";

            manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody, mailSenderName);

        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    public String generateBody() {//throws IOException, SAXException, InvalidFormatException {

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
        sb.append("<h3>同步EZ行事曆(");
        sb.append(months);
        sb.append("個月)執行結果:</h3>");

        for (String r : result) {
            sb.append(r);
            sb.append("<br>");
        }

        return sb.toString();
    }

}
