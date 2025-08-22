/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.controller.auth;

import com.advantech.api.model.FloorDto;
import com.advantech.api.model.RequisitionReasonDto;
import com.advantech.controller.RequisitionController;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.RequisitionReasonService;
import com.advantech.service.db1.RequisitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController; // Implicitly include ResponseBody

/**
 *
 * @author Justin.Yeh
 */
//@RestController
@RequestMapping("/ApiAuth/test")
public class TestApiAuthController {

    private final Logger logger = LoggerFactory.getLogger(TestApiAuthController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String msg = "";

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private FloorService floorService;

    @Autowired
    private RequisitionReasonService requisitionReasonService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    @Autowired
    private HttpSessionCsrfTokenRepository csrfTokenRepository;

//    @PostMapping("/authLogout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        boolean isAuth = false;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            auth.setAuthenticated(false);
//            SecurityContextHolder.getContext().setAuthentication(null);
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//            isAuth = !auth.isAuthenticated();
//        }
//        return String.valueOf(isAuth);
//    }
    @GetMapping("/getCsrfToken")
    public String getCsrfToken(@Autowired HttpServletRequest request) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        return csrfToken.getToken();
    }

    @GetMapping(value = "/getReasons")
    public List<RequisitionReasonDto> getReasons() {
        return requisitionReasonService.findAll().stream()
                .map(r -> new RequisitionReasonDto(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/testGetUtf8")
    public String testGetUtf8(@RequestParam String testString) {
        HibernateObjectPrinter.print(testString);
        return testString;
    }

    @PostMapping("/hello")
    public Map<String, String> sayHello() {

//        floors.forEach((u) -> {
//            logger.info("Name: {} id: {} \n", u.getName(), u.getId());
//        });
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, World!");
        return response; // 返回JSON格式的数据
    }
//
//    @PostMapping("/hello2")
//    public ResponseEntity<String> sayHello2(@RequestBody List<FloorDto> floors) {
//        try {
//            floors.forEach((u) -> {
//                logger.info("Name: {} id: {} \n", u.getName(), u.getId());
//            });
//            return ResponseEntity.ok("Hello, ");
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON data" + ex.getMessage());
//        }
//    }

}
