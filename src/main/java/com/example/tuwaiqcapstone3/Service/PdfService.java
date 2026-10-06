package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {


    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // lang = "ar" or "en"
    public byte[] createPlanPdf(User user, List<Ride> rides, String lang) {
        boolean ar = "ar".equalsIgnoreCase(lang);

        try {
            // Amiri has both Arabic and Latin letters, so one font works for both languages
            byte[] fontBytes = new ClassPathResource("fonts/Amiri-Regular.ttf").getInputStream().readAllBytes();
            BaseFont baseFont = BaseFont.createFont("Amiri-Regular.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, true, fontBytes, null);

            Font titleFont = new Font(baseFont, 20);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font cellFont = new Font(baseFont, 11);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // title (one-cell table so it can be right-to-left)
            String title = ar ? "خطة المباريات - " + user.getName() : "Match Plan for " + user.getName();
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            PdfPCell titleCell = cell(title, titleFont, ar);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setPaddingBottom(15);
            if (ar) {
                titleTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            }
            titleTable.addCell(titleCell);
            document.add(titleTable);

            // main table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            if (ar) {
                table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            }

            String[] headers = ar
                    ? new String[]{"المباراة", "وقت المباراة", "وقت الانطلاق", "نقطة التجمع", "الوجهة"}
                    : new String[]{"Match", "Match time", "Departure", "Meeting point", "Destination"};

            for (String h : headers) {
                table.addCell(cell(h, headerFont, ar));
            }

            for (Ride ride : rides) {
                String match = ride.getMatch().getHomeTeam() + " vs " + ride.getMatch().getAwayTeam();
                String matchTime = ride.getMatch().getStartTime().format(FORMAT);
                String departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime()).format(FORMAT);

                table.addCell(cell(match, cellFont, ar));
                table.addCell(cell(matchTime, cellFont, ar));
                table.addCell(cell(departure, cellFont, ar));
                table.addCell(cell(ride.getMeetingPoint(), cellFont, ar));
                table.addCell(cell(ride.getDestination(), cellFont, ar));
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new ApiException("Failed to create the PDF: " + e.getMessage());
        }
    }

    private PdfPCell cell(String text, Font font, boolean ar) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        if (ar) {
            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        }
        return cell;
    }
}
