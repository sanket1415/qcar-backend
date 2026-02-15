package com.qcar.service;

import com.qcar.model.Car;
import com.qcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private QRCodeService qrCodeService;  // Make sure this matches the variable name below

    public Car registerCar(String unitNo, String ownerName, String carNumber, String type) {
        // Validate type
        if (!type.equals("Tenant") && !type.equals("Owner")) {
            throw new IllegalArgumentException("Type must be 'Tenant' or 'Owner'");
        }

        // Validate car number is unique
        if (carRepository.existsByCarNumber(carNumber)) {
            throw new IllegalArgumentException("Car number already exists");
        }

        Car car = new Car();
        car.setQrId(generateUniqueQrId());
        car.setUnitNo(unitNo);
        car.setOwnerName(ownerName);
        car.setCarNumber(carNumber);
        car.setType(type);
        car.setQrColor(qrCodeService.getColorForType(type));  // Fixed: qrCodeService (capital C)

        return carRepository.save(car);
    }

    public boolean deleteCar(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarByQrId(String qrId) {
        return carRepository.findByQrId(qrId);
    }

    private String generateUniqueQrId() {
        String qrId;
        do {
            qrId = UUID.randomUUID().toString().substring(0, 8);
        } while (carRepository.findByQrId(qrId).isPresent());
        return qrId;
    }
}