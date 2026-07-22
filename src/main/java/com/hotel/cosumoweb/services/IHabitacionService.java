package com.hotel.cosumoweb.services;

import com.hotel.cosumoweb.model.dto.request.HabitacionRequestDto;
import com.hotel.cosumoweb.model.dto.response.HabitacionResponseDto;
import java.util.List;

public interface IHabitacionService {
    List<HabitacionResponseDto> listarTodos();
    HabitacionResponseDto buscarPorId(int id);
    HabitacionResponseDto guardar(HabitacionRequestDto request);
    void eliminar(int id);
}