package com.hotel.cosumoweb.model.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DetalleRequestDto {
    private Integer idDetalle = 0;
    private Integer idEstadia;
    private Integer idServicio;
    private Integer cantidad;
    private BigDecimal total;
    private String estado;
    
    // Arrays for multiple selection
    private List<Integer> idServicios = new ArrayList<>();
    private List<Integer> cantidades = new ArrayList<>();
    private List<BigDecimal> totales = new ArrayList<>();
}