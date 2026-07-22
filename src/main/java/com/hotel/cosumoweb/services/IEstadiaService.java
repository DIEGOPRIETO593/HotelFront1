package com.hotel.cosumoweb.services;

import java.util.List;
import com.hotel.cosumoweb.model.dto.request.EstadiaRequestDto;
import com.hotel.cosumoweb.model.dto.response.EstadiaResponseDto;

public interface IEstadiaService {
    List<EstadiaResponseDto> listarTodos();
    EstadiaResponseDto buscarPorId(int id);
    EstadiaResponseDto guardar(EstadiaRequestDto request);
    void eliminar(int id);
}