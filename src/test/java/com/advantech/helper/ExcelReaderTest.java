/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import java.util.List;
import static org.junit.Assert.*;
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
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelReaderTest {

    @Autowired
    private ExcelDataTransformer t;

    @Test
    public void testRead() throws Exception {
        List list1 = t.getFloorFiveExcelData();
        assertTrue(!list1.isEmpty());

        for (int i = 1; i <= 10; i++) {
            HibernateObjectPrinter.print(list1.get(list1.size() - i));
        }
        HibernateObjectPrinter.print(list1.get(0));

        assertEquals(521, list1.size());

    }
}
