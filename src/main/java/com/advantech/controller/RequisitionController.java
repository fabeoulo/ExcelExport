/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.helper.RequisitionListContainer;
import com.advantech.sap.SapMaterialInfo;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Floor_;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionEvent;
import com.advantech.model.db1.RequisitionEvent_;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.Requisition_;
import com.advantech.model.db1.User;
import com.advantech.model.db1.User_;
import com.advantech.service.db1.RequisitionEventService;
import com.advantech.service.db1.RequisitionReasonService;
import com.advantech.service.db1.RequisitionService;
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
import com.advantech.trigger.RequisitionStateChangeTrigger;
import com.advantech.webservice.WareHourseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkState;
import com.sap.conn.jco.JCoException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private SapService sapService;

    @Autowired
    private WareHourseService wareHourseService;

    @Autowired
    private RequisitionStateChangeTrigger trigger;

// <editor-fold desc="findAll Original">
//    @JsonView(DataTablesOutput.View.class)
//    @RequestMapping(value = "/findAllOg", method = {RequestMethod.POST})
//    protected DataTablesOutput<Requisition> findAll(
//            HttpServletRequest request,
//            @Valid DataTablesInput input,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {
//
////        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
////        Floor floor = user.getFloor();
//        if (startDate != null && endDate != null) {
//            final Date sD = startDate.toDate();
//            final Date eD = endDate.withHourOfDay(23).toDate();
//
//            return service.findAll(input, (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
//                Path<Date> dateEntryPath = root.get(Requisition_.createDate);
////                if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER")) {
//                return cb.between(dateEntryPath, sD, eD);
////                } else {
////                    Join<Requisition, User> userJoin = root.join(Requisition_.user, JoinType.INNER);
////                    return cq.where(cb.and(cb.between(dateEntryPath, sD, eD), cb.equal(userJoin.get(User_.FLOOR), floor))).getRestriction();
////                }
//            });
//        } else {
////            if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER")) {
//            return service.findAll(input);
////            } else {
////                return service.findAll(input, (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
////                    Join<Requisition, User> userJoin = root.join(Requisition_.user, JoinType.INNER);
////                    return cb.equal(userJoin.get(User_.FLOOR), floor);
////                });
////            }
//        }
//    }
// </editor-fold>
//    
    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/findAll", method = {RequestMethod.POST})
    protected DataTablesOutput<Requisition> findAll(
            HttpServletRequest request,
            @Valid DataTablesInput input,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {

        if (!(startDate != null && endDate != null)) {
            startDate = new DateTime(0);
            endDate = new DateTime();
        }
        final Date sD = startDate.toDate();
        final Date eD = endDate.plusDays(1).withMillisOfDay(0).toDate();

        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        boolean isFindAll = SecurityPropertiesUtils.checkUserInAuthorities(user,
                newArrayList(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_OPER"),
                        new SimpleGrantedAuthority("ROLE_GUEST")
                ));

        List<Integer> m6FloorIds = newArrayList(7);
        boolean isM6 = m6FloorIds.contains(user.getFloor().getId());
        List<Integer> m8FloorIds = newArrayList(6);
        boolean isM8 = m8FloorIds.contains(user.getFloor().getId());
        List<Integer> m9FloorIds = newArrayList(10, 8);
        boolean isM9 = m9FloorIds.contains(user.getFloor().getId());
        List<Integer> m3FloorIds = newArrayList(9, 8);
        boolean isM3 = m3FloorIds.contains(user.getFloor().getId());

        Specification<Requisition> s = (Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Path<Date> dateEntryPath = root.get(Requisition_.createDate);
            Predicate betweenDate = cb.between(dateEntryPath, sD, eD);
            Predicate floorM6 = root.get(Requisition_.FLOOR).get(Floor_.ID).in(m6FloorIds);
            Predicate floorM8 = root.get(Requisition_.FLOOR).get(Floor_.ID).in(m8FloorIds);
            Predicate floorM9 = root.get(Requisition_.FLOOR).get(Floor_.ID).in(m9FloorIds);
            Predicate floorM3 = root.get(Requisition_.FLOOR).get(Floor_.ID).in(m3FloorIds);

            if (isFindAll) {
                return cb.and(betweenDate);
            } else if (isM3) {
                return cb.and(
                        betweenDate,
                        floorM3
                );
            } else if (isM6) {
                return cb.and(
                        betweenDate,
                        floorM6
                );
            } else if (isM8) {
                return cb.and(
                        betweenDate,
                        floorM8
                );
            } else if (isM9) {
                return cb.and(
                        betweenDate,
                        floorM9
                );
            } else {
                return cb.and(betweenDate);
            }
        };

        return service.findAll(input, s);
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

        this.checkbeforeSave(newArrayList(requisition));

        service.save(requisition, remark);

        trigger.checkRepair(newArrayList(requisition));
        trigger.checkQualify(newArrayList(requisition));
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
                requisitions.forEach(i -> i.setModelName(null));
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
                r.setPoQty(info.getPoQty());
                r.setMaterialQty(info.getAmount());
                r.setStorageSpaces(info.getStorageSpaces());
            }
        }
        return requisitions;
    }

    private void checkPrintLabel(List<Requisition> requisitions) {
        List<Integer> floorIds = newArrayList(8, 9, 10);
        List<String> labelStorages = getLabelStorages();
        RequisitionReason defaultReason = requisitionReasonService.getOne(6);
        requisitions.forEach(r -> {
            if (r.getMaterialNumber().startsWith("20")
                    && floorIds.contains(r.getFloor().getId())
                    && labelStorages.stream().anyMatch(ls -> r.getStorageSpaces().contains(ls))) {

                r.getFloor().setId(8); // set LABEL floor
                r.setRequisitionReason(defaultReason);
            }
        });
    }

    public List<String> getLabelStorages() {
        return newArrayList("O-3F", "O-IDS", "MFG", "O-4F");
    }

    @ResponseBody
    @RequestMapping(value = "/retrieveSapInfos", method = {RequestMethod.GET, RequestMethod.POST})
    protected List<SapMaterialInfo> retrieveSapInfos(@RequestParam String po, @RequestParam(value = "materialNumbers[]") String[] materialNumbers) throws Exception {
        return this.sapService.retrieveSapMaterialInfos(po, materialNumbers);
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

        return wareHourseService.insertEflowWithUserRemark(l, commitJobNo);
    }

    @ResponseBody
    @RequestMapping(value = "/batchSave", method = {RequestMethod.POST})
    protected String batchSave(@ModelAttribute RequisitionListContainer container) throws Exception {

        List<Requisition> l = container.getMyList();
        this.checkbeforeSave(l);
        service.batchInsert(l);
        return "success";
    }

    public void checkbeforeSave(List<Requisition> l) throws Exception {
        l = this.retrieveSapInfos(l);
        this.checkModelMaterial(l);
        this.checkPrintLabel(l);
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

}
