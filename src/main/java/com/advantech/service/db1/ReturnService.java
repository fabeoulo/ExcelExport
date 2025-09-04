/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionCateIms;
import com.advantech.model.db1.RequisitionCateMes;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class ReturnService {

    private final List<Integer> floorIdsByArea = newArrayList(9, 10);
    private final List<Integer> typeForQualify = Arrays.asList(2);

    private final List<Integer> exceptUserIds = Arrays.asList(1);
    private final List<Integer> stateForQualify = Arrays.asList(7);
    private final List<Integer> reasonForQualify = Arrays.asList(2);

    private final DateTimeFormatter fmtD = DateTimeFormat.forPattern("yyyy/MM/dd");

    @Autowired
    private RequisitionService rservice;

    public List<Requisition> findAllNoGood(DateTime sdt, DateTime edt) {
        return rservice.findAllByReturnAndTypeAndFloor(sdt, edt, typeForQualify, floorIdsByArea);
    }

    public List<Requisition> filterQualify(List<Requisition> rl) {
        return rl.stream()
                .filter(e -> {
                    int floorId = e.getFloor().getId();
                    int userId = e.getUser().getId();
                    int rsId = e.getRequisitionState().getId();
                    int rrId = e.getRequisitionReason().getId();
                    int rtId = e.getRequisitionType().getId();
                    Date returnDate = e.getReturnDate();
                    int amount = e.getAmount();

                    return !exceptUserIds.contains(userId)
                            && returnDate != null
                            && amount > 0
                            && floorIdsByArea.contains(floorId)
                            && stateForQualify.contains(rsId)
                            && reasonForQualify.contains(rrId)
                            && typeForQualify.contains(rtId);
                })
                .collect(Collectors.toList());
    }

    public List<Requisition> getQualifyCheckedList(List<Requisition> returnAll) {

        List<Requisition> filtered = this.filterQualify(returnAll);

        return filtered.stream()
                .collect(Collectors.groupingBy(item
                        -> Arrays.asList(
                        item.getPo(),
                        item.getMaterialNumber(),
                        this.getCateMesName(item),
                        item.getFloor())
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().stream().mapToInt(Requisition::getAmount).sum() > 2)
                .map(entry -> {
                    List keys = entry.getKey();
                    List<Requisition> gl = entry.getValue();

                    Requisition latest = gl.stream().max(Comparator.comparing(r -> r.getReturnDate())).orElse(new Requisition());
                    int amountSum = gl.stream().mapToInt(Requisition::getAmount).sum();
                    String returnReasonAll = gl.stream()
                            .filter(o -> !this.getStringSafely(o.getReturnReason(), String::toString, "").equals(""))
                            .map(o -> "【" + o.getReturnReason() + "】")
                            .collect(Collectors.joining("、"));
                    String imsCateName = gl.stream()
                            .filter(o -> !this.getStringSafely(o.getRequisitionCateIms(), RequisitionCateIms::getName, "").equals(""))
                            .map(o -> o.getRequisitionCateIms().getName())
                            .distinct()
                            .collect(Collectors.joining("、"));

                    Requisition g = new Requisition();
                    g.setFloor((Floor) keys.get(3));
                    g.setPo(keys.get(0).toString());
                    g.setMaterialNumber(keys.get(1).toString());
                    g.setRequisitionCateMesCustom(keys.get(2).toString());
                    g.setAmount(amountSum);
                    g.setModelName(latest.getModelName());
                    g.setPoQty(latest.getPoQty());
                    g.setReturnReason(returnReasonAll);

                    g.setRemark(imsCateName);
                    g.setReturnDate(latest.getReturnDate());

                    return g;
                })
                .sorted(
                        Comparator.comparing((Requisition r) -> r.getFloor().getId()).reversed()
                                .thenComparing(Requisition::getPo)
                                .thenComparing(Requisition::getMaterialNumber)
                )
                .collect(Collectors.toList());
    }

    public String getFormatDate(Date d) {
        return fmtD.print(new DateTime(d));
    }

    public <T> String getStringSafely(T obj, Function<T, String> getter, String replacement) {
        return Optional.ofNullable(obj).map(getter).orElse(replacement).trim();
    }

    public String getCateMesName(Requisition r) {
        return Optional.ofNullable(r.getRequisitionCateMes()).map(RequisitionCateMes::getName)
                .orElse(
                        Optional.ofNullable(r.getRequisitionCateMesCustom()).orElse("")
                );
    }
}
