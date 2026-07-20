package com.hotel.cosumoweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotel.cosumoweb.model.dto.request.CatalogoRequestDto;
import com.hotel.cosumoweb.model.dto.response.CatalogoResponseDto;
import com.hotel.cosumoweb.services.ICatalogoService;

@Service
public class CatalogoServiceImpl implements ICatalogoService {

    private final WebClient webClient;

    public CatalogoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<CatalogoResponseDto> listarTodos() {
        return webClient.get()
                .uri("/catalogo")
                .retrieve()
                .bodyToFlux(CatalogoResponseDto.class)
                .collectList()
                .block();
    }

    @Override
    public CatalogoResponseDto buscarPorId(int id) {
        return webClient.get()
                .uri("/catalogo/{id}", id)
                .retrieve()
                .bodyToMono(CatalogoResponseDto.class)
                .block();
    }

    @Override
    public CatalogoResponseDto guardar(CatalogoRequestDto request) {
        return webClient.post()
                .uri("/catalogo")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CatalogoResponseDto.class)
                .block();
    }

    @Override
    public void eliminar(int id) {
        webClient.delete()
                .uri("/catalogo/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}