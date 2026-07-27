package com.hotel.cosumoweb.model.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetalleResponseDto {
    private Integer idDetalle;
    
    private Integer idEstadia;
    private Integer idServicio;
    private String nombreServicio;
    
    private Integer cantidad;
    private BigDecimal total;
    private String numeroHabitacion;
    private String nombreHuesped;
    private String estado;
}
