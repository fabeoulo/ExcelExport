/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webscraper;

import com.advantech.webscraper.model.EzCalendar;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Justin.Yeh
 */
public class EzScraperClient {

    private static final Logger log = LoggerFactory.getLogger(EzScraperClient.class);

    private String baseUrl;

    private String userName;

    private String password;

    private CloseableHttpClient client;

    private final ObjectMapper mapper = new ObjectMapper();

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void loginAndSetClient() throws Exception {

        CookieStore cookieStore = new BasicCookieStore();

        client = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        String uri = "/net/Account/Login";

        // Step 1: GET login page
        HttpGet get = new HttpGet(baseUrl + uri);

        CloseableHttpResponse getResp = client.execute(get);
        String html = EntityUtils.toString(getResp.getEntity(), StandardCharsets.UTF_8);

//        // Step 2: parse token
//        Document doc = Jsoup.parse(html);
//        String token = doc.select("input[name=UserName]").val();
//
//        System.out.println("Token: " + token);
//
        // Step 3: POST login
        HttpPost post = new HttpPost(baseUrl + uri);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("UserName", userName));
        params.add(new BasicNameValuePair("Password", password));
//        params.add(new BasicNameValuePair("ReturnUrl", ""));

        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//        // 模擬瀏覽器
//        post.setHeader("User-Agent", "Mozilla/5.0");
//        post.setHeader("Referer", "https://employeezone.com.tw/net/Account/Login");
//
        CloseableHttpResponse postResp = client.execute(post);

//        System.out.println("Login status: " + postResp.getStatusLine());
    }

    public List<EzCalendar> getCalendarInfo(int year, int month) throws Exception {

        String uri = "/net/Home/GetCalendarInfo?year=" + year + "&month=" + month;

        HttpGet get = new HttpGet(baseUrl + uri);

        CloseableHttpResponse getResp = client.execute(get);
        String json = EntityUtils.toString(getResp.getEntity(), StandardCharsets.UTF_8);

        List<EzCalendar> l = mapper.readValue(json, new TypeReference<List<EzCalendar>>() {
        });

        return l;
    }
}
