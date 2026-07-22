package com.hotel.cosumoweb.services.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.hotel.cosumoweb.model.dto.request.ProductoRequestDto;
import com.hotel.cosumoweb.model.dto.response.ProductoResponseDto;
import com.hotel.cosumoweb.services.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService {
    private final WebClient webClient;
    public ProductoServiceImpl(WebClient webClient) { this.webClient = webClient; }
    @Override
    public List<ProductoResponseDto> listarTodos() {
        return webClient.get().uri("/producto").retrieve().bodyToFlux(ProductoResponseDto.class).collectList().block();
    }
    @Override
    public ProductoResponseDto buscarPorId(Integer id) {
        return webClient.get().uri("/producto/{id}", id).retrieve().bodyToMono(ProductoResponseDto.class).block();
    }
    @Override
    public void eliminar(Integer id) {
        webClient.delete().uri("/producto/{id}", id).retrieve().toBodilessEntity().block();
    }
    @Override
    public void guardar(ProductoRequestDto request) {
        if (request.getIdProducto() > 0) {
            webClient.put().uri("/producto/{id}", request.getIdProducto()).bodyValue(request).retrieve().toBodilessEntity().block();
        } else {
            webClient.post().uri("/producto").bodyValue(request).retrieve().toBodilessEntity().block();
        }
    }
}
