/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import io.netty.util.internal.InternalThreadLocalMap;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * See
 * https://stackoverflow.com/questions/3320400/to-prevent-a-memory-leak-the-jdbc-driver-has-been-forcibly-unregistered
 * https://www.panziye.com/java/4844.html
 *
 * @author Justin.Yeh
 */
@WebListener
public class DriverContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // nothing to do
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Now deregister JDBC drivers in this context's ClassLoader:
        // Get the webapp's ClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // Loop through all drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                // This driver was registered by the webapp's ClassLoader, so deregister it:
                try {
                    DriverManager.deregisterDriver(driver);
                    event.getServletContext().log("Deregistering JDBC driver " + driver);
                } catch (SQLException ex) {
                    event.getServletContext().log("Driver deregistration failure.", ex);
//                    event.getServletContext().log("Driver deregistration failure.", ex);
                }
            } else {
                // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
                event.getServletContext().log("Not deregistering JDBC driver " + driver + " as it does not belong to this webapp's ClassLoader");
            }
        }

        // MySQL driver leaves around a thread. This static method cleans it up.
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            event.getServletContext().log("Abandoned Connection Cleanup checkedShutdown.");
        } catch (Exception e) {
            // again failure, not much you can do
            event.getServletContext().log("Abandoned Connection Cleanup failure.", e);
        }

        // fix memory leak
        InternalThreadLocalMap.destroy();
    }

}
