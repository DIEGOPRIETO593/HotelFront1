package com.hotel.cosumoweb.model.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class DetalleResponseDto {
    private Integer idDetalle;
    private String numeroHabitacion;
    private String nombreHuesped;
    private Integer idEstadia;
    private String estado;
    private Double total;
    private List<DetalleItemResponseDto> items;
}
