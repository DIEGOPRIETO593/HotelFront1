package com.hotel.cosumoweb.model.dto.request;
import java.util.List;
public class MinibarRequestDto {
    private int idMinibar;
    private int idHabitacion;
    private List<Integer> idProductos;
    private List<Integer> cantidades;
    private String estado;
    public int getIdMinibar() { return idMinibar; }
    public void setIdMinibar(int idMinibar) { this.idMinibar = idMinibar; }
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public List<Integer> getIdProductos() { return idProductos; }
    public void setIdProductos(List<Integer> idProductos) { this.idProductos = idProductos; }
    public List<Integer> getCantidades() { return cantidades; }
    public void setCantidades(List<Integer> cantidades) { this.cantidades = cantidades; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
