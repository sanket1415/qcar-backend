package com.qcar.repository;

import com.qcar.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByQrId(String qrId);
    Optional<Car> findByCarNumber(String carNumber);
    boolean existsByCarNumber(String carNumber);
}
