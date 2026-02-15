package com.qcar.controller;

import com.qcar.model.Car;
import com.qcar.service.CarService;
import com.qcar.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicController {

    @Autowired
    private CarService carService;

    @Autowired
    private ExcelService excelService;

    @GetMapping("/car/{qrId}")
    public String getCarDetails(@PathVariable String qrId, Model model) {
        Car car = carService.getCarByQrId(qrId).orElse(null);

        if (car == null) {
            model.addAttribute("error", "Car not found");
            return "error";
        }

        // Log scan to Excel
        excelService.logQRScan(car.getCarNumber(), car.getOwnerName(), car.getUnitNo());

        model.addAttribute("car", car);
        return "car-details";
    }
}