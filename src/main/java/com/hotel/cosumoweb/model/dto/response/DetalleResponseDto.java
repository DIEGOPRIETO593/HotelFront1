package com.hotel.cosumoweb.model.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleResponseDto {
    private int idDetalle;
    
    private int idEstadia;
    private int idServicio;
    private String nombreServicio;
    
    private int cantidad;
    private BigDecimal total;
    private String numeroHabitacion;
    private String nombreHuesped;
}
