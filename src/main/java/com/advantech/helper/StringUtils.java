/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import java.text.NumberFormat;

/**
 *
 * @author Justin.Yeh
 */
public class StringUtils {

    private static final NumberFormat nf = NumberFormat.getInstance();

    public static String formatNumber(Object val) {
        String result;

        if (val == null) {
            return null;
        }

        try {
            double valD = Double.parseDouble(String.valueOf(val));
            result = nf.format(valD);
        } catch (NumberFormatException e) {
            result = val.toString();
        }

        return result;
    }
}
