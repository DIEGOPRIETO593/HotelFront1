package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleRequestDto {
    private int idDetalle;
    private int idEstadia;
    private int idServicio;
    private int cantidad;
    private BigDecimal subtotal;
}