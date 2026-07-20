package com.hotel.cosumoweb.services;

import java.util.List;
import com.hotel.cosumoweb.model.dto.request.DetalleRequestDto;
import com.hotel.cosumoweb.model.dto.response.DetalleResponseDto;

public interface IDetalleService {
    List<DetalleResponseDto> listarTodos();
    DetalleResponseDto buscarPorId(int id);
    DetalleResponseDto guardar(DetalleRequestDto request);
    void eliminar(int id);
}