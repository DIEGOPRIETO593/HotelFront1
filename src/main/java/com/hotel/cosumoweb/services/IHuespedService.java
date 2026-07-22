package com.hotel.cosumoweb.services;

import java.util.List;

import com.hotel.cosumoweb.model.dto.request.HuespedRequestDto;
import com.hotel.cosumoweb.model.dto.response.HuespedResponseDto; // Importación necesaria

public interface IHuespedService {
	
	List<HuespedResponseDto> listarHuespedes();
	void guardarHuesped(HuespedRequestDto nuevo);
	HuespedResponseDto buscarPorId(Integer id);
	void eliminarHuesped(Integer id);
}