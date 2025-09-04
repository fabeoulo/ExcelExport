/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionCateIms;
import com.advantech.model.db1.RequisitionCateMes;
import com.advantech.model.db1.ScrappedDetail;
import com.advantech.model.db1.Unit;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserProfile;
import com.advantech.repo.db1.FloorRepository;
import com.advantech.repo.db1.UnitRepository;
import com.advantech.repo.db1.UserProfileRepository;
import com.advantech.repo.db1.UserRepository;
import com.advantech.service.db1.ReturnService;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelReaderTest {

    @Autowired
    private ExcelDataTransformer t;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FloorRepository floorRepo;

    @Autowired
    private UserProfileRepository profileRepo;

    @Autowired
    private UnitRepository unitRepo;

    @Autowired
    private ReturnService returnService;

//    @Test
    public void testExcelReportService() throws IOException {
        DateTime sdt = new DateTime().dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        DateTime edt = new DateTime().dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue();

        List<Requisition> datas = returnService.findAllNoGood(sdt, edt);
        List<Requisition> checkedList = returnService.getQualifyCheckedList(datas);

        ByteArrayOutputStream out = this.generateReport(checkedList);
        try ( FileOutputStream fos = new FileOutputStream("D:/Users/Justin.yeh/Downloads/reportOut.xlsx")) {
            out.writeTo(fos);
        }
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

    //    @Test
    public void testRead() throws Exception {
        List<ScrappedDetail> list1 = t.getFloorFiveExcelData();
        assertTrue(!list1.isEmpty());

        List<String> model = list1.stream().map(ScrappedDetail::getMaterialNumber).collect(toList());

        model.forEach(s -> {
            if (s.contains(".")) {
                System.out.println(Long.toString(new BigDecimal(s).longValue()));
            } else {
                System.out.println(s);
            }
        });

    }

//    @Test
//    @Transactional
//    @Rollback(false)
    public void testImportUser() throws Exception {
        List<User> users = userRepo.findAll();
        List<UserProfile> profiles = profileRepo.findAll();
        List<Floor> floors = floorRepo.findAll();
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        Unit unit = unitRepo.getOne(1);
        UserProfile userRole = profileRepo.getOne(3);
        UserProfile operRole = profileRepo.getOne(5);

        DataFormatter dataFormatter = new DataFormatter();
        String fileLocation = "C:\\Users\\wei.cheng\\Desktop\\開發快速領料平台介面同仁名單.xlsx";

        try ( Workbook workbook = WorkbookFactory.create(new File(fileLocation))) {
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach(row -> {
                String jobnumber = dataFormatter.formatCellValue(row.getCell(1));
                String name = dataFormatter.formatCellValue(row.getCell(2));
                String floorName = dataFormatter.formatCellValue(row.getCell(3));
                String userRoleString = dataFormatter.formatCellValue(row.getCell(4));
//                System.out.printf("%s\t%s\t%s\t%s\t\r\n", jobnumber, name, floorName, userRole);

                User user = users.stream().filter(u -> u.getJobnumber().equals(jobnumber.trim())).findFirst().orElse(null);
                if (!jobnumber.trim().equals("") && user == null && !name.equals("姓名")) {
                    user = new User();
                    user.setJobnumber(jobnumber);
                    user.setUsername(name);
                    user.setPassword(encoder.encode(jobnumber));
                    user.setEmail("");
                    user.setFloor(floors.stream().filter(f -> f.getName().equals(floorName)).findFirst().orElse(null));
                    user.setUnit(unit);
                    userRepo.save(user);
                    Set roles = new HashSet();
                    roles.add(userRoleString.equals("使用者") ? userRole : operRole);
                    user.setUserProfiles(roles);
                    userRepo.save(user);
                }

            });
            // Closing the workbook
        }
    }
}
