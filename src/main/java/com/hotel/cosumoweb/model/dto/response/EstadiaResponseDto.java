package com.hotel.cosumoweb.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EstadiaResponseDto {
    private int idEstadia;
    
    private int idHuesped;
    private String nombreHuesped;     
    private int idHabitacion;
    private String numeroHabitacion; 

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private int cantidadHuespedes;
    private BigDecimal totalPagar;
}