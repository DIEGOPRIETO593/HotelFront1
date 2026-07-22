package com.hotel.cosumoweb.services;
import java.util.List;
import com.hotel.cosumoweb.model.dto.request.ProductoRequestDto;
import com.hotel.cosumoweb.model.dto.response.ProductoResponseDto;
public interface IProductoService {
    List<ProductoResponseDto> listarTodos();
    ProductoResponseDto buscarPorId(Integer id);
    void eliminar(Integer id);
    void guardar(ProductoRequestDto request);
}
