package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleRequestDto {
    private Integer idDetalle = 0;
    private Integer idEstadia;
    private Integer idServicio;
    private Integer cantidad;
    private BigDecimal total;
}