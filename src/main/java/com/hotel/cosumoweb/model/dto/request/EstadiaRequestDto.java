package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class EstadiaRequestDto {
    private Integer idEstadia = 0;
    private Integer idHuesped;
    private Integer idHabitacion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaIngreso;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaSalida;

    private Integer cantidadHuespedes;
    private BigDecimal totalPagar;
}