package com.hotel.cosumoweb.model.dto.request;
public class MinibarRequestDto {
    private int idMinibar;
    private int idHabitacion;
    private int idProducto;
    private int cantidad;
    public int getIdMinibar() { return idMinibar; }
    public void setIdMinibar(int idMinibar) { this.idMinibar = idMinibar; }
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
