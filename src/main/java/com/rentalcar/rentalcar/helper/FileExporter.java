package com.rentalcar.rentalcar.helper;
import com.itextpdf.text.*;
import com.rentalcar.rentalcar.common.CalculateNumberOfDays;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static com.rentalcar.rentalcar.common.Constants.FINE_COST;

public class FileExporter {
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


    public static ResponseEntity<byte[]> exportBillToPdf(MyBookingDto contract, MyBookingDto actual) throws IOException, DocumentException {
        // Tạo một ByteArrayOutputStream để lưu file PDF vào bộ nhớ
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Tạo Document và PdfWriter
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        // Mở tài liệu
        document.open();

        // Trang 1: Hóa đơn contract
        addBillPage(document, contract, "Contract Invoice", false);

        // Thêm trang mới
        document.newPage();

        // Trang 2: Hóa đơn actual
        addBillPage(document, actual, "Actual Invoice", true);

        // Đóng tài liệu
        document.close();

        // Chuyển file PDF thành byte[]
        byte[] pdfBytes = outputStream.toByteArray();

        // Trả file PDF dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Hàm hỗ trợ tạo nội dung cho từng trang
    private static void addBillPage(Document document, MyBookingDto bill, String title, boolean isActual) throws DocumentException {
        // Tiêu đề
        Paragraph titleParagraph = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);

        document.add(new Paragraph("\n")); // Khoảng cách dòng

        // Thêm thông tin chi tiết
        document.add(new Paragraph("Booking Details", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Booking No: " + bill.getBookingId()));
        document.add(new Paragraph("Booking Status: " + bill.getBookingStatus()));
        document.add(new Paragraph("Start Date: " + formatDate(bill.getStartDate())));
        document.add(new Paragraph("End Date: " + formatDate(bill.getEndDate())));
        if(isActual && isCompletedOrCancelled(bill.getBookingStatus())) {
            document.add(new Paragraph("Actual End Date: " + formatDate(bill.getActualEndDate())));
        }
        else if(isActual) {
            document.add(new Paragraph("Actual End Date: " + formatDate(LocalDateTime.now())));
        }

        document.add(new Paragraph("Number of Days: " + bill.getStr_numberOfDays()));
        if(isActual && bill.getLateTime() != null) {
            document.add(new Paragraph("Late Time: " + bill.getLateTime()));
        }

        document.add(new Paragraph("\n"));

        // Thêm thông tin tài chính
        document.add(new Paragraph("Financial Details", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        document.add(new Paragraph("Base Price: " + formatCurrency(bill.getBasePrice())+ " VND"));
        document.add(new Paragraph("Total Price: " + formatCurrency(bill.getTotalPrice())+ " VND"));
        document.add(new Paragraph("Fine Late Time: " + formatCurrency(bill.getFineLateTime())+ " VND"));
        document.add(new Paragraph("Addition Payment " + formatCurrency(bill.getTotalMoney())+ " VND"));
        document.add(new Paragraph("Return Deposit " + formatCurrency(bill.getReturnDeposit())+ " VND"));
        document.add(new Paragraph("Driver Salary: " + formatCurrency(bill.getSalaryDriver()) + " VND"));

        document.add(new Paragraph("\n"));

        // Footer
        Paragraph footer = new Paragraph("Thank you for choosing our service!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    private static String formatCurrency(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")); // Định dạng tiền Việt Nam
        return currencyFormatter.format(amount);
    }

    private static String formatDate(LocalDateTime date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return date.format(dateFormatter);
    }
    private static boolean isCompletedOrCancelled(String bookingStatus) {
        return bookingStatus.equalsIgnoreCase("Cancelled") || bookingStatus.equalsIgnoreCase("Completed") ||
                bookingStatus.equalsIgnoreCase("Pending cancel") || bookingStatus.equalsIgnoreCase("Pending payment");
    }
}
