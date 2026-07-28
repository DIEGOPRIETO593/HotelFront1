package com.hotel.cosumoweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.cosumoweb.model.dto.response.HabitacionResponseDto;
import com.hotel.cosumoweb.services.IDetalleService;
import com.hotel.cosumoweb.services.IHabitacionService;
import com.hotel.cosumoweb.services.IMinibarService;

@Controller
@RequestMapping("/home")
public class HomeController {
	@Autowired private IHabitacionService servicioHabitacion;
    @Autowired private IDetalleService servicioDetalle;
    @Autowired private IMinibarService servicioMinibar;

    @GetMapping
    public String mostrarDashboard(Model model) {
       
        List<HabitacionResponseDto> habitaciones = servicioHabitacion.listarTodos();

        // Conteo con validación nula y limpia de espacios
        long disponibles = habitaciones.stream()
                .filter(h -> h.getEstado() != null && h.getEstado().trim().equalsIgnoreCase("Disponible"))
                .count();
                
        long ocupadas = habitaciones.stream()
                .filter(h -> h.getEstado() != null && (h.getEstado().trim().equalsIgnoreCase("Ocupado") || h.getEstado().trim().equalsIgnoreCase("Ocupada")))
                .count();
                
        long mantenimiento = habitaciones.stream()
                .filter(h -> h.getEstado() != null && h.getEstado().trim().equalsIgnoreCase("Mantenimiento"))
                .count();

        // Pasar contadores al Modelo
        model.addAttribute("cantDisponibles", disponibles);
        model.addAttribute("cantOcupadas", ocupadas);
        model.addAttribute("cantMantenimiento", mantenimiento);

        // También enviamos las listas por si las necesitas en tus tablas
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("detalles", servicioDetalle.listarTodos());
        model.addAttribute("minibares", servicioMinibar.listarTodos());

        return "home/home"; 
    }
}