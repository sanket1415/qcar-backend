package com.qcar.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cars")
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String qrId;
    
    @Column(nullable = false)
    private String unitNo;
    
    @Column(nullable = false)
    private String ownerName;
    
    @Column(unique = true, nullable = false)
    private String carNumber;
    
    @Column(nullable = false)
    private String type; // "Tenant" or "Owner"
    
    @Column(nullable = false)
    private String qrColor; // "Maroon" or "DarkBlue"
}
