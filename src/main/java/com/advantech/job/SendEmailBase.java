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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public abstract class SendEmailBase {
    
    @Autowired
    protected MailManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserNotificationService notificationService;
    
    
    protected String[] findEMailByNotifyId(Integer id) {
        UserNotification notifi = notificationService.findById(id).get();
        List<User> l = userService.findByUserNotifications(notifi);
        return l.stream().map(u -> u.getEmail()).toArray(size -> new String[size]);
    }
}
