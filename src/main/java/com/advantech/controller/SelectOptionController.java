/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.controller;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.RequisitionCateIms;
import com.advantech.model.db1.RequisitionCateMes;
import com.advantech.model.db1.RequisitionFlow;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.RequisitionState;
import com.advantech.model.db1.RequisitionType;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.RequisitionCateImsService;
import com.advantech.service.db1.RequisitionCateMesService;
import com.advantech.service.db1.RequisitionFlowService;
import com.advantech.service.db1.RequisitionReasonService;
import com.advantech.service.db1.RequisitionStateService;
import com.advantech.service.db1.RequisitionTypeService;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Justin.Yeh
 */
@RestController
@RequestMapping("/SelectOption")
public class SelectOptionController {

    @Autowired
    private RequisitionReasonService requisitionReasonService;

    @Autowired
    private RequisitionTypeService requisitionTypeService;

    @Autowired
    private RequisitionStateService requisitionStateService;

    @Autowired
    private RequisitionFlowService requisitionFlowService;

    @Autowired
    private RequisitionCateImsService requisitionCateImsService;

    @Autowired
    private RequisitionCateMesService requisitionCateMesService;

    @Autowired
    private FloorService floorService;

    @ResponseBody
    @RequestMapping(value = "/findRequisitionReasonOptions", method = {RequestMethod.GET})
    public List<RequisitionReason> findRequisitionReasonOptions() {
        return requisitionReasonService.findAllByFlag(1);
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionStateOptions", method = {RequestMethod.GET})
    protected List<RequisitionState> findRequisitionStateOptions() {
        List<Integer> ids = newArrayList(2, 4, 5, 6, 7, 8);
        List<RequisitionState> states = requisitionStateService.findAll();
        return states.stream().filter(f -> ids.contains(f.getId())).collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionTypeOptions", method = {RequestMethod.GET})
    protected List<RequisitionType> findRequisitionTypeOptions() {
        return requisitionTypeService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionFlowOptions", method = {RequestMethod.GET})
    protected List<RequisitionFlow> findRequisitionFlowOptions() {
        return requisitionFlowService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findFloorOptions", method = {RequestMethod.GET})
    public List<Floor> findFloorOptions() {
        return floorService.findAllEnableState();
    }

    @ResponseBody
    @RequestMapping(value = "/findUrgentOptions", method = {RequestMethod.GET})
    public List findUrgentOptions() {
        List l = new ArrayList();
        List<String> datas = newArrayList("", "Y");

        for (int i = 0; i < datas.size(); i++) {
            HashMap<String, Object> map = new HashMap();
            map.put("id", i);
            map.put("name", datas.get(i));

            l.add(map);
        }

        return l;
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionCateImsOptions", method = {RequestMethod.GET})
    protected List<RequisitionCateIms> findRequisitionCateImsOptions() {
        return requisitionCateImsService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionCateMesOptions", method = {RequestMethod.GET})
    protected List<RequisitionCateMes> findRequisitionCateMesOptions() {
        return requisitionCateMesService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionCateImsRef", method = {RequestMethod.GET})
    protected List<RequisitionCateIms> findRequisitionCateImsRef() {
        return requisitionCateImsService.findAllWithFloor();
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionCateMesRef", method = {RequestMethod.GET})
    protected List<RequisitionCateMes> findRequisitionCateMesRef() {
        return requisitionCateMesService.findAllWithCateIms();
    }

}
