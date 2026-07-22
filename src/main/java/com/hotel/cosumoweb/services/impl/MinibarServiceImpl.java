package com.hotel.cosumoweb.services.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;
import com.hotel.cosumoweb.model.dto.response.MinibarResponseDto;
import com.hotel.cosumoweb.services.IMinibarService;

@Service
public class MinibarServiceImpl implements IMinibarService {
    private final WebClient webClient;
    public MinibarServiceImpl(WebClient webClient) { this.webClient = webClient; }
    @Override
    public List<MinibarResponseDto> listarTodos() {
        return webClient.get().uri("/minibar").retrieve().bodyToFlux(MinibarResponseDto.class).collectList().block();
    }
    @Override
    public MinibarResponseDto buscarPorId(Integer id) {
        return webClient.get().uri("/minibar/{id}", id).retrieve().bodyToMono(MinibarResponseDto.class).block();
    }
    @Override
    public void eliminar(Integer id) {
        webClient.delete().uri("/minibar/{id}", id).retrieve().toBodilessEntity().block();
    }
    @Override
    public void guardar(MinibarRequestDto request) {
        if (request.getIdMinibar() > 0) {
            webClient.put().uri("/minibar/{id}", request.getIdMinibar()).bodyValue(request).retrieve().toBodilessEntity().block();
        } else {
            webClient.post().uri("/minibar").bodyValue(request).retrieve().toBodilessEntity().block();
        }
    }
}
