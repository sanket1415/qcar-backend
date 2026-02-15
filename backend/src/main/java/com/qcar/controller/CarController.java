package com.qcar.controller;

import com.qcar.model.Car;
import com.qcar.service.CarService;
import com.qcar.service.PDFService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.ByteArrayOutputStream;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {
    
    @Autowired
    private CarService carService;
    
    @Autowired
    private PDFService pdfService;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCar(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Check authentication
        if (session.getAttribute("adminId") == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            Car car = carService.registerCar(
                request.get("unitNo"),
                request.get("ownerName"),
                request.get("carNumber"),
                request.get("type")
            );
            
            response.put("success", true);
            response.put("message", "Car registered successfully");
            response.put("car", car);
            response.put("qrUrl", baseUrl + "/car/" + car.getQrId());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCars(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        // Check authentication
        if (session.getAttribute("adminId") == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }
        
        List<Car> cars = carService.getAllCars();
        response.put("success", true);
        response.put("cars", cars);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCar(
            @PathVariable Long id,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (session.getAttribute("adminId") == null) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        }

        try {
            boolean deleted = carService.deleteCar(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Car deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Car not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/qr/pdf/all")
    public ResponseEntity<byte[]> downloadAllQRPDFs(HttpSession session) {

        // Check authentication
        if (session.getAttribute("adminId") == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            List<Car> cars = carService.getAllCars();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (Car car : cars) {
                String qrUrl = baseUrl + "/car/" + car.getQrId();
                byte[] pdfBytes = pdfService.generateQRPDF(qrUrl, car.getType(), car.getCarNumber());

                ZipEntry zipEntry = new ZipEntry("QR-" + car.getCarNumber() + ".pdf");
                zos.putNextEntry(zipEntry);
                zos.write(pdfBytes);
                zos.closeEntry();
            }

            zos.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "all-qr-codes.zip");

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/qr/pdf/{qrId}")
    public ResponseEntity<byte[]> downloadQRPDF(
            @PathVariable String qrId,
            HttpSession session) {
        
        // Check authentication
        if (session.getAttribute("adminId") == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            Car car = carService.getCarByQrId(qrId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
            
            String qrUrl = baseUrl + "/car/" + qrId;
            byte[] pdfBytes = pdfService.generateQRPDF(qrUrl, car.getType(), car.getCarNumber());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "qr-" + car.getCarNumber() + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
