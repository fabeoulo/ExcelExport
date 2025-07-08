/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webapi.model;

/**
 *
 * @author Justin.Yeh
 */
public class EmailModel {

    private String[] toAddresses;

    private String[] ccAddresses;

    private String[] bccAddresses;

    private String setFromAddress;

    private String subject;

    private String body;

    private String template;

    private boolean isHtml = true;

    public EmailModel() {
    }

    public EmailModel(String[] toAddresses, String[] ccAddresses, String subject, String body, String setFromAddress) {
        this.toAddresses = toAddresses;
        this.ccAddresses = ccAddresses;
        this.subject = subject;
        this.body = body;
        this.setFromAddress = setFromAddress;
    }

    public String[] getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String[] toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String[] getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(String[] ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    public String[] getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(String[] bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    public String getSetFromAddress() {
        return setFromAddress;
    }

    public void setSetFromAddress(String setFromAddress) {
        this.setFromAddress = setFromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isIsHtml() {
        return isHtml;
    }

    public void setIsHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }

}
