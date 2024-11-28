package com.rentalcar.rentalcar.helper;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
public class ExcelExporter {
    public static ResponseEntity<byte[]> exportBillsToExcel(MyBookingDto bill) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bills");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Start Date", "End Date", "Base Price", "Total", "Number of Days", "Salary Driver", "Booking No", "Booking Status"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Điền dữ liệu
        int rowIndex = 1;

            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(bill.getStartDate().toString());
            row.createCell(1).setCellValue(bill.getEndDate().toString());
            row.createCell(2).setCellValue(bill.getBasePrice());
            row.createCell(3).setCellValue(bill.getTotalPrice());
            row.createCell(4).setCellValue(String.valueOf(bill.getStr_numberOfDays()));
            row.createCell(5).setCellValue(bill.getSalaryDriver());
            row.createCell(6).setCellValue(bill.getBookingId());
            row.createCell(7).setCellValue(bill.getBookingStatus());


        // Xuất file ra byte[]
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] excelBytes = outputStream.toByteArray();

        // Trả file Excel dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bills.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
