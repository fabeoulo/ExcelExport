/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.job;

import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.ScrappedDetail;
import com.advantech.model.db1.ScrappedRequisition;
import com.advantech.model.db1.ScrappedSummary;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.ScrappedDetailService;
import com.advantech.service.db1.ScrappedService;
import com.advantech.service.db1.ScrappedSummaryService;
import static com.google.common.collect.Lists.newArrayList;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class SyncDataScrappedReport extends JobScrappedBase {

    private static final Logger logger = LoggerFactory.getLogger(SyncDataScrappedReport.class);

    @Autowired
    private ScrappedService scrappedService;

    @Autowired
    private ScrappedSummaryService scrappedSummaryService;

    @Autowired
    private ScrappedDetailService srappedDetailService;

    @Autowired
    private FloorService floorService;

    private List<ScrappedRequisition> dataNow, dataNowExtra, dataNowShort;

    private Map<Integer, List<ScrappedRequisition>> groupedUp = new HashMap<>(), groupedDown = new HashMap<>();
    private Map<Integer, List<ScrappedRequisition>> groupedExtraUp = new HashMap<>(), groupedExtraDown = new HashMap<>();
    private Map<Integer, List<ScrappedRequisition>> groupedShortUp = new HashMap<>(), groupedShortDown = new HashMap<>();

    public void execute() {

        super.setDateTime(DateTime.now());

        DateTime sD = lastWeek.dayOfWeek().withMinimumValue();
        DateTime eD = thisMon;
        dataNow = scrappedService.findAllScrapped(sD, eD, lastWeekYw);
        dataNowExtra = scrappedService.findAllScrappedExtra(sD, eD, lastWeekYw);
        dataNowShort = scrappedService.findAllShort(sD, eD, lastWeekYw);

        List<Floor> floors = floorService.findAllById(newArrayList(7, 9, 10, 1));

        deleteDataInDb(floors);

        // repeat save is OK.
        saveWeekData();

        saveScrappedDetail(floors);
    }

    // repeat save is OK.
    private void saveWeekData() {

        Predicate<ScrappedRequisition> prUp = sr -> new BigDecimal(100).compareTo(sr.getUnitPrice()) < 0;
        Predicate<ScrappedRequisition> prDown = sr -> new BigDecimal(100).compareTo(sr.getUnitPrice()) >= 0;
        groupedUp = scrappedService.getWeeklyGroup(dataNow, prUp);
        groupedDown = scrappedService.getWeeklyGroup(dataNow, prDown);
        groupedExtraUp = scrappedService.getWeeklyGroup(dataNowExtra, prUp);
        groupedExtraDown = scrappedService.getWeeklyGroup(dataNowExtra, prDown);
        groupedShortUp = scrappedService.getWeeklyGroup(dataNowShort, prUp);
        groupedShortDown = scrappedService.getWeeklyGroup(dataNowShort, prDown);

        ScrappedSummary summaryM9_3F = new ScrappedSummary();
        ScrappedSummary summaryM9_4F = new ScrappedSummary();
        ScrappedSummary summaryM6 = new ScrappedSummary();
        summaryM9_3F.setYk(lastWeekYw);
        summaryM9_3F.setArea("M9_3F");
        summaryM9_4F.setYk(lastWeekYw);
        summaryM9_4F.setArea("M9_4F");
        summaryM6.setYk(lastWeekYw);
        summaryM6.setArea("M6");

        List<Integer> priceAmountSums = scrappedService.getPriceAmountSumByFloor(groupedUp.getOrDefault(lastWeekYw, Arrays.asList()));
        summaryM9_4F.setScrapSumHundredUp(priceAmountSums.get(0));
        summaryM9_4F.setScrapPcsHundredUp(priceAmountSums.get(1));
        summaryM9_3F.setScrapSumHundredUp(priceAmountSums.get(2));
        summaryM9_3F.setScrapPcsHundredUp(priceAmountSums.get(3));
        summaryM6.setScrapSumHundredUp(priceAmountSums.get(4));
        summaryM6.setScrapPcsHundredUp(priceAmountSums.get(5));

        priceAmountSums = scrappedService.getPriceAmountSumByFloor(groupedDown.getOrDefault(lastWeekYw, Arrays.asList()));
        List<Integer> priceAmountSumsExtraUp = scrappedService.getPriceAmountSumByFloor(groupedExtraUp.getOrDefault(lastWeekYw, Arrays.asList()));
        List<Integer> priceAmountSumsExtraDown = scrappedService.getPriceAmountSumByFloor(groupedExtraDown.getOrDefault(lastWeekYw, Arrays.asList()));
        summaryM9_4F.setScrapSumHundredDown(priceAmountSums.get(0) + priceAmountSumsExtraUp.get(0) + priceAmountSumsExtraDown.get(0));
        summaryM9_4F.setScrapPcsHundredDown(priceAmountSums.get(1) + priceAmountSumsExtraUp.get(1) + priceAmountSumsExtraDown.get(1));
        summaryM9_3F.setScrapSumHundredDown(priceAmountSums.get(2) + priceAmountSumsExtraUp.get(2) + priceAmountSumsExtraDown.get(2));
        summaryM9_3F.setScrapPcsHundredDown(priceAmountSums.get(3) + priceAmountSumsExtraUp.get(3) + priceAmountSumsExtraDown.get(3));
        summaryM6.setScrapSumHundredDown(priceAmountSums.get(4) + priceAmountSumsExtraUp.get(4) + priceAmountSumsExtraDown.get(4));
        summaryM6.setScrapPcsHundredDown(priceAmountSums.get(5) + priceAmountSumsExtraUp.get(5) + priceAmountSumsExtraDown.get(5));

        priceAmountSums = scrappedService.getPriceAmountSumByFloor(groupedShortUp.getOrDefault(lastWeekYw, Arrays.asList()));
        summaryM9_4F.setShortSumHundredUp(priceAmountSums.get(0));
        summaryM9_4F.setShortPcsHundredUp(priceAmountSums.get(1));
        summaryM9_3F.setShortSumHundredUp(priceAmountSums.get(2));
        summaryM9_3F.setShortPcsHundredUp(priceAmountSums.get(3));
        summaryM6.setShortSumHundredUp(priceAmountSums.get(4));
        summaryM6.setShortPcsHundredUp(priceAmountSums.get(5));

        priceAmountSums = scrappedService.getPriceAmountSumByFloor(groupedShortDown.getOrDefault(lastWeekYw, Arrays.asList()));
        summaryM9_4F.setShortSumHundredDown(priceAmountSums.get(0));
        summaryM9_4F.setShortPcsHundredDown(priceAmountSums.get(1));
        summaryM9_3F.setShortSumHundredDown(priceAmountSums.get(2));
        summaryM9_3F.setShortPcsHundredDown(priceAmountSums.get(3));
        summaryM6.setShortSumHundredDown(priceAmountSums.get(4));
        summaryM6.setShortPcsHundredDown(priceAmountSums.get(5));

        scrappedSummaryService.save(summaryM9_3F);
        scrappedSummaryService.save(summaryM9_4F);
        scrappedSummaryService.save(summaryM6);
    }

    private List<ScrappedDetail> deleteDataInDb(List<Floor> floors) {
        List<ScrappedDetail> dataInDb = srappedDetailService.findByFloorAndYk(floors, lastWeekYw);

        Map<Integer, ScrappedRequisition> mapDataNow = Stream.of(dataNow, dataNowExtra, dataNowShort)
                .flatMap(List::stream)
                .collect(Collectors.toMap(s -> (s.getId() / 100) * 100 + s.getFloorIdBoth(), s -> s));

        for (ScrappedDetail sd : dataInDb) {
            // log DataInDb Except dataNow
            if (!mapDataNow.containsKey(sd.getRequisitionId() * 100 + sd.getFloor().getId())) {
                logger.info("ScrappedDetail removed after sync : floorId:{}, requisitionId:{}, yearWeek:{}",
                        sd.getFloor().getId(), sd.getRequisitionId(), lastWeekYw);
            }
            srappedDetailService.delete(sd);
        }
        return dataInDb;
    }

    private void saveScrappedDetail(List<Floor> floors) {

        DateTime now = DateTime.now();
        Map<Integer, Floor> mapFloor = floors.stream().collect(Collectors.toMap(f -> f.getId(), f -> f));

        List<ScrappedDetail> scrappedDetails = Stream.of(dataNow, dataNowExtra, dataNowShort)
                .flatMap(List::stream)
                .map(r -> {
                    Floor rFloor = mapFloor.getOrDefault(r.getFloorIdBoth(), mapFloor.get(1));

                    ScrappedDetail sd = new ScrappedDetail();
                    sd.setFloor(rFloor);
                    sd.setRequisitionId(r.getId() / 100);
                    sd.setYk(lastWeekYw);
                    sd.setCreateDate(now.toDate());

                    return sd;
                })
                .collect(Collectors.toList());

//        // debug
//        HibernateObjectPrinter.print(scrappedDetails.get(0));
//        srappedDetailService.save(scrappedDetails.get(0));
//
        srappedDetailService.saveAll(scrappedDetails);
    }
}
