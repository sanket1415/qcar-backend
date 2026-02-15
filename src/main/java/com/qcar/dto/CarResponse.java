package com.qcar.dto;

import lombok.Data;

@Data
public class CarResponse {
    private Long id;
    private String qrId;
    private String unitNo;
    private String ownerName;
    private String carNumber;
    private String type;
    private String qrColor;
}