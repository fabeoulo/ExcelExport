/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.job;

import com.advantech.helper.ExcelDataTransformer;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.ScrappedDetail;
import com.advantech.repo.db1.FloorRepository;
import com.advantech.repo.db1.ScrappedDetailRepository;
import static com.google.common.collect.Lists.newArrayList;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 *
 * @author Wei.Cheng Sync back excel's data from "MFG-Server (MFG-OAPC-019B)"
 * Every day ※Only sync current years data ※before2021
 */
@Component
public class SyncData {

    private static final Logger logger = LoggerFactory.getLogger(SyncData.class);

    @Autowired
    private ExcelDataTransformer excelTran;

    @Autowired
    private ScrappedDetailRepository scrappedRepo;

    @Autowired
    private FloorRepository floorRepo;

    @Transactional
    public void execute() {

        List<Floor> floors = floorRepo.findAllById(newArrayList(2));

        try {
            for (Floor f : floors) {

                List<ScrappedDetail> excelData;

                if (f.getName().equals("5F")) {
                    excelData = excelTran.getFloorFiveExcelData();
                } else {
                    excelData = excelTran.getFloorSixExcelData();
                }

                List<ScrappedDetail> dataInDb = scrappedRepo.findByFloor(f);

                excelData.forEach(s -> s.setFloor(f));

                //modelname & materialNumber are not format as string in excel
                excelData = fixMaterialNumAndModelname(excelData);

                //Find new data
                List<ScrappedDetail> newData = (List<ScrappedDetail>) CollectionUtils.subtract(excelData, dataInDb);

                Date today = new Date();
                newData = getFromSameYear(newData, today);

                logger.info("Saving floor {} data: {} \n", f.getName(), newData.size());

                scrappedRepo.saveAll(newData);

                //注意最後check兩邊size是否一致, 不一致可能是db有多的資料(使用者delete過), 必須做刪除
                //Don't remove data a year ago
                logger.info("Checking the data size again...");
                dataInDb = scrappedRepo.findByFloor(f);

                if (dataInDb.size() != excelData.size()) {
                    logger.info("Detect different data, begin remove...");

                    List<ScrappedDetail> delData = (List<ScrappedDetail>) CollectionUtils.subtract(dataInDb, excelData);

                    delData = getFromSameYear(delData, today);
                    logger.info("Remove floor {} data: {} \n", f.getName(), delData.size());

                    scrappedRepo.deleteAll(delData);
                } else {
                    logger.info("Nothing need to remove.");
                }
            }
        } catch (IOException | SAXException | InvalidFormatException ex) {
            logger.error("Sync back excel's data fail.", ex);
        }
    }

    private List<ScrappedDetail> fixMaterialNumAndModelname(List<ScrappedDetail> l) {
        l = this.fixLongString(l);
        l = this.trimWhiteSpace(l);
        return l;
    }

    private List<ScrappedDetail> trimWhiteSpace(List<ScrappedDetail> l) {
        for (ScrappedDetail s : l) {
            s.setModelName(s.getModelName().trim());
            s.setMaterialNumber(s.getMaterialNumber().trim());
        }
        return l;
    }

    private List<ScrappedDetail> fixLongString(List<ScrappedDetail> l) {
        for (ScrappedDetail s : l) {
            if (s.getModelName().contains(".")) {
                s.setModelName(longToString(s.getModelName()));
            }
            if (s.getMaterialNumber().contains(".")) {
                s.setMaterialNumber(longToString(s.getMaterialNumber()));
            }
        }
        return l;
    }

    private String longToString(String val) {
        try {
            return Long.toString(new BigDecimal(val).longValue());
        } catch (Exception e) {
            return val;
        }
    }

    private List<ScrappedDetail> getFromSameYear(List<ScrappedDetail> l, Date currentDate) {
        return l.stream().filter(s -> getYearBetween(s.getCreateDate(), currentDate) < 1).collect(toList());
    }

    private int getYearBetween(Date d1, Date d2) {
        return Years.yearsBetween(new DateTime(d1), new DateTime(d2)).getYears();
    }

}
