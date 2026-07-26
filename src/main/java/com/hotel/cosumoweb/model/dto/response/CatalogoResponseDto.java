package com.hotel.cosumoweb.model.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CatalogoResponseDto {
    private Integer idServicio;
    private String nombreServicio;
    private BigDecimal tarifa;
    private String descripcion;
}