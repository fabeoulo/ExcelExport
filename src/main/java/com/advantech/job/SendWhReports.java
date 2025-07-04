/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.job;

import com.advantech.helper.MailManager;
import com.advantech.helper.WorkingDayUtils;
import com.advantech.model.db1.Achieving;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.model.db3.WhReport;
import com.advantech.service.db1.AchievingService;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.service.db3.WhReportService;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author Justin.Yeh
 */
@Component
public abstract class SendWhReports {

    @Autowired
    protected MailManager manager;

    @Autowired
    protected UserNotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkingDayUtils workingDayUtils;

    @Autowired
    private AchievingService achievingService;

    @Autowired
    protected WhReportService whReportService;

    protected final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");

    protected final DecimalFormat df = new DecimalFormat("$#,##0");

    protected final DecimalFormat df2 = new DecimalFormat("#.##%");

    protected Map<String, Achieving> mAchieving;

    public void execute() {
        try {
            this.sendMail();
        } catch (Exception ex) {
            getLogger().error("Send mail fail.", ex);
        }
    }

    protected abstract Logger getLogger();

    protected abstract void sendMail() throws Exception;

    public void testSendMail(int testTargetUserId, DateTime specDate) throws Exception {

        UserNotification notifi = notificationService.findById(6).get();
        UserNotification notifiCc = notificationService.findById(7).get();

        String[] mailTarget = findUsersMail(notifi);
        String[] mailCcTarget = findUsersMail(notifiCc);

        if (mailTarget.length == 0) {
            getLogger().info("Job sendReport can't find mail target in database table.");
            return;
        }

        DateTime now = new DateTime();
        String mailBody = generateMailBody(now);
        String mailTitle = fmt.print(now) + " - SAP產值/工時資料";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);

    }

    protected String[] findUsersMail(UserNotification notifi) {
        List<User> l = userService.findByUserNotifications(notifi);
        return l.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
    }

    protected void setPlantAchievingMap() {
        mAchieving = achievingService.findAll().stream()
                .sorted(Comparator.comparing(Achieving::getSapProductionType))
                .collect(
                        Collectors.toMap(
                                Achieving::getFactory,
                                item -> item,
                                (f, s) -> f,
                                LinkedHashMap::new)
                );
    }

    protected abstract String generateMailBody(DateTime dt) throws IOException, SAXException, InvalidFormatException;

    protected void addTable(String dateTitleName, List<WhReport> l, StringBuilder sb) {
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append(dateTitleName);
        sb.append("</th>");
        sb.append("<th>Quantity</th>");
        sb.append("<th>SAP工時</th>");
        sb.append("<th>SAP實際產值</th>");
        sb.append("<th>部別</th>");
        sb.append("</tr>");

        int totalQuantity = 0;
        BigDecimal totalSapWorktime = BigDecimal.ZERO, totalSapOutputValue = BigDecimal.ZERO;
        List<String> plants = Lists.newArrayList(mAchieving.keySet());

        for (WhReport whr : l) {
            Achieving ach = mAchieving.getOrDefault(whr.getPlant(), new Achieving());
            int plantIndex = plants.indexOf(whr.getPlant());

            totalQuantity = totalQuantity + whr.getQuantityNotNull();
            totalSapWorktime = totalSapWorktime.add(whr.getSapWorktimeScaled());

            BigDecimal outputValue = whr.getSapOutputValueCutDigits();
            totalSapOutputValue = totalSapOutputValue.add(outputValue);

            if (plantIndex % 2 == 0) {
                sb.append("<tr class='m3'>");
            } else {
                sb.append("<tr>");
            }
            sb.append("<td>");
            sb.append(whr.getDateField());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getQuantityNotNull());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getSapWorktimeScaled());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(outputValue));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(ach.getSapProductionType());
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

    protected void addTable2(String dateTitleName, DateTime dt, List<WhReport> l, StringBuilder sb) {
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append(dateTitleName);
        sb.append("</th>");
        sb.append("<th>Quantity</th>");
        sb.append("<th>SAP工時</th>");
        sb.append("<th>SAP實際產值</th>");
        sb.append("<th>本月工時預估</th>");
        sb.append("<th>本月產值預估</th>");
        sb.append("<th>工時累計達成率</th>");
        sb.append("<th>產值累計達成率</th>");
        sb.append("<th>工作日累計比例</th>");
        sb.append("<th>部別</th>");
        sb.append("</tr>");

        int totalQuantity = 0;
        BigDecimal totalSapWorktime = BigDecimal.ZERO,
                totalSapOutputValue = BigDecimal.ZERO,
                totalWorktimeEstimated = BigDecimal.ZERO,
                totalOutputValueEstimated = BigDecimal.ZERO;

        double datePercentage = workingDayUtils.findBusinessDayPercentageByDb(dt);

        for (WhReport whr : l) {
            Achieving ach = mAchieving.getOrDefault(whr.getPlant(), new Achieving());

            totalQuantity = totalQuantity + whr.getQuantityNotNull();
            totalSapWorktime = totalSapWorktime.add(whr.getSapWorktimeScaled());
            totalWorktimeEstimated = totalWorktimeEstimated.add(ach.getWorktimeEstimated());
            totalOutputValueEstimated = totalOutputValueEstimated.add(ach.getOutputValueEstimated());

            BigDecimal outputValue = whr.getSapOutputValueCutDigits();
            totalSapOutputValue = totalSapOutputValue.add(outputValue);

            sb.append("<tr>");
            sb.append("<td>");
            sb.append(whr.getDateField());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getQuantityNotNull());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(whr.getSapWorktimeScaled());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(outputValue));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(ach.getWorktimeEstimated());
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df.format(ach.getOutputValueEstimated()));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(whr.getSapWorktimeScaled().divide(ach.getWorktimeEstimated(), 4, BigDecimal.ROUND_HALF_EVEN)));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(whr.getSapOutputValueCutDigits().divide(ach.getOutputValueEstimated(), 4, BigDecimal.ROUND_HALF_EVEN)));
            sb.append("</td>");
            sb.append("<td class='rightAlign'>");
            sb.append(df2.format(datePercentage));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(ach.getSapProductionType());
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
        sb.append(df2.format(l.isEmpty() ? 0 : datePercentage));
        sb.append("</td>");
        sb.append("<td>");
        sb.append("");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("</table>");
        sb.append("<hr />");
    }
}
