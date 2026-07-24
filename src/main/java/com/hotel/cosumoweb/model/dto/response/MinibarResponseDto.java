package com.hotel.cosumoweb.model.dto.response;

public class MinibarResponseDto {
    private Long idMinibar;
    private Long idHabitacion;
    private Long idProducto;
    private Integer cantidad;
    private String numeroHabitacion;
    private String nombreProducto;
    private Double total;

    public Long getIdMinibar() { return idMinibar; }
    public void setIdMinibar(Long idMinibar) { this.idMinibar = idMinibar; }
    
    public Long getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Long idHabitacion) { this.idHabitacion = idHabitacion; }
    
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
