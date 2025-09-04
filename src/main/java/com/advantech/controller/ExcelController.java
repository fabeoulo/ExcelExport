/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.controller;

import com.advantech.model.db1.Requisition;
import com.advantech.service.db1.ReturnService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Justin.Yeh
 */
@Controller
@RequestMapping("/Excel")
public class ExcelController {

    @Autowired
    private ReturnService returnService;

    @ResponseBody
    @RequestMapping(value = "/downloadReturn", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> downloadReturn(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime date) throws Exception {

        DateTime dataDate = Optional.ofNullable(date).orElse(DateTime.now());

        DateTime sdt = dataDate.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime edt = dataDate.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();

        List<Requisition> datas = returnService.findAllNoGood(sdt, edt);
        List<Requisition> checkedList = returnService.getQualifyCheckedList(datas);

        ByteArrayOutputStream out = this.generateReport(checkedList);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reportReturn.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    private ByteArrayOutputStream generateReport(List<Requisition> data) throws IOException {

        ClassPathResource resource = new ClassPathResource("excel-template/return_template.xlsx");
        try ( InputStream is = resource.getInputStream();  Workbook workbook = new XSSFWorkbook(is);  ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            Sheet sheet = workbook.getSheetAt(0);

            // 從第2列開始填資料 (假設第1列是標題)
            int rowIndex = 1;
            for (Requisition item : data) {
                Row row = sheet.createRow(rowIndex++);

                Cell cell = row.createCell(0);
                cell.setCellValue(getPlantByFloor(item.getFloor().getId()));
                cell = row.createCell(1);
                cell.setCellValue(item.getPo());
                cell = row.createCell(2);
                cell.setCellValue(item.getModelName());
                cell = row.createCell(3);
                cell.setCellValue(item.getMaterialNumber());
                cell = row.createCell(4);
                cell.setCellValue(item.getRemark());
                cell = row.createCell(5);
                cell.setCellValue(item.getPoQty().intValue());
                cell = row.createCell(6);
                cell.setCellValue(item.getAmount());
                cell = row.createCell(7);
                cell.setCellValue(returnService.getCateMesName(item));
                cell = row.createCell(8);
                cell.setCellValue(item.getReturnReason());
                cell = row.createCell(22);
                cell.setCellValue(returnService.getFormatDate(item.getReturnDate()));
            }

            workbook.write(out);
            return out;
        }
    }

    private String getPlantByFloor(int floorId) {
        return floorId == 6 ? "TWM8"
                : floorId == 7 ? "TWM6"
                        : "TWM9";
    }

}
