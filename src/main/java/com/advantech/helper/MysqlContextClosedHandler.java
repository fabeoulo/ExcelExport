/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See https://stackoverflow.com/questions/3320400/to-prevent-a-memory-leak-the-jdbc-driver-has-been-forcibly-unregistered
 * https://www.panziye.com/java/4844.html
 * @author Justin.Yeh
 */
@WebListener
public class MysqlContextClosedHandler implements ServletContextListener {

    private final Logger log = LoggerFactory.getLogger(MysqlContextClosedHandler.class);
    
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
                    log.info("Deregistering JDBC driver {}", driver);
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    log.error("Error deregistering JDBC driver {}", driver, ex);
//                    event.getServletContext().log("Driver deregistration failure.", ex);
                }
            } else {
                // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
                log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
            }
        }

        // MySQL driver leaves around a thread. This static method cleans it up.
        try {
//                    log.info("Deregistering JDBC driver ");
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            // again failure, not much you can do
        }
    }

}