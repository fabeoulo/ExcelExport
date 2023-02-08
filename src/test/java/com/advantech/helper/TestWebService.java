/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.webservice.Factory;
import com.advantech.webservice.port.FqcKanBanQueryPort;
import com.advantech.webservice.port.QryWipAttQueryPort;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestWebService {

    @Autowired
    private FqcKanBanQueryPort kanbanPort;

    @Autowired
    private QryWipAttQueryPort modelNameQryPort;

//    @Test
    public void test1() throws Exception {

        List l = kanbanPort.query(Factory.TWM3);

        HibernateObjectPrinter.print(l);
    }

    @Test
    public void test2() throws Exception {

        String po = "THK002854Z22A";

        List l = modelNameQryPort.query(po, Factory.TWM3);

        HibernateObjectPrinter.print(l);
    }

}
