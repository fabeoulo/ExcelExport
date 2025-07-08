/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import java.util.Map;

/**
 *
 * @author Justin.Yeh
 */
public abstract class JobBase {
    
    protected boolean isServer() {
        String hostName = "";
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            hostName = env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            hostName = env.get("HOSTNAME");
        }

        return hostName.contains("IIS");
    }
}
