/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import java.util.Random;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 *
 * @author Wei.Cheng
 */
public class TestClass {

//    @Test
    public void testDerCool() {
        new Random().ints(1, 50)
                .distinct()
                .limit(12)
                .boxed()
                .sorted()
                .collect(Collectors.toList())
                .forEach(i -> customP(i));
    }

    public void customP(Integer i) {
        System.out.printf("Number: %d\n", i);
    }

    @Test
    public void testDateTime() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/M/d");

        DateTime d = new DateTime();

        DateTime sDOW = d.minusDays(d.getDayOfWeek() - 1);
        DateTime eDOW = sDOW.plusDays(7 - 1);

        System.out.println(fmt.print(sDOW));
        System.out.println(fmt.print(eDOW));
    }
}
