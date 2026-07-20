package com.hotel.cosumoweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotel.cosumoweb.model.dto.request.DetalleRequestDto;
import com.hotel.cosumoweb.model.dto.response.DetalleResponseDto;
import com.hotel.cosumoweb.services.IDetalleService;

@Service
public class DetalleServiceImpl implements IDetalleService {

    private final WebClient webClient;

    public DetalleServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<DetalleResponseDto> listarTodos() {
        return webClient.get()
                .uri("/detalle")
                .retrieve()
                .bodyToFlux(DetalleResponseDto.class)
                .collectList()
                .block();
    }

    @Override
    public DetalleResponseDto buscarPorId(int id) {
        return webClient.get()
                .uri("/detalle/{id}", id)
                .retrieve()
                .bodyToMono(DetalleResponseDto.class)
                .block();
    }

    @Override
    public DetalleResponseDto guardar(DetalleRequestDto request) {
        return webClient.post()
                .uri("/detalle")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DetalleResponseDto.class)
                .block();
    }

    @Override
    public void eliminar(int id) {
        webClient.delete()
                .uri("/detalle/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}