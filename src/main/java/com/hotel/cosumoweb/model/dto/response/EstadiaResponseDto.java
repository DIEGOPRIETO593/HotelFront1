package com.hotel.cosumoweb.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EstadiaResponseDto {
    private Integer idEstadia;
    
    private Integer idHuesped;
    private String nombreHuesped;     
    private Integer idHabitacion;
    private String numeroHabitacion; 

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private Integer cantidadHuespedes;
    private BigDecimal totalPagar;
    private String estado;
    private String observaciones;
    
    public long getDias() {
        if (fechaIngreso != null && fechaSalida != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);
            return dias < 1 ? 1 : dias;
        }
        return 0;
    }
}