/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.listener;

import com.advantech.helper.ThreadLocalCleanUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Justin.Yeh
 */
public class QuartzContextListener implements ServletContextListener {

    //this listener is unused http://stackoverflow.com/questions/19573457/simple-example-for-quartz-2-2-and-tomcat-7
    //quartz only need to modify at web.xml & the quartz properties/xml to start, stop, wait
    private static final Logger log = LoggerFactory.getLogger(QuartzContextListener.class);

    public QuartzContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        try {
//            CronTrigMod.getInstance().unScheduleAllJob();

//            Endpoint.clearSessions();
//            Endpoint2.clearSessions();
//            Endpoint4.clearSessions();
            Thread.sleep(3000);
            ThreadLocalCleanUtil.clearThreadLocals();
        } catch (Exception e) {
            log.error(e.toString());
        }

        //web service當tomcat在做reload會有資源為釋放之情形(多次reload可能會memory leak)
        //http://timen-zbt.iteye.com/blog/1814795
        //安裝quartz後當tomcat重新啟動時會出現memory leak(http://www.cnblogs.com/leeying/p/3782102.html)
        //使用以上方法
    }
}
