package com.hotel.cosumoweb.model.dto.response;
public class MinibarResponseDto {
    private Long idMinibar;
    private Long idHabitacion;
    private Long idProducto;
    private Integer cantidad;
    public Long getIdMinibar() { return idMinibar; }
    public void setIdMinibar(Long idMinibar) { this.idMinibar = idMinibar; }
    public Long getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Long idHabitacion) { this.idHabitacion = idHabitacion; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
