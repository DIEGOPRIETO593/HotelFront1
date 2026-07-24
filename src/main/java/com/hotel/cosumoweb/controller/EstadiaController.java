package com.hotel.cosumoweb.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.cosumoweb.model.dto.request.EstadiaRequestDto;
import com.hotel.cosumoweb.model.dto.response.EstadiaResponseDto;
import com.hotel.cosumoweb.services.IEstadiaService;
import com.hotel.cosumoweb.services.IHabitacionService;
import com.hotel.cosumoweb.services.IHuespedService;

@Controller
@RequestMapping("/estadia")
public class EstadiaController {

    private final IEstadiaService servicioEstadia;
    private final IHuespedService servicioHuesped;
    private final IHabitacionService servicioHabitacion;

    public EstadiaController(IEstadiaService servicioEstadia, IHuespedService servicioHuesped, IHabitacionService servicioHabitacion) {
        this.servicioEstadia = servicioEstadia;
        this.servicioHuesped = servicioHuesped;
        this.servicioHabitacion = servicioHabitacion;
    }

    // 1. LISTAR PRINCIPAL
    @GetMapping
    public String leerEstadia(Model model) {
        cargarListasModel(model);
        if (!model.containsAttribute("estadia")) {
            model.addAttribute("estadia", new EstadiaRequestDto());
        }
        return "estadia/listarestadia";
    }

    private void cargarListasModel(Model model) {
        model.addAttribute("estadias", servicioEstadia.listarTodos());
        model.addAttribute("huespedes", servicioHuesped.listarHuespedes()); // O servicioHuesped.listarTodos()
        model.addAttribute("habitaciones", servicioHabitacion.listarTodos());
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}