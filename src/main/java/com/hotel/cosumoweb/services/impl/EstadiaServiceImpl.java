package com.hotel.cosumoweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotel.cosumoweb.model.dto.request.EstadiaRequestDto;
import com.hotel.cosumoweb.model.dto.response.EstadiaResponseDto;
import com.hotel.cosumoweb.services.IEstadiaService;

@Service
public class EstadiaServiceImpl implements IEstadiaService {

	private final WebClient webClient;

	public EstadiaServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public List<EstadiaResponseDto> listarTodos() {
		return webClient.get().uri("/estadia").retrieve().bodyToFlux(EstadiaResponseDto.class).collectList().block();
	}

	@Override
	public EstadiaResponseDto buscarPorId(int id) {
		return webClient.get().uri("/estadia/{id}", id).retrieve().bodyToMono(EstadiaResponseDto.class).block();
	}

	@Override
	public EstadiaResponseDto guardar(EstadiaRequestDto request) {
		return webClient.post().uri("/estadia").bodyValue(request).retrieve().bodyToMono(EstadiaResponseDto.class)
				.block();
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri("/estadia/{id}", id).retrieve().toBodilessEntity().block();
	}
}