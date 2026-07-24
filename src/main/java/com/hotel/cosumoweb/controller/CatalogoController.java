package com.hotel.cosumoweb.controller;

import java.util.HashMap;
import java.util.List;
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

import com.hotel.cosumoweb.model.dto.request.CatalogoRequestDto;
import com.hotel.cosumoweb.model.dto.response.CatalogoResponseDto;
import com.hotel.cosumoweb.services.ICatalogoService;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final ICatalogoService servicioCatalogo;

    public CatalogoController(ICatalogoService servicioCatalogo) {
        this.servicioCatalogo = servicioCatalogo;
    }

    @GetMapping
    public String leerCatalogo(Model model) {
        List<CatalogoResponseDto> resultadoBD = servicioCatalogo.listarTodos();
        model.addAttribute("servicios", resultadoBD);
        if (!model.containsAttribute("servicio")) {
            model.addAttribute("servicio", new CatalogoRequestDto());
        }
        return "catalogo/listarcatalogo";
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}