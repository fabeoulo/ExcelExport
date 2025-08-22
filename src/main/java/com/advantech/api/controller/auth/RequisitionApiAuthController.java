/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.controller.auth;

import com.advantech.controller.RequisitionController;
import com.advantech.api.model.AddRequisitionDto;
import com.advantech.api.model.FloorDto;
import com.advantech.api.model.RequisitionDto;
import com.advantech.api.model.RequisitionReasonDto;
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
import static com.google.common.collect.Lists.newArrayList;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController; // Implicitly include ResponseBody

/**
 *
 * @author Justin.Yeh
 */
@Controller(value = "RequisitionApiAuthController")
@RequestMapping("/ApiAuth/Requisition")
public class RequisitionApiAuthController {

    private final Logger logger = LoggerFactory.getLogger(RequisitionApiAuthController.class);

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

    @ResponseBody
    @GetMapping(value = "/getFloors")
    public List<FloorDto> getFloors() {
        return requisitionController.findFloorOptions().stream()
                .map(f -> new FloorDto(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping(value = "/getReasons")
    public List<RequisitionReasonDto> getReasons() {
        return requisitionController.findRequisitionReasonOptions().stream()
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
//        return response; // 自動轉JSON格式返回数据
//    }

//    @ResponseBody
//    @RequestMapping(value = "/createRequisition", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> addRequisition(
            @ApiParam(required = true, value = "String of AddRequisitionDto model.")
            @RequestBody String datas) throws Exception {

        String msg = "";
        AddRequisitionDto dto = new AddRequisitionDto();
        User userDetail = new User();
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

        rL.forEach(r -> {
            r.setRequisitionFlow(rf);
        });
        return rL;
    }

    @ResponseBody
    @RequestMapping(value = "/queryRequisition", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<List<RequisitionDto>> queryRequisition(@RequestBody Map<String, Object> body) throws Exception {

        DateTime sd, ed;
        List<Integer> floorIds;

        String startDateStr = (String) body.get("startDate");
        String endDateStr = (String) body.get("endDate");

        sd = DateTime.parse(startDateStr);
        ed = DateTime.parse(endDateStr);
        checkArgument(ed.isBefore(sd.plusMonths(1).plusDays(1)), "over 1 month");
        checkIntegerList(body.get("floorId"), "floorId");
        floorIds = (List<Integer>) body.get("floorId");
        checkArgument(!floorIds.isEmpty(), "need floorId");

        List<Requisition> l = requisitionService.findAllByReturnAndTypeAndFloor(sd, ed, newArrayList(2), floorIds);
        List<RequisitionDto> l_dto = l.stream().map(r -> new RequisitionDto(r)).collect(Collectors.toList());

        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        logger.info("queryRequisition user:  {} id: {}", user.getUsername(), user.getId());
        return ResponseEntity.ok(l_dto);
    }

    private static void checkIntegerList(Object value, String key) {
        if (!(value instanceof List<?>)) {
            throw new IllegalArgumentException(key + " 必須是序列");
        }

        ((List<?>) value).stream()
                .map(elem -> {
                    if (elem instanceof Integer) {
                        return (Integer) elem;
                    } else {
                        throw new IllegalArgumentException(key + " 包含非整數: " + elem);
                    }
                })
                .collect(Collectors.toList());
    }
}
