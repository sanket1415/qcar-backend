package com.qcar.dto;

import lombok.Data;

@Data
public class CarRequest {
    private String unitNo;
    private String ownerName;
    private String carNumber;
    private String type;
}