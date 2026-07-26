package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CatalogoRequestDto {
    private Integer idServicio = 0;
    private String nombreServicio;
    private BigDecimal tarifa;
    private String descripcion;
}