package com.qcar.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelService {

    private static final String EXCEL_FILE = "qr-scan-log.xlsx";

    public void logQRScan(String carNumber, String ownerName, String unitNo) {
        try {
            Workbook workbook;
            Sheet sheet;
            int rowNum = 0;

            File file = new File(EXCEL_FILE);

            if (file.exists()) {
                // Open existing file
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                rowNum = sheet.getLastRowNum() + 1;
                fis.close();
            } else {
                // Create new file with headers
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("QR Scans");

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Date & Time");
                headerRow.createCell(1).setCellValue("Car Number");
                headerRow.createCell(2).setCellValue("Owner Name");
                headerRow.createCell(3).setCellValue("Unit Number");

                rowNum = 1;
            }

            // Add new scan record
            Row row = sheet.createRow(rowNum);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            row.createCell(0).setCellValue(timestamp);
            row.createCell(1).setCellValue(carNumber);
            row.createCell(2).setCellValue(ownerName);
            row.createCell(3).setCellValue(unitNo);

            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save file
            FileOutputStream fos = new FileOutputStream(EXCEL_FILE);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("QR scan logged to Excel: " + carNumber);

        } catch (Exception e) {
            System.err.println("Error logging to Excel: " + e.getMessage());
        }
    }
}