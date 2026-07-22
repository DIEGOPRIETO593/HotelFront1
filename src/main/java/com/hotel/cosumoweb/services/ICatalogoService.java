package com.hotel.cosumoweb.services;

import java.util.List;
import com.hotel.cosumoweb.model.dto.request.CatalogoRequestDto;
import com.hotel.cosumoweb.model.dto.response.CatalogoResponseDto;

public interface ICatalogoService {
    List<CatalogoResponseDto> listarTodos();
    CatalogoResponseDto buscarPorId(int id);
    CatalogoResponseDto guardar(CatalogoRequestDto request);
    void eliminar(int id);
}