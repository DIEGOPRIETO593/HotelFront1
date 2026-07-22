package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class EstadiaRequestDto {
    private int idEstadia;
    private int idHuesped;
    private int idHabitacion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaIngreso;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaSalida;

    private int cantidadHuespedes;
    private BigDecimal totalPagar;
}