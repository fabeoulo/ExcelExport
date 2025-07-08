/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.helper.MailManager;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserNotification;
import com.advantech.service.db1.UserNotificationService;
import com.advantech.service.db1.UserService;
import com.advantech.webapi.EmailApiClient;
import com.advantech.webapi.model.EmailModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Justin.Yeh
 */
public abstract class SendEmailBase extends JobBase {

    @Autowired
    protected MailManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    protected EmailApiClient emailApiClient;

    protected boolean sendByApi(String[] toAddresses, String[] ccAddresses, String subject, String body) {
        return sendByApi(toAddresses, ccAddresses, subject, body, null);
    }

    protected boolean sendByApi(String[] toAddresses, String[] ccAddresses, String subject, String body, String setFromAddress) {
        if (super.isServer()) {
            EmailModel emailModel = new EmailModel(toAddresses, ccAddresses, subject, body, setFromAddress);
            return emailApiClient.sendEmail(emailModel);
        }
        return false;
    }

    protected String[] findEmailByNotifyId(Integer id) {
        UserNotification notifi = notificationService.findById(id).get();
        List<User> l = userService.findByUserNotifications(notifi);
        return l.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
    }
}
