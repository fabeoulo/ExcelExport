/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db2.Items;
import com.advantech.model.db2.OrderTypes;
import com.advantech.model.db2.Orders;
import com.advantech.service.db1.RequisitionService;
import com.advantech.service.db2.OrderTypesService;
import com.advantech.service.db2.OrdersService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Wei.Cheng
 */
@RestController
@RequestMapping("/OrdersController")
public class OrdersController {

    @Autowired
    private OrdersService service;

    @Autowired
    private OrderTypesService orderTypesService;

    @Autowired
    private RequisitionService requisitionService;

    @ResponseBody
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    protected String save(@ModelAttribute Orders orders, @RequestParam String po, @RequestParam String material, @RequestParam String snBoard, @RequestParam int requision_id, BindingResult bindingResult) throws Exception {

        bindingResult.getAllErrors().stream().map((object) -> {
            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;

                System.out.println(fieldError.getCode());
            }
            return object;
        }).filter((object) -> (object instanceof ObjectError)).map((object) -> (ObjectError) object).forEachOrdered((objectError) -> {
            System.out.println(objectError.getCode());
        });

        orders.setRequisionId(requision_id);
        Items i = new Items(orders, po, null, material, snBoard);
        service.save(orders, i);

        Requisition req = requisitionService.findById(requision_id).get();
        req.setLackingFlag(1);
        requisitionService.save(req, "Save data to lacking db");
        return "success";

    }

    @ResponseBody
    @RequestMapping(value = "/findOrderTypesOptions", method = {RequestMethod.GET})
    protected List<OrderTypes> findOrderTypesOptions() {
        return orderTypesService.findAll();
    }

}
