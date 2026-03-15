package dev.muthukumar.ai_crm.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import dev.muthukumar.ai_crm.model.Certificate;
import dev.muthukumar.ai_crm.model.Invoice;
import dev.muthukumar.ai_crm.model.InvoiceItem;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfGeneratorUtil {

    private static final BaseColor PRIMARY = new BaseColor(30, 64, 175);
    private static final BaseColor GOLD    = new BaseColor(234, 179, 8);
    private static final BaseColor LIGHT   = new BaseColor(239, 246, 255);
    private static final BaseColor DARK    = new BaseColor(17, 24, 39);
    private static final BaseColor MUTED   = new BaseColor(107, 114, 128);

    // ── Certificate PDF ──────────────────────────────────────────────────────
    public byte[] generateCertificatePdf(Certificate cert) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        PdfContentByte canvas = writer.getDirectContent();
        drawBorder(canvas, doc);

        Font orgFont  = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, PRIMARY);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.ITALIC, GOLD);
        Font nameFont  = new Font(Font.FontFamily.TIMES_ROMAN, 28, Font.BOLD, PRIMARY);
        Font bodyFont  = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, DARK);
        Font subFont   = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, MUTED);

        addCenter(doc, "TECHNO INFORMATICS", orgFont, 20f);
        doc.add(new Chunk(new LineSeparator(2, 55, GOLD, Element.ALIGN_CENTER, -5)));
        addCenter(doc, "Certificate of Completion", titleFont, 14f);
        addCenter(doc, "This is to certify that", bodyFont, 12f);
        addCenter(doc, cert.getStudentName(), nameFont, 6f);

        if (cert.getCollegeName() != null)
            addCenter(doc, (cert.getRollNo() != null ? cert.getRollNo() + "  |  " : "") + cert.getCollegeName(), subFont, 4f);

        String label = switch (cert.getCategory() != null ? cert.getCategory() : "COURSE") {
            case "INTERN"  -> "has successfully completed the internship program:";
            case "PROJECT" -> "has successfully delivered the project:";
            default        -> "has successfully completed the course:";
        };
        addCenter(doc, label, bodyFont, 12f);

        Font progFont = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD, PRIMARY);
        addCenter(doc, cert.getProgramTitle() != null ? cert.getProgramTitle() : "", progFont, 4f);

        if (cert.getDomainName() != null)
            addCenter(doc, "Domain: " + cert.getDomainName(), subFont, 4f);
        if (cert.getStartDate() != null && cert.getEndDate() != null)
            addCenter(doc, "Duration: " + cert.getStartDate() + "  to  " + cert.getEndDate(), subFont, 4f);
        if (cert.getGrade() != null && !cert.getGrade().isBlank()) {
            Font gradeFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, GOLD);
            addCenter(doc, "Grade: " + cert.getGrade(), gradeFont, 4f);
        }

        doc.add(Chunk.NEWLINE);
        doc.add(new Chunk(new LineSeparator(1, 75, GOLD, Element.ALIGN_CENTER, -5)));

        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(85);
        footer.setSpacingBefore(14f);
        PdfPCell left = new PdfPCell(); left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Phrase("Certificate No: " + cert.getCertificateNumber(), subFont));
        left.addElement(new Phrase("Issued: " + cert.getIssuedDate(), subFont));
        PdfPCell right = new PdfPCell(); right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Font signFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, PRIMARY);
        right.addElement(new Phrase("\n\n________________________", signFont));
        right.addElement(new Phrase("Authorised Signature", subFont));
        footer.addCell(left); footer.addCell(right);
        doc.add(footer);

        doc.close();
        return out.toByteArray();
    }

    // ── Invoice PDF ───────────────────────────────────────────────────────────
    public byte[] generateInvoicePdf(Invoice inv) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font wht  = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD, BaseColor.WHITE);
        Font whtS = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
        Font hdr  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font td   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK);
        Font label = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, MUTED);
        Font val   = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, DARK);
        Font grand = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        // Header band
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3, 2});

        PdfPCell compCell = new PdfPCell();
        compCell.setBackgroundColor(PRIMARY); compCell.setBorder(Rectangle.NO_BORDER); compCell.setPadding(14);
        compCell.addElement(new Phrase("TECHNO INFORMATICS", wht));
        compCell.addElement(new Phrase("contact@technoinformatics.in", whtS));

        PdfPCell invCell = new PdfPCell();
        invCell.setBackgroundColor(GOLD); invCell.setBorder(Rectangle.NO_BORDER); invCell.setPadding(14);
        invCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invCell.addElement(new Phrase("INVOICE", wht));
        invCell.addElement(new Phrase("# " + inv.getInvoiceNumber(), whtS));
        invCell.addElement(new Phrase("Date: " + inv.getInvoiceDate(), whtS));
        if (inv.getDueDate() != null) invCell.addElement(new Phrase("Due: " + inv.getDueDate(), whtS));
        header.addCell(compCell); header.addCell(invCell);
        doc.add(header);
        doc.add(Chunk.NEWLINE);

        // Bill to
        if (inv.getClientName() != null) {
            PdfPTable bill = new PdfPTable(1); bill.setWidthPercentage(45); bill.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell bc = new PdfPCell(); bc.setBackgroundColor(LIGHT); bc.setPadding(10);
            Font bLabel = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, PRIMARY);
            bc.addElement(new Phrase("Bill To:", bLabel));
            bc.addElement(new Phrase(inv.getClientName(), td));
            if (inv.getClientEmail() != null) bc.addElement(new Phrase(inv.getClientEmail(), td));
            if (inv.getClientPhone() != null) bc.addElement(new Phrase(inv.getClientPhone(), td));
            bill.addCell(bc); doc.add(bill); doc.add(Chunk.NEWLINE);
        }

        // Items table
        if (inv.getItems() != null && !inv.getItems().isEmpty()) {
            PdfPTable items = new PdfPTable(5);
            items.setWidthPercentage(100);
            items.setWidths(new float[]{1, 5, 2, 2, 2});
            for (String h : new String[]{"#", "Description", "Qty", "Unit Price", "Total"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, hdr));
                c.setBackgroundColor(PRIMARY); c.setPadding(7);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                items.addCell(c);
            }
            int i = 1;
            for (InvoiceItem item : inv.getItems()) {
                BaseColor bg = (i % 2 == 0) ? LIGHT : BaseColor.WHITE;
                addItemCell(items, String.valueOf(i++), td, bg, Element.ALIGN_CENTER);
                addItemCell(items, item.getDescription(), td, bg, Element.ALIGN_LEFT);
                addItemCell(items, String.valueOf(item.getQuantity()), td, bg, Element.ALIGN_CENTER);
                addItemCell(items, "Rs. " + item.getUnitPrice(), td, bg, Element.ALIGN_RIGHT);
                addItemCell(items, "Rs. " + item.getTotalPrice(), td, bg, Element.ALIGN_RIGHT);
            }
            doc.add(items);
        }

        doc.add(Chunk.NEWLINE);

        // Totals
        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(42);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setWidths(new float[]{3, 2});
        addTotalRow(totals, "Subtotal:", "Rs. " + inv.getSubtotal(), label, val, BaseColor.WHITE);
        if (inv.getDiscount() != null && inv.getDiscount().doubleValue() > 0)
            addTotalRow(totals, "Discount:", "- Rs. " + inv.getDiscount(), label, val, BaseColor.WHITE);
        if (inv.getTaxPercent() != null && inv.getTaxPercent().doubleValue() > 0)
            addTotalRow(totals, "Tax (" + inv.getTaxPercent() + "%):", "Rs. " + inv.getTaxAmount(), label, val, BaseColor.WHITE);
        addTotalRow(totals, "Total:", "Rs. " + inv.getTotalAmount(), grand, grand, PRIMARY);
        addTotalRow(totals, "Balance Due:", "Rs. " + inv.getBalanceDue(), label,
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(220, 38, 38)), BaseColor.WHITE);
        doc.add(totals);

        if (inv.getNotes() != null && !inv.getNotes().isBlank()) {
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Note: " + inv.getNotes(), new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, MUTED)));
        }

        doc.add(Chunk.NEWLINE);
        doc.add(new Chunk(new LineSeparator(1, 100, GOLD, Element.ALIGN_CENTER, -5)));
        Paragraph foot = new Paragraph("Thank you for your business! — Computer generated invoice.", new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, MUTED));
        foot.setAlignment(Element.ALIGN_CENTER);
        doc.add(foot);

        doc.close();
        return out.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void addCenter(Document doc, String text, Font font, float before) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(before);
        doc.add(p);
    }

    private void drawBorder(PdfContentByte c, Document doc) {
        Rectangle page = doc.getPageSize();
        float m = 18f;
        c.setColorStroke(PRIMARY); c.setLineWidth(4f);
        c.rectangle(m, m, page.getWidth() - 2*m, page.getHeight() - 2*m); c.stroke();
        c.setColorStroke(GOLD); c.setLineWidth(1.5f);
        c.rectangle(m+6, m+6, page.getWidth()-2*(m+6), page.getHeight()-2*(m+6)); c.stroke();
    }

    private void addItemCell(PdfPTable t, String text, Font font, BaseColor bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg); c.setPadding(6);
        c.setHorizontalAlignment(align);
        t.addCell(c);
    }

    private void addTotalRow(PdfPTable t, String lbl, String val, Font lFont, Font vFont, BaseColor bg) {
        PdfPCell l = new PdfPCell(new Phrase(lbl, lFont));
        l.setPadding(5); l.setBorder(Rectangle.NO_BORDER); l.setBackgroundColor(bg);
        PdfPCell v = new PdfPCell(new Phrase(val, vFont));
        v.setPadding(5); v.setBorder(Rectangle.NO_BORDER);
        v.setHorizontalAlignment(Element.ALIGN_RIGHT); v.setBackgroundColor(bg);
        t.addCell(l); t.addCell(v);
    }
}
