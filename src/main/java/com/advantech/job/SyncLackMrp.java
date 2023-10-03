/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.model.db2.Items;
import com.advantech.model.db2.MaterialMrp;
import com.advantech.model.db2.Orders;
import com.advantech.sap.SapMrpTbl;
import com.advantech.sap.SapService;
import com.advantech.service.db2.ItemsService;
import com.advantech.service.db2.MaterialMrpService;
import com.advantech.service.db2.OrdersService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class SyncLackMrp {

    private static final Logger logger = LoggerFactory.getLogger(SyncLackMrp.class);

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private SapService sapService;

    @Autowired
    private MaterialMrpService materialMrpService;

    @Autowired
    private OrdersService ordersService;

    @Transactional
    public void execute() throws Exception {
        syncLackingMaterialMrp();
        updateOrdersOwner();
    }

    private void syncLackingMaterialMrp() throws Exception {
        List<Items> itemOrders = itemsService.findAllWithPlant();

        List<SapMrpTbl> tblin = itemOrders.stream().map(i -> {
            return new SapMrpTbl(i.getLabel3(), i.getOrders().getTeams().getPlant());
        }).collect(Collectors.toList());

        // take a long time if tblin.size()>1000
        Map<String, String> mrpMap = sapService.getMrpCodeByTblin(tblin);
        List<MaterialMrp> newMms = new ArrayList<>();
        mrpMap.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                String[] keys = k.split(",");
                MaterialMrp mm = new MaterialMrp(keys[1], keys[0], v, new DateTime().toDate());
                newMms.add(mm);
            }
        });

        saveMaterialMrps(newMms);
        saveItems(itemOrders, mrpMap);
    }

    private void saveMaterialMrps(List<MaterialMrp> newMms) {
        List<MaterialMrp> mergeList = new ArrayList<>();
        List<MaterialMrp> dbMms = materialMrpService.findAll();
        for (MaterialMrp newMm : newMms) {
            MaterialMrp mm = findMaterialInDb(dbMms, newMm);
            if (mm == null) {
                mergeList.add(newMm);
            } else if (!mm.getMrpCode().equals(newMm.getMrpCode())) {
                mm.setMrpCode(newMm.getMrpCode());
                mm.setUpdateTime(newMm.getUpdateTime());
                mergeList.add(mm);
            }
        }
        materialMrpService.saveAll(mergeList);
    }

    private MaterialMrp findMaterialInDb(List<MaterialMrp> list, MaterialMrp material) {
        return list.stream()
                .filter(
                        m -> m.getPlant().equals(material.getPlant())
                        && m.getMatName().equals(material.getMatName())
                )
                .findFirst()
                .orElse(null);
    }

    private void saveItems(List<Items> itemOrders, Map<String, String> mrpMap) {
        List<Items> syncItems = itemOrders.stream()
                .map(i -> {
                    String k = i.getLabel3() + "," + i.getOrders().getTeams().getPlant();
                    String code = mrpMap.getOrDefault(k, "");
                    i.setMrpCode(code);
                    i.setMrpSync(true);
                    return i;
                })
                .collect(Collectors.toList());
        itemsService.saveAll(syncItems);
    }

    private void updateOrdersOwner() {
        List<Orders> orders = ordersService.findAllOpenWithoutReply();
        List<String> plants = orders.stream().map(o -> o.getTeams().getPlant()).collect(Collectors.toList());
        List<String> materials = orders.stream()
                .map(o -> {
                    Items i = o.getItemses().stream().findFirst().get();
                    return i.getLabel3();
                }).collect(Collectors.toList());

        Map<String, String> map = materialMrpService.getMrpMap(plants, materials);
        List<Orders> updateL = orders.stream()
                .filter(o -> {
                    Items i = o.getItemses().stream().findFirst().get();
                    String key = o.getTeams().getPlant() + i.getLabel3();
                    if (map.containsKey(key)) {
                        o.setOwnerId(map.get(key));
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

        ordersService.saveAll(updateL);
    }
}
