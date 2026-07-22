package com.hotel.cosumoweb.services;
import java.util.List;
import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;
import com.hotel.cosumoweb.model.dto.response.MinibarResponseDto;
public interface IMinibarService {
    List<MinibarResponseDto> listarTodos();
    MinibarResponseDto buscarPorId(Integer id);
    void eliminar(Integer id);
    void guardar(MinibarRequestDto request);
}
