package com.hotel.cosumoweb.model.dto.response;

import lombok.Data;

@Data
public class HabitacionResponseDto {
	private int idHabitacion;
	private String numero;
	private String estado;
	private int piso;
	private int estrellas;
	private int capacidad;
    private Double precio;

   
}