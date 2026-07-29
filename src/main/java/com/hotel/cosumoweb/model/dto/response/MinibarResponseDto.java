package com.hotel.cosumoweb.model.dto.response;
import java.util.List;
public class MinibarResponseDto {
    private Long idMinibar;
    private Long idHabitacion;
    private String numeroHabitacion;
    private Double total;
    private String estado;
    private List<MinibarDetalleResponseDto> detalles;
    
    public Long getIdMinibar() { return idMinibar; }
    public void setIdMinibar(Long idMinibar) { this.idMinibar = idMinibar; }
    public Long getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Long idHabitacion) { this.idHabitacion = idHabitacion; }
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<MinibarDetalleResponseDto> getDetalles() { return detalles; }
    public void setDetalles(List<MinibarDetalleResponseDto> detalles) { this.detalles = detalles; }
}
