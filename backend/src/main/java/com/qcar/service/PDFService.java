package com.qcar.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {

    @Autowired
    private QRCodeService qrCodeService;

    public byte[] generateQRPDF(String qrUrl, String type, String carNumber) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Set border color based on type
        Color borderColor = "Tenant".equals(type)
                ? new DeviceRgb(128, 0, 0)      // Maroon
                : new DeviceRgb(0, 0, 139);      // Dark Blue

        // Title
        Paragraph title = new Paragraph("QCAR")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Subtitle
        Paragraph subtitle = new Paragraph("Car QR Management System")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        // Generate and add QR code
        String colorHex = qrCodeService.getColorForType(type);
        byte[] qrImage = qrCodeService.generateQRCode(qrUrl, colorHex, 300);
        Image qr = new Image(ImageDataFactory.create(qrImage))
                .setWidth(300)
                .setHeight(300)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBorder(new SolidBorder(borderColor, 5));
        document.add(qr);

        // Car number
        Paragraph carNum = new Paragraph("Car: " + carNumber)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(15);
        document.add(carNum);

        // Type badge
        Paragraph typeBadge = new Paragraph(type.toUpperCase())
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(borderColor);
        document.add(typeBadge);

        document.close();
        return baos.toByteArray();
    }
}