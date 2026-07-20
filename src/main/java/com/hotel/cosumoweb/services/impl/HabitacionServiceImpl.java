package com.hotel.cosumoweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotel.cosumoweb.model.dto.request.HabitacionRequestDto;
import com.hotel.cosumoweb.model.dto.response.HabitacionResponseDto;
import com.hotel.cosumoweb.services.IHabitacionService;

@Service
public class HabitacionServiceImpl implements IHabitacionService {

	private final WebClient webClient;

	public HabitacionServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public List<HabitacionResponseDto> listarTodos() {
		return webClient.get().uri("/habitacion").retrieve().bodyToFlux(HabitacionResponseDto.class).collectList()
				.block();
	}

	@Override
	public HabitacionResponseDto buscarPorId(int id) {
		return webClient.get().uri("/habitacion/{id}", id).retrieve().bodyToMono(HabitacionResponseDto.class).block();
	}

	@Override
	public HabitacionResponseDto guardar(HabitacionRequestDto request) {
		return webClient.post().uri("/habitacion").bodyValue(request).retrieve().bodyToMono(HabitacionResponseDto.class)
				.block();
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri("/habitacion/{id}", id).retrieve().toBodilessEntity().block();
	}

}