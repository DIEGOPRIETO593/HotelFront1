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
import com.hotel.cosumoweb.model.dto.request.ProductoRequestDto;
import com.hotel.cosumoweb.model.dto.response.ProductoResponseDto;
import com.hotel.cosumoweb.services.IProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {
    private final IProductoService servicio;
    public ProductoController(IProductoService servicio) { this.servicio = servicio; }

    @GetMapping
    public String listar(Model model) {
        List<ProductoResponseDto> lista = servicio.listarTodos();
        model.addAttribute("productos", lista);
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new ProductoRequestDto());
        }
        return "producto/listarproducto";
    }

    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute("producto") ProductoRequestDto request, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("productos", servicio.listarTodos());
            model.addAttribute("showModal", true);
            return "producto/listarproducto";
        }
        try {
            servicio.guardar(request);
            redirect.addFlashAttribute("message", crearMensaje("success", "Producto procesado correctamente."));
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
        return "redirect:/producto";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            ProductoResponseDto dto = servicio.buscarPorId(id);
            ProductoRequestDto form = new ProductoRequestDto();
            form.setIdProducto(dto.getIdProducto().intValue());
            form.setNombre(dto.getNombre());
            form.setPrecio(dto.getPrecio());
            form.setStock(dto.getStock());
            model.addAttribute("productos", servicio.listarTodos());
            model.addAttribute("producto", form);
            model.addAttribute("showModal", true);
            return "producto/listarproducto";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró."));
            return "redirect:/producto";
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
        return "redirect:/producto";
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}
