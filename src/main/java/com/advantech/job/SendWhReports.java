/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.job;

import com.advantech.helper.MailManager;
import com.advantech.helper.WorkingDayUtils;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.model.db1.WorkingHoursReport;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.service.db1.WorkingHoursService;
import static com.google.common.base.Preconditions.checkState;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class SendWhReports {

    private static final Logger logger = LoggerFactory.getLogger(SendReport.class);

    @Autowired
    protected MailManager manager;

    @Autowired
    protected UserNotificationService notificationService;

    @Autowired
    protected WorkingHoursService whService;

    @Autowired
    protected UserService userService;

    protected final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");

    protected final DecimalFormat df = new DecimalFormat("$#,##0");

    protected final DecimalFormat df2 = new DecimalFormat("#.##%");

    public void execute() {
//        sendMail();
    }

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

    public void testSendMail(int testTargetUserId, DateTime specDate) throws Exception {

        User user = userService.findById(testTargetUserId).orElseGet(null);

        checkState(user != null, "User not found.");

        String[] mailTarget = {user.getEmail()};
        String[] mailCcTarget = {};

        checkState(mailTarget.length != 0, "Job sendReport can't find mail target in database table.");

        String mailBody = generateMailBody(specDate);
        String mailTitle = fmt.print(specDate) + " - SAP產值/工時資料";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);

    }
    
    public void testSendMail2(int testTargetUserId, DateTime specDate) throws Exception {

        User user = userService.findById(testTargetUserId).orElseGet(null);

        checkState(user != null, "User not found.");

        String[] mailTarget = {user.getEmail()};
        String[] mailCcTarget = {};

        checkState(mailTarget.length != 0, "Job sendReport can't find mail target in database table.");

        String mailBody = "generateMailBody";
        String mailTitle = fmt.print(specDate) + " - SAP產值/工時資料";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);

    }

    protected String[] findUsersMail(UserNotification notifi) {
        List<User> l = userService.findByUserNotifications(notifi);
        return l.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
    }

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
        sb.append(".rightAlign {text-align:right;}");
        sb.append(".total {font-weight: bold;}");
        sb.append("</style>");
        sb.append("<div id='mailBody'>");
        sb.append("<h3>Dear User:</h3>");
        sb.append("<h3>SAP產值/工時資料如下(");
        sb.append(fmt.print(dt));
        sb.append("):</h3>");

        //Generate DailyWhReport table, send mail when friday
        List<WorkingHoursReport> daliyList = whService.findDailyWhReport(dt);

        sb.append("<h5>Daily report(7日)</h5>");

        addTable("日期", daliyList, sb);

        //Generate weekly table
        DateTime firstDateOfWeek = dt.withTime(0, 0, 0, 0).dayOfWeek().withMinimumValue();
        if (dt.toLocalDate().compareTo(new LocalDate(firstDateOfWeek)) == 0) {
            List weeklyList = whService.findWeeklyWhReport(dt);
            sb.append("<h5>Weekly report(4週)</h5>");
            addTable("週別", weeklyList, sb);
        }

        //Generate monthly table
        List monthlyList = whService.findMonthlyWhReport(dt);
        sb.append("<h5>Monthly report(當月累計)</h5>");
        addTable2("月份", dt, monthlyList, sb);

        return sb.toString();

    }

    protected void addTable(String dateTitleName, List<WorkingHoursReport> l, StringBuilder sb) {
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append(dateTitleName);
        sb.append("</th>");
        sb.append("<th>Quantity</th>");
        sb.append("<th>SAP工時</th>");
        sb.append("<th>SAP產值</th>");
        sb.append("<th>廠別</th>");
        sb.append("</tr>");

        int totalQuantity = 0;
        BigDecimal totalSapWorktime = BigDecimal.ZERO, totalSapOutputValue = BigDecimal.ZERO;

        for (WorkingHoursReport whr : l) {
            totalQuantity = totalQuantity + whr.getQuantity();
            totalSapWorktime = totalSapWorktime.add(whr.getSapWorktime());

            BigDecimal outputValue = cutOutDigits(whr.getSapOutputValue());
            totalSapOutputValue = totalSapOutputValue.add(outputValue);

            if ("TWM3".equals(whr.getPlant())) {
                sb.append("<tr class='m3'>");
            } else {
                sb.append("<tr>");
            }
            sb.append("<td>");
            sb.append(whr.getDateField());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getQuantity());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getSapWorktime());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(outputValue));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(whr.getPlant());
            sb.append("</td>");
            sb.append("</tr>");
        }

        sb.append("<tr class='total'>");
        sb.append("<td>");
        sb.append("Total:");
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(totalQuantity);
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(totalSapWorktime);
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df.format(totalSapOutputValue));
        sb.append("</td>");
        sb.append("<td>");
        sb.append("");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("</table>");
        sb.append("<hr />");
    }

    protected void addTable2(String dateTitleName, DateTime dt, List<WorkingHoursReport> l, StringBuilder sb) {
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append(dateTitleName);
        sb.append("</th>");
        sb.append("<th>Quantity</th>");
        sb.append("<th>SAP工時</th>");
        sb.append("<th>SAP產值</th>");
        sb.append("<th>本月工時預估</th>");
        sb.append("<th>本月產值預估</th>");
        sb.append("<th>工時累計達成率</th>");
        sb.append("<th>產值累計達成率</th>");
        sb.append("<th>累計天數比例</th>");
        sb.append("<th>廠別</th>");
        sb.append("</tr>");

        int totalQuantity = 0;
        BigDecimal totalSapWorktime = BigDecimal.ZERO,
                totalSapOutputValue = BigDecimal.ZERO,
                totalWorktimeEstimated = BigDecimal.ZERO,
                totalOutputValueEstimated = BigDecimal.ZERO;
        
        DateTime prevBusinessDay = WorkingDayUtils.findLastBusinessDay(dt.minusDays(1));
        double datePercentage = WorkingDayUtils.findBusinessDayPercentage(prevBusinessDay);

        for (WorkingHoursReport whr : l) {
            totalQuantity = totalQuantity + whr.getQuantity();
            totalSapWorktime = totalSapWorktime.add(whr.getSapWorktime());
            totalWorktimeEstimated = totalWorktimeEstimated.add(whr.getWorktimeEstimated());
            totalOutputValueEstimated = totalOutputValueEstimated.add(whr.getOutputValueEstimated());

            BigDecimal outputValue = cutOutDigits(whr.getSapOutputValue());
            totalSapOutputValue = totalSapOutputValue.add(outputValue);

            sb.append("<tr>");
            sb.append("<td>");
            sb.append(whr.getDateField());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getQuantity());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getSapWorktime());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(outputValue));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getWorktimeEstimated());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(whr.getOutputValueEstimated()));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(whr.getSapWorktime().divide(whr.getWorktimeEstimated(), 4, BigDecimal.ROUND_HALF_EVEN)));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(whr.getSapOutputValue().divide(whr.getOutputValueEstimated(), 4, BigDecimal.ROUND_HALF_EVEN)));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(datePercentage));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(whr.getPlant());
            sb.append("</td>");
            sb.append("</tr>");
        }

        sb.append("<tr class='total'>");
        sb.append("<td>");
        sb.append("Total:");
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(totalQuantity);
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(totalSapWorktime);
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df.format(totalSapOutputValue));
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(totalWorktimeEstimated);
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df.format(totalOutputValueEstimated));
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df2.format(totalSapWorktime.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : totalSapWorktime.divide(totalWorktimeEstimated, 4, BigDecimal.ROUND_HALF_EVEN)));
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df2.format(totalSapOutputValue.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : totalSapOutputValue.divide(totalOutputValueEstimated, 4, BigDecimal.ROUND_HALF_EVEN)));
        sb.append("</td>");
        sb.append("<td class='rightAlign'>");
        sb.append(df2.format(datePercentage));
        sb.append("</td>");
        sb.append("<td>");
        sb.append("");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("</table>");
        sb.append("<hr />");
    }

    private BigDecimal cutOutDigits(BigDecimal bd) {
        return bd.subtract(bd.remainder(new BigDecimal(10)));
    }
}
