/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.helper.RequisitionListContainer;
import com.advantech.sap.SapMaterialInfo;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionEvent;
import com.advantech.model.db1.RequisitionEvent_;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.RequisitionState;
import com.advantech.model.db1.RequisitionState_;
import com.advantech.model.db1.RequisitionType;
import com.advantech.model.db1.Requisition_;
import com.advantech.model.db1.User;
import com.advantech.sap.SapQueryPort;
import com.advantech.service.db1.RequisitionEventService;
import com.advantech.service.db1.RequisitionReasonService;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db1.RequisitionStateService;
import com.advantech.service.db1.RequisitionTypeService;
import com.fasterxml.jackson.annotation.JsonView;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.advantech.sap.SapService;
import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.service.db1.FloorService;
import com.advantech.webservice.port.WareHourseInsertPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkState;
import com.mysql.cj.conf.PropertyKey;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Wei.Cheng
 */
@RestController
@RequestMapping("/RequisitionController")
public class RequisitionController {

    @Autowired
    private RequisitionService service;

    @Autowired
    private RequisitionEventService eventService;

    @Autowired
    private RequisitionReasonService requisitionReasonService;

    @Autowired
    private RequisitionTypeService requisitionTypeService;

    @Autowired
    private RequisitionStateService requisitionStateService;

    @Autowired
    private FloorService floorService;

    @Autowired
    private SapService sapService;

    @Autowired
    private WareHourseInsertPort whInsertPort;

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/findAll", method = {RequestMethod.POST})
    protected DataTablesOutput<Requisition> findAll(
            HttpServletRequest request,
            @Valid DataTablesInput input,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {

//        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
//        Floor floor = user.getFloor();
        if (startDate != null && endDate != null) {
            final Date sD = startDate.toDate();
            final Date eD = endDate.withHourOfDay(23).toDate();

            return service.findAll(input, (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
                Path<Date> dateEntryPath = root.get(Requisition_.createDate);
//                if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER")) {
                return cb.between(dateEntryPath, sD, eD);
//                } else {
//                    Join<Requisition, User> userJoin = root.join(Requisition_.user, JoinType.INNER);
//                    return cq.where(cb.and(cb.between(dateEntryPath, sD, eD), cb.equal(userJoin.get(User_.FLOOR), floor))).getRestriction();
//                }
            });
        } else {
//            if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER")) {
            return service.findAll(input);
//            } else {
//                return service.findAll(input, (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
//                    Join<Requisition, User> userJoin = root.join(Requisition_.user, JoinType.INNER);
//                    return cb.equal(userJoin.get(User_.FLOOR), floor);
//                });
//            }
        }

    }

    @ResponseBody
    @RequestMapping(value = "/insertEflow", method = {RequestMethod.POST})
    protected String insertEflow(@RequestParam String datas, @RequestParam String commitJobNo) throws Exception {

        //check state
        List<Requisition> l = new ObjectMapper().readValue(datas, new TypeReference<List<Requisition>>() {
        });
        l = l.stream().filter(t -> t.getRequisitionState().getId() == 4).collect(Collectors.toList());
        if (l.isEmpty()) {
            return "待領料數量0.";
        }

//        //check login
//        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
//        checkArgument(commitJobNo.equals(user.getJobnumber()), "User lost.請重新登入");//not handle error msg
        //check stock
        Map<String, BigDecimal> stockMap = sapService.getStockMap(l);
        String[] stockMsg = {""};
        List<Requisition> passList = new ArrayList<>();
        List<Requisition> lackList = new ArrayList<>();
        List<Requisition> noStockList = new ArrayList<>();
        l.forEach(t -> {
            String mat = t.getMaterialNumber();
            BigDecimal stock = stockMap.get(mat);
            if (stock != null && stock.compareTo(BigDecimal.ZERO) == 0) {
                noStockList.add(t);
            } else if (stock != null && stock.compareTo(BigDecimal.valueOf(Math.abs(t.getAmount()))) == -1) {
                lackList.add(t);
            } else {
                passList.add(t);
                return;
            }
            String remarkStock = " [庫存量：" + stock + "],";
            t.setRemark(t.getRemark() + remarkStock);
            stockMsg[0] += "料號：" + mat + remarkStock;
        });

        //insert WH
        String response = whInsertPort.insertWareHourse(passList, commitJobNo);
//        String response = "";//for quickly debug
        if (response.equals("")) {

            service.updateWithStateAndEvent(lackList, 4);
            service.updateWithStateAndEvent(passList, 5);
            service.updateWithStateAndEvent(noStockList, 2);

            if (stockMsg[0].isEmpty()) {
                return "success";
            } else {
                return "部份成功,有庫存不足. " + stockMsg[0];
            }
        }
        return "失敗 response:=" + response;
    }

    @ResponseBody
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    protected String save(@ModelAttribute Requisition requisition, @RequestParam(required = false) String remark, BindingResult bindingResult) throws Exception {

        bindingResult.getAllErrors().stream().map((object) -> {
            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;

                System.out.println(fieldError.getCode());
            }
            return object;
        }).filter((object) -> (object instanceof ObjectError)).map((object) -> (ObjectError) object).forEachOrdered((objectError) -> {
            System.out.println(objectError.getCode());
        });

        this.retrieveSapInfos(newArrayList(requisition));
        this.checkModelMaterial(newArrayList(requisition));

        service.save(requisition, remark);
        return "success";

    }

