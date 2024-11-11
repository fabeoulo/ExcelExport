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
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.User;
import com.advantech.service.db1.CustomUserDetailsService;
import com.advantech.service.db1.FloorService;
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
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RequisitionController requisitionController;

    @ResponseBody
    @GetMapping(value = "/getFloors")
    public List<FloorDto> getFloors() {
        return floorService.findAll().stream()
                .map(f -> new FloorDto(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping(value = "/getReasons")
    public List<RequisitionReasonDto> getReasons() {
        return requisitionReasonService.findAll().stream()
                .map(r -> new RequisitionReasonDto(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }

//    @ResponseBody
//    @GetMapping(value = "/testGetUtf8")
    public String testGetUtf8(@RequestParam String testString) {
        HibernateObjectPrinter.print(testString);
        return testString;
    }
//
//    @PostMapping("/hello")
//    public Map<String, String> sayHello(@RequestBody List<FloorDto> floors) {
//
//        floors.forEach((u) -> {
//            logger.info("Name: {} id: {} \n", u.getName(), u.getId());
//        });
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Hello, World!");
//        return response; // 返回JSON格式的数据
//    }
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
        AddRequisitionDto dto = new AddRequisitionDto();
        try {
            dto = objectMapper.readValue(datas, AddRequisitionDto.class);
        } catch (JsonProcessingException e) {
            msg = e.getMessage();
            logger.error(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        User userDetail = (User) customUserDetailsService.loadUserByUsername(dto.getJobnumber());
        List<Requisition> rL = ConvertToReq(dto, userDetail);

        rL = requisitionController.retrieveSapInfos(rL);
        requisitionController.checkModelMaterial(rL);
        requisitionService.batchInsert(rL, userDetail);

        return ResponseEntity.ok(msg);
    }

    private List<Requisition> ConvertToReq(AddRequisitionDto dto, User userDetail) {

        Optional<Floor> oF = floorService.findById(dto.getFloorId());
        checkState(oF.isPresent(), "Floor not found.");

        List<Requisition> rL = dto.getRequitionDto().stream().map(
                rd -> {
                    Optional<RequisitionReason> oR = requisitionReasonService.findById(rd.getRequisitionReasonId());
                    checkState(oR.isPresent(), "Reason not found.");

                    return new Requisition(dto.getPo(), rd.getMaterialNumber(), rd.getAmount(),
                            oR.get(), userDetail, "FIMP. " + rd.getRemark(), oF.get());
                }).collect(Collectors.toList());

        return rL;
    }
}
