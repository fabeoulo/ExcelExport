/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import com.advantech.converter.Encodeable;
import java.util.HashMap;
import java.util.Map;

/**
 * 不只webservice在用
 *
 * @author Wei.Cheng 假如要用其他廠區的話，Factory必須吻合DB設定
 */
public enum Factory implements Encodeable {

    TWM2("M2"),
    TWM3("M3"),
    TWM6("M6"),
    TWM8("M8"),
    TWM9("M9"),
    M9WH("WH");

    private final String s;
    private static final Map<String, Factory> map = new HashMap<>();//getEnum("PD03") return TWM6

    static {
        for (Factory f : Factory.values()) {
            map.put(f.s, f);
        }
    }

    private Factory(final String s) {
        this.s = s;
    }

    @Override
    public Object token() {
        return this.s;
    }

    @Override
    public String toString() {
        return this.s;
    }

    public static Factory getEnum(String t) {
        Factory f = map.get(t);
        if (f != null) {
            return f;
        }
        throw new IllegalArgumentException("Can't find enum with value " + t);
    }

    public String getName() {
        return super.toString();
    }
}
