package com.hotel.cosumoweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotel.cosumoweb.model.dto.request.HuespedRequestDto;
import com.hotel.cosumoweb.model.dto.response.HuespedResponseDto;
import com.hotel.cosumoweb.services.IHuespedService;

@Service
public class HuespedServiceImpll implements IHuespedService { 

	private final WebClient webClient;

	public HuespedServiceImpll(WebClient webClient) { 
		this.webClient = webClient;
	}

	@Override
	public List<HuespedResponseDto> listarHuespedes() {
		return webClient.get()
				.uri("/huesped")
				.retrieve()
				.bodyToFlux(HuespedResponseDto.class)
				.collectList()
				.block();
	}

	@Override
	public HuespedResponseDto buscarPorId(Integer id) { 
		return webClient.get()
				.uri("/huesped/{id}", id)
				.retrieve()
				.bodyToMono(HuespedResponseDto.class)
				.block();
	}

	@Override
	public void eliminarHuesped(Integer id) { 
		webClient.delete()
				.uri("/huesped/{id}", id)
				.retrieve()
				.toBodilessEntity()
				.block();
	}

	@Override
	public void guardarHuesped(HuespedRequestDto nuevo) {
		webClient.post()
				.uri("/huesped")
				.bodyValue(nuevo)
				.retrieve()
				.toBodilessEntity()
				.block();
	}
}