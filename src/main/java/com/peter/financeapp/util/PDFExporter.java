package com.peter.financeapp.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.service.report.dto.TransactionReportDTO;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExporter {

    private static final BaseColor DARK_BG      = new BaseColor(31, 41, 55);
    private static final BaseColor DARKER_BG    = new BaseColor(17, 24, 39);
    private static final BaseColor ACCENT_BLUE  = new BaseColor(59, 130, 246);
    private static final BaseColor TEXT_LIGHT   = new BaseColor(249, 250, 251);
    private static final BaseColor TEXT_MUTED   = new BaseColor(156, 163, 175);
    private static final BaseColor BORDER_COLOR = new BaseColor(55, 65, 81);
    private static final BaseColor GREEN        = new BaseColor(16, 185, 129);
    private static final BaseColor RED          = new BaseColor(239, 68, 68);

    public static void exportTransactions(List<TransactionReportDTO> transactions,
                                          String filePath,
                                          String userName,
                                          String month) throws Exception {

        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        addHeader(document, userName, month);
        addSummary(document, transactions);
        addTable(document, transactions);
        addFooter(document);

        document.close();
    }

    private static void addHeader(Document document, String userName, String month)
            throws Exception {

        Font titleFont    = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, TEXT_LIGHT);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, TEXT_MUTED);
        Font infoFont     = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_MUTED);
        Font infoValFont  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, TEXT_LIGHT);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1f, 1f});
        headerTable.setSpacingAfter(20);

        // Left — logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(DARKER_BG);
        logoCell.setPadding(16);
        logoCell.addElement(new Paragraph("FinPulse", titleFont));
        logoCell.addElement(new Paragraph("Your financial heartbeat", subtitleFont));
        headerTable.addCell(logoCell);

        // Right — report info
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setBackgroundColor(DARKER_BG);
        infoCell.setPadding(16);
        infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoCell.addElement(new Paragraph("Transaction Report", infoFont));
        infoCell.addElement(new Paragraph(month, infoValFont));
        infoCell.addElement(new Paragraph("Generated for: " + userName, infoFont));
        infoCell.addElement(new Paragraph(
                "Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                infoFont
        ));
        headerTable.addCell(infoCell);

        document.add(headerTable);
    }

    private static void addSummary(Document document, List<TransactionReportDTO> transactions)
            throws Exception {

        double totalIncome = transactions.stream()
                .filter(t -> t.getCategoryType() == CategoryType.INCOME)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getCategoryType() == CategoryType.EXPENSE)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double balance = totalIncome - totalExpense;

        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);

        summaryTable.addCell(createSummaryCell("Total Income",
                String.format("N%,.0f", totalIncome), GREEN));
        summaryTable.addCell(createSummaryCell("Total Expenses",
                String.format("N%,.0f", totalExpense), RED));
        summaryTable.addCell(createSummaryCell("Balance",
                String.format("N%,.0f", balance), ACCENT_BLUE));

        document.add(summaryTable);
    }

    private static PdfPCell createSummaryCell(String label, String value,
                                              BaseColor valueColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(DARK_BG);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(14);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_MUTED);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, valueColor);

        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valueFont));
        return cell;
    }

    private static void addTable(Document document, List<TransactionReportDTO> transactions)
            throws Exception {

        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, TEXT_LIGHT);
        Paragraph sectionTitle = new Paragraph("All Transactions", sectionFont);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2f, 2.5f, 1.5f, 2f});
        table.setSpacingAfter(20);

        // Header row
        String[] headers = {"Date", "Category", "Description", "Type", "Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(DARKER_BG);
            cell.setBorderColor(BORDER_COLOR);
            cell.setPadding(10);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10,
                    Font.BOLD, ACCENT_BLUE);
            cell.addElement(new Paragraph(header, headerFont));
            table.addCell(cell);
        }

        // Data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Font cellFont    = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, TEXT_LIGHT);
        Font incomeFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, GREEN);
        Font expenseFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, RED);

        boolean alternate = false;
        for (TransactionReportDTO t : transactions) {
            BaseColor rowBg = alternate ? new BaseColor(25, 33, 46) : DARK_BG;
            alternate = !alternate;

            boolean isIncome = t.getCategoryType() == CategoryType.INCOME;
            Font amountFont = isIncome ? incomeFont : expenseFont;

            // Date
            table.addCell(createDataCell(
                    t.getDate().format(formatter), cellFont, rowBg));

            // Category
            table.addCell(createDataCell(
                    t.getCategoryName(), cellFont, rowBg));

            // Description
            table.addCell(createDataCell(
                    t.getDescription() != null ? t.getDescription() : "-",
                    cellFont, rowBg));

            // Type
            table.addCell(createDataCell(
                    t.getCategoryType().name(), amountFont, rowBg));

            // Amount
            table.addCell(createDataCell(
                    String.format("N%,.0f", t.getAmount().doubleValue()),
                    amountFont, rowBg));
        }

        document.add(table);
    }

    private static PdfPCell createDataCell(String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(9);
        cell.addElement(new Paragraph(text, font));
        return cell;
    }

    private static void addFooter(Document document) throws Exception {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9,
                Font.ITALIC, TEXT_MUTED);
        Paragraph footer = new Paragraph(
                "Generated by FinPulse — Your financial heartbeat", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}