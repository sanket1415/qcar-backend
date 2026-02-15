package com.qcar.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    public byte[] generateQRCode(String text, String colorHex, int size) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

        BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Color qrColor = Color.decode(colorHex);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? qrColor.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Add logo in center
        try {
            InputStream logoStream = getClass().getResourceAsStream("/static/logo.png");
            if (logoStream != null) {
                BufferedImage logo = ImageIO.read(logoStream);

                // Logo size = 20% of QR size
                int logoSize = size / 5;
                Image scaledLogo = logo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);

                Graphics2D g = qrImage.createGraphics();
                int logoX = (size - logoSize) / 2;
                int logoY = (size - logoSize) / 2;

                // White background for logo
                g.setColor(Color.WHITE);
                g.fillRect(logoX - 5, logoY - 5, logoSize + 10, logoSize + 10);

                // Draw logo
                g.drawImage(scaledLogo, logoX, logoY, null);
                g.dispose();
            }
        } catch (Exception e) {
            // If logo fails, continue without it
            System.out.println("Logo not found, generating QR without logo");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        return baos.toByteArray();
    }

    public String getColorForType(String type) {
        return "Tenant".equals(type) ? "#800000" : "#00008B"; // Maroon : DarkBlue
    }
}