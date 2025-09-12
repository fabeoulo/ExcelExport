/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.ScrappedRequisition;
import com.advantech.repo.db1.ScrappedRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.collect.Lists.newArrayList;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class ScrappedService {

    @Autowired
    private ScrappedRepository repo;

    public List<ScrappedRequisition> findAllTarget(DateTime sdt, DateTime edt) {
        return repo.findAllScrapped(sdt.toDate(), edt.toDate());
    }

    public Map<Integer, List<ScrappedRequisition>> getWeeklyGroup(List<ScrappedRequisition> rl) {
        return rl.stream().collect(Collectors.groupingBy(
                sr -> sr.getWeek(),
                TreeMap::new, // TreeMap for order
                Collectors.toList()
        ));
    }

    public List<Map<Integer, List<Integer>>> getPriceMap(Map<Integer, List<ScrappedRequisition>> grouped) {

        int priceSumAll = 0, amountSumAll = 0;
        Map<Integer, List<Integer>> mapPrice3f = new HashMap<>(), mapPrice4f = new HashMap<>(), mapPriceBoth = new HashMap<>(), mapPriceAll = new HashMap<>();

        for (Map.Entry<Integer, List<ScrappedRequisition>> entry : grouped.entrySet()) {
            Integer keys = entry.getKey();
            List<ScrappedRequisition> gl = entry.getValue();

            List<ScrappedRequisition> gl3f = getFilterByFloor(gl, 9);
            List<ScrappedRequisition> gl4f = getFilterByFloor(gl, 10);

            int priceSum3f = getPriceSum(gl3f);
            int priceSum4f = getPriceSum(gl4f);
            int priceSumBoth = priceSum3f + priceSum4f;
            priceSumAll += priceSumBoth;

            int amountSum3f = gl3f.stream().mapToInt(ScrappedRequisition::getAmount).sum();
            int amountSum4f = gl4f.stream().mapToInt(ScrappedRequisition::getAmount).sum();
            int amountSumBoth = amountSum3f + amountSum4f;
            amountSumAll += amountSumBoth;

            mapPrice3f.put(keys, newArrayList(priceSum3f, amountSum3f));
            mapPrice4f.put(keys, newArrayList(priceSum4f, amountSum4f));
            mapPriceBoth.put(keys, newArrayList(priceSumBoth, amountSumBoth));
            mapPriceAll.put(keys, newArrayList(priceSumAll, amountSumAll));
        }

        return newArrayList(mapPrice4f, mapPrice3f, mapPriceBoth, mapPriceAll);

    }

    public List<ScrappedRequisition> getFilterByFloor(List<ScrappedRequisition> gl, int floorId) {
        return gl.stream().filter(all -> all.getFloorIdBoth().equals(floorId)).collect(Collectors.toList());
    }

    public int getPriceSum(List<ScrappedRequisition> gl) {
        return gl.stream().map(sr -> sr.getUnitPrice().multiply(BigDecimal.valueOf(sr.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .intValue();
    }

// <editor-fold desc="develop json query.">
    public List<Requisition> findAllTargetByJson(DateTime sdt, DateTime edt) throws Exception {
        String json = this.findAllTargetJson(sdt, edt);

        ObjectMapper mapper = new ObjectMapper();
        List<Requisition> l = mapper.readValue(json, new TypeReference<List<Requisition>>() {
        });

        return l;
    }

    private String findAllTargetJson(DateTime sdt, DateTime edt) throws Exception {
        Object result = repo.findAllScrappedByTbfn(sdt.toDate(), edt.toDate());
        String json;
        if (result instanceof String) {
            json = (String) result;
        } else if (result instanceof Clob) {
            Clob clob = (Clob) result;
            json = clob.getSubString(1, (int) clob.length());
        } else {
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
        return json;
    }
// </editor-fold>

}
