package com.hotel.cosumoweb.model.dto.response;

import lombok.Data;

@Data
public class DetalleItemResponseDto {
    private Integer idItem;
    private Integer idServicio;
    private String nombreServicio;
    private Integer cantidad;
    private Double total;
}
