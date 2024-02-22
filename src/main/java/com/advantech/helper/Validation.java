/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Justin.Yeh
 */
public class Validation {

    @Autowired
    private Validator validator;

    public <T extends Object> boolean validateTargets(List<T> tars) {
        Map<String, String> errors = new HashMap();
        int count = 0;
        for (T tar : tars) {
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(tar);
            if (!constraintViolations.isEmpty()) {
                Iterator it = constraintViolations.iterator();
                while (it.hasNext()) {
                    ConstraintViolation violation = (ConstraintViolation) it.next();
                    errors.put("Item " + count + "-" + violation.getPropertyPath().toString(), violation.getMessage());
                }
            }
            count++;
        }

        Preconditions.checkState(errors.isEmpty(), new Gson().toJson(errors));
        return true;
    }
}