    public void checkModelMaterial(List<Requisition> requisitions) throws Exception {
        for (Requisition r : requisitions) {
            //Fail when sap info not retrieve from retrieveSapInfos() function
            checkArgument(r.getModelName() != null && !"".equals(r.getModelName()),
                    "Can't find material info '" + r.getMaterialNumber() + "' in po: " + r.getPo());
        }
    }

    public List<Requisition> retrieveSapInfos(List<Requisition> requisitions) throws JCoException, URISyntaxException {
        if (!requisitions.isEmpty()) {
            String po = requisitions.get(0).getPo();

            String[] materialNumbers = requisitions.stream().map(Requisition::getMaterialNumber).toArray(String[]::new);
            List<SapMaterialInfo> sapInfos = sapService.retrieveSapMaterialInfos(po, materialNumbers);

            if (sapInfos.isEmpty()) {
                return requisitions;
            }

            for (Requisition r : requisitions) {
                SapMaterialInfo info = sapInfos.stream()
                        .filter(s -> s.getMaterialNumber().equals(r.getMaterialNumber()))
                        .findFirst().orElse(null);
                if (info == null) {
                    continue;
                }
                r.setModelName(info.getModelName());
                r.setUnitPrice(info.getUnitPrice());
                r.setWerk(info.getWerk());
            }
        }
        return requisitions;
    }

    @ResponseBody
    @RequestMapping(value = "/retrieveSapInfos", method = {RequestMethod.GET, RequestMethod.POST})
    protected List<SapMaterialInfo> retrieveSapInfos(@RequestParam String po, @RequestParam(value = "materialNumbers[]") String[] materialNumbers) throws Exception {
        return this.sapService.retrieveSapMaterialInfos(po, materialNumbers);
    }

    @ResponseBody
    @RequestMapping(value = "/batchSave", method = {RequestMethod.POST})
    protected String batchSave(@ModelAttribute RequisitionListContainer container) throws Exception {
        List<Requisition> l = container.getMyList();
        l = this.retrieveSapInfos(l);
        this.checkModelMaterial(l);
        service.batchInsert(l);
        return "success";

    }

    @ResponseBody
    @RequestMapping(value = "/updateState", method = {RequestMethod.POST})
    protected String updateState(@RequestParam int requisition_id, @RequestParam int state_id,
            @RequestParam(required = false) String remark) {

        service.changeState(requisition_id, state_id);
        return "success";

    }

    @ResponseBody
    @RequestMapping(value = "/findEvent", method = {RequestMethod.POST})
    protected DataTablesOutput<RequisitionEvent> findEvent(@Valid DataTablesInput input, @RequestParam int requisition_id) {
        Requisition re = service.findById(requisition_id).get();
        return eventService.findAll(input, (Root<RequisitionEvent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Path<Integer> idEntryPath = root.get(RequisitionEvent_.REQUISITION);
            return cb.equal(idEntryPath, re);
        });
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionReasonOptions", method = {RequestMethod.GET})
    protected List<RequisitionReason> findRequisitionReasonOptions() {
        return requisitionReasonService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionStateOptions", method = {RequestMethod.GET})
    protected List<RequisitionState> findRequisitionStateOptions() {
        return requisitionStateService.findAll((Root<RequisitionState> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Path<Integer> idEntryPath = root.get(RequisitionState_.ID);
            return cb.not(idEntryPath.in(newArrayList(1, 3)));
        });
    }

    @ResponseBody
    @RequestMapping(value = "/findRequisitionTypeOptions", method = {RequestMethod.GET})
    protected List<RequisitionType> findRequisitionTypeOptions() {
        return requisitionTypeService.findAll();
    }

    @ResponseBody
    @RequestMapping(value = "/findFloorOptions", method = {RequestMethod.GET})
    protected List<Floor> findFloorOptions() {
        return floorService.findAll();
    }
}
