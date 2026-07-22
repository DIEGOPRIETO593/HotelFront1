package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CatalogoRequestDto {
    private int idServicio;
    private String nombreServicio;
    private BigDecimal tarifa;
}