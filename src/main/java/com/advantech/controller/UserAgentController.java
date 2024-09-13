/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.controller;

import com.advantech.model.db1.UserAgent;
import com.advantech.service.db1.UserAgentService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Justin.Yeh
 */
@RestController
@RequestMapping("/UserAgentController")
public class UserAgentController {

    @Autowired
    private UserAgentService userAgentService;

    @RequestMapping(value = "/findAll", method = {RequestMethod.GET})
    protected DataTablesOutput<UserAgent> findAll(
            HttpServletRequest request,
            @Valid DataTablesInput input) {

        return userAgentService.findAll(input);
    }

    @ResponseBody
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    protected String save(@Valid @ModelAttribute UserAgent pojo) {

        Optional<UserAgent> pojoInDb = userAgentService.findByBeginDate(pojo.getBeginDate());
        if (pojoInDb.isPresent()) {
            pojo.setId(pojoInDb.get().getId());
        }

        userAgentService.save(pojo);
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    protected String delete(@Valid @ModelAttribute UserAgent pojo) {
        userAgentService.delete(pojo);
        return "success";
    }
}
