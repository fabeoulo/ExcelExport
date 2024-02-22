/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.controller;

import com.advantech.model.db1.IECalendarLinkou;
import com.advantech.model.db1.IEWorkdayCalendar;
import com.advantech.service.db1.IECalendarLinkouService;
import com.advantech.service.db1.IEWorkdayCalendarService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@RequestMapping("/IECalendarController")
public class IECalendarController {

    private final String LOCAL_HOLIDAY = "Local Holiday", NOT_HOLIDAY = "Not Holiday";
    private final String SAVE_URL = "/save", DELETE_URL = "/delete";

    @Autowired
    private IECalendarLinkouService calendarLinkouService;

    @Autowired
    private IEWorkdayCalendarService workdayCalendarService;

    @RequestMapping(value = "/findAll", method = {RequestMethod.GET})
    protected DataTablesOutput<IECalendarLinkou> findAll(
            HttpServletRequest request,
            @Valid DataTablesInput input) {

        return calendarLinkouService.findAll(input);

    }

    @ResponseBody
    @RequestMapping(value = SAVE_URL, method = {RequestMethod.POST})
    protected String save(@Valid @ModelAttribute IECalendarLinkou pojo) {

        Optional<IECalendarLinkou> pojoInDb = calendarLinkouService.findByDateMark(pojo.getDateMark());
        if (pojoInDb.isPresent()) {
            pojo.setId(pojoInDb.get().getId());
        }

        String dateType = pojo.getDateName().equals(NOT_HOLIDAY) ? NOT_HOLIDAY : LOCAL_HOLIDAY;
        pojo.setDateType(dateType);
        calendarLinkouService.save(pojo);
        saveWorkdayCalendar(pojo, SAVE_URL);

        return "success";
    }

    @ResponseBody
    @RequestMapping(value = DELETE_URL, method = {RequestMethod.POST})
    protected String delete(@Valid @ModelAttribute IECalendarLinkou pojo) {

        calendarLinkouService.delete(pojo);
        saveWorkdayCalendar(pojo, DELETE_URL);

        return "success";
    }

    private void saveWorkdayCalendar(IECalendarLinkou pojo, @NotNull String action) {
        Optional<IEWorkdayCalendar> wdc = workdayCalendarService.findByDate(pojo.getDateMark());
        if (wdc.isPresent()) {
            IEWorkdayCalendar wd = wdc.get();
            String remark;
            int workdayFlag;

            if (!action.equals(DELETE_URL)) {
                remark = pojo.getDateName();
                workdayFlag = remark.equals(NOT_HOLIDAY) ? 1 : 0;
            } else {
                remark = "";
                workdayFlag = wd.getDayOfWeek() > 5 ? 0 : 1;
            }

            wd.setRemark(remark);
            wd.setWorkdayFlag(workdayFlag);
            workdayCalendarService.save(wd);
        }
    }
}
