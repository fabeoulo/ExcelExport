/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.controller;

import com.advantech.controller.RequisitionController;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.api.model.AddRequisitionDto;
import com.advantech.api.model.FloorDto;
import com.advantech.api.model.RequisitionReasonDto;
import com.advantech.controller.SelectOptionController;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionFlow;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.User;
import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.RequisitionFlowService;
import com.advantech.service.db1.RequisitionReasonService;
import com.advantech.service.db1.RequisitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.*;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Justin.Yeh
 */
@RestController
@RequestMapping("/Api/Requisition")
public class RequisitionApiController {

    private final Logger logger = LoggerFactory.getLogger(RequisitionApiController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private FloorService floorService;

    @Autowired
    private RequisitionReasonService requisitionReasonService;

    @Autowired
    private RequisitionFlowService requisitionFlowService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RequisitionController requisitionController;

    @Autowired
    private SelectOptionController selectOptionController;

    @ResponseBody
    @GetMapping(value = "/getFloors")
    public List<FloorDto> getFloors() {
        return selectOptionController.findFloorOptions().stream()
                .map(f -> new FloorDto(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping(value = "/getReasons")
    public List<RequisitionReasonDto> getReasons() {
        return selectOptionController.findRequisitionReasonOptions().stream()
                .map(r -> new RequisitionReasonDto(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }
//
//    @ResponseBody
//    @PostMapping("/hello")
//    public Map<String, String> sayHello(@RequestBody List<FloorDto> floors) {
//
//        floors.forEach((u) -> {
//            logger.info("Name: {} id: {} \n", u.getName(), u.getId());
//        });
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Hello, World!");
//        return response; // ResponseBody or RestController 自動轉JSON格式返回数据
//    }

    @ResponseBody
    @RequestMapping(value = "/getDtoTemplate", method = RequestMethod.GET)
    public AddRequisitionDto getDtoTemplate() {
        return new AddRequisitionDto();
    }

    @ResponseBody
    @RequestMapping(value = "/createRequisition", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> addRequisition(
            @ApiParam(required = true, value = "String of AddRequisitionDto model.")
            @RequestBody String datas) throws Exception {

        String msg = "";
        AddRequisitionDto dto;
        User userDetail;
        try {
            dto = objectMapper.readValue(datas, AddRequisitionDto.class);

            userDetail = (User) customUserDetailsService.loadUserByUsername(dto.getJobnumber());
            SecurityPropertiesUtils.loginUserManual(userDetail);
        } catch (JsonProcessingException e) {
            msg = e.getMessage();
            logger.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        List<Requisition> rL = this.ConvertToReq(dto, userDetail);

        this.SetDefault(rL);

        requisitionController.checkbeforeSave(rL);
        requisitionService.batchInsert(rL);

        try {
            SecurityPropertiesUtils.logoutUserManual();
        } catch (Exception ex) {
            logger.error("Fail logoutUserManual. ", ex);
        }
        return ResponseEntity.ok(msg);
    }

    private List<Requisition> ConvertToReq(AddRequisitionDto dto, User userDetail) {

        Optional<Floor> oF = floorService.findById(dto.getFloorId());
        checkState(oF.isPresent(), "Floor not found.");
        List<RequisitionReason> reasons = requisitionReasonService.findAll();
        Map<Integer, RequisitionReason> mapReason = reasons.stream().collect(Collectors.toMap(RequisitionReason::getId, rt -> rt));

        List<Requisition> rL = dto.getRequitionDto().stream().map(
                rd -> {
                    RequisitionReason reason = mapReason.get(rd.getRequisitionReasonId());
                    checkNotNull(reason, "Reason not found.");
                    String remark = dto.getAgent() + ". " + rd.getRemark();

                    return new Requisition(dto.getPo(), rd.getMaterialNumber(), rd.getAmount(),
                            reason, userDetail, remark, oF.get(),
                            dto.getAgent());
                }).collect(Collectors.toList());

        return rL;
    }

    private List<Requisition> SetDefault(List<Requisition> rL) {
        RequisitionFlow rf = requisitionFlowService.getOne(1);
//        RequisitionReason rr = requisitionReasonService.getOne(2);

        rL.forEach(r -> {
            r.setRequisitionFlow(rf);
//            r.setRequisitionReason(rr);
        });
        return rL;
    }
}
