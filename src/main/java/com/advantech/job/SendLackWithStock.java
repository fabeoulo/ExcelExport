/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db2.Items;
import com.advantech.model.db2.Orders;
import com.advantech.sap.SapService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db2.OrdersService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
public class SendLackWithStock extends SendEmailBase {

    private static final Logger logger = LoggerFactory.getLogger(SendLackWithStock.class);

    @Autowired
    private SapService sapService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RequisitionService rservice;

    private List<Orders> checkedOrders = Arrays.asList();
    private List<Requisition> checkedReq = Arrays.asList();

    protected final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");
    protected final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/M/d HH:mm:ss");

    public void execute() {
        try {
            findCheckedLack();
            if (!checkedOrders.isEmpty()) {
                this.sendMail();
            }
        } catch (Exception ex) {
            logger.error("Send mail fail.", ex);
        }
    }

    private void findCheckedLack() throws Exception {
        //M9_4F
        Integer teamId = 11;
        List<Orders> lackOrders = ordersService.findAllLackWithUserItem(teamId);//order by id
        if (lackOrders.isEmpty()) {
            return;
        }

        List<Requisition> lackReq = getReqFromLack(lackOrders, Arrays.asList());
        Map<String, BigDecimal> stockMap = sapService.getStockMapWithGoodLgort(lackReq);

        //FIFO
        List<Orders> checkedO = lackOrders.stream().filter(o -> {
            Items firstItem = o.getItemses().stream().findFirst().orElse(null);
            if (firstItem != null && stockMap.containsKey(firstItem.getLabel3())) {
                String key = firstItem.getLabel3();
                BigDecimal stock = stockMap.get(key);
                BigDecimal require = new BigDecimal(o.getNumber());
                BigDecimal restStock = stock.subtract(require);
                if (restStock.compareTo(new BigDecimal(0)) >= 0) {
                    stockMap.put(key, restStock);
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        checkedOrders = checkedO;
        checkedReq = getReqFromLack(checkedO, lackReq);
    }

    private List<Requisition> getReqFromLack(List<Orders> lackOrders, List<Requisition> lackReq) {
        if (lackOrders.isEmpty()) {
            return Arrays.asList();
        }
        List<Integer> listInt = lackOrders.stream().map(l -> l.getRequisionId()).collect(Collectors.toList());

        return lackReq.isEmpty() ? rservice.findAllByIdWithUserAndState(listInt)
                : lackReq.stream().filter(r -> listInt.contains(r.getId())).collect(Collectors.toList());
    }

    protected void sendMail() throws Exception {

        String[] mailTarget = findEMailFromLack();
        String[] mailCcTarget = findEMailByNotifyId(11);

        if (mailTarget.length == 0) {
            logger.info("Job SendLackWithStock can't find mail target in database table.");
            return;
        }

        DateTime now = new DateTime();
        String mailBody = generateMailBody();
        String mailTitle = fmt.print(now) + " - 缺料有良品提醒";

        manager.sendMail(mailTarget, mailCcTarget, mailTitle, mailBody);
    }

    private String[] findEMailFromLack() {
        Set<String> lackMails = checkedOrders.stream().map(l -> l.getUsers().getMail())
                .filter(e -> e != null && !e.trim().equals("")).collect(Collectors.toSet());
        Set<String> reqMails = checkedReq.stream().map(r -> r.getUser().getEmail())
                .filter(e -> e != null && !e.trim().equals("")).collect(Collectors.toSet());
        lackMails.addAll(reqMails);
        return lackMails.stream().toArray(size -> new String[size]);
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
        sb.append("<h3>缺料有良品到料如下:</h3>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>日期</th>");
        sb.append("<th>工單</th>");
        sb.append("<th>機種</th>");
        sb.append("<th>料號</th>");
        sb.append("<th>數量</th>");
        sb.append("<th>填寫人</th>");
        sb.append("<th>不良敘述</th>");
        sb.append("</tr>");

        for (Orders order : checkedOrders) {
            Items firstItem = order.getItemses().stream().findFirst().orElse(null);
            if (firstItem != null) {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append(fmtD.print(order.getTimeOpen().getTime()));
                sb.append("</td>");
                sb.append("<td>");
                sb.append(firstItem.getLabel1());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(firstItem.getLabel2());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(firstItem.getLabel3());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(order.getNumber());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(order.getUsers().getName());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(order.getComment());
                sb.append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table>");

        return sb.toString();
    }

}
