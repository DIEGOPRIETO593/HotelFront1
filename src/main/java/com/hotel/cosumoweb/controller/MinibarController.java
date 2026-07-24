package com.hotel.cosumoweb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;
import com.hotel.cosumoweb.model.dto.response.MinibarResponseDto;
import com.hotel.cosumoweb.services.IMinibarService;
import com.hotel.cosumoweb.services.IProductoService;
import com.hotel.cosumoweb.services.IHabitacionService;

@Controller
@RequestMapping("/minibar")
public class MinibarController {
    private final IMinibarService servicio;
    private final IProductoService productoService;
    private final IHabitacionService habitacionService;

    public MinibarController(IMinibarService servicio, IProductoService productoService, IHabitacionService habitacionService) { 
        this.servicio = servicio; 
        this.productoService = productoService;
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("minibares", servicio.listarTodos());
        model.addAttribute("productosList", productoService.listarTodos());
        model.addAttribute("habitacionesList", habitacionService.listarTodos());
        if (!model.containsAttribute("minibar")) {
            model.addAttribute("minibar", new MinibarRequestDto());
        }
        return "minibar/listarminibar";
    }

    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute("minibar") MinibarRequestDto request, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("minibares", servicio.listarTodos());
            model.addAttribute("productosList", productoService.listarTodos());
            model.addAttribute("habitacionesList", habitacionService.listarTodos());
            model.addAttribute("showModal", true);
            return "minibar/listarminibar";
        }
        try {
            servicio.guardar(request);
            redirect.addFlashAttribute("message", crearMensaje("success", "Minibar procesado correctamente."));
        } catch (WebClientResponseException e) {
			String errorMsg = "Error en API Backend: " + e.getStatusCode();
			String body = e.getResponseBodyAsString();
			int idx = body.indexOf("\"message\":\"");
			if (idx != -1) {
				int start = idx + 11;
				int end = body.indexOf("\"", start);
				if (end != -1) {
					errorMsg = body.substring(start, end);
				}
			}
			redirect.addFlashAttribute("message", crearMensaje("danger", errorMsg));
		} catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error."));
        }
        return "redirect:/minibar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            MinibarResponseDto dto = servicio.buscarPorId(id);
            MinibarRequestDto form = new MinibarRequestDto();
            form.setIdMinibar(dto.getIdMinibar().intValue());
            form.setIdHabitacion(dto.getIdHabitacion().intValue());
            form.setIdProducto(dto.getIdProducto().intValue());
            form.setCantidad(dto.getCantidad());
            
            model.addAttribute("minibares", servicio.listarTodos());
            model.addAttribute("productosList", productoService.listarTodos());
            model.addAttribute("habitacionesList", habitacionService.listarTodos());
            model.addAttribute("minibar", form);
            model.addAttribute("showModal", true);
            return "minibar/listarminibar";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró."));
            return "redirect:/minibar";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("message", crearMensaje("success", "Eliminado correctamente."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar."));
        }
        return "redirect:/minibar";
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}
