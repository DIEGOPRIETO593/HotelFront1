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

import com.hotel.cosumoweb.model.dto.request.DetalleRequestDto;
import com.hotel.cosumoweb.model.dto.response.DetalleResponseDto;
import com.hotel.cosumoweb.services.ICatalogoService;
import com.hotel.cosumoweb.services.IDetalleService;
import com.hotel.cosumoweb.services.IEstadiaService;

@Controller
@RequestMapping("/detalle")
public class DetalleController {

	private final IDetalleService servicioDetalle;
	private final IEstadiaService servicioEstadia;
	private final ICatalogoService servicioCatalogo;

	public DetalleController(IDetalleService servicioDetalle, IEstadiaService servicioEstadia, ICatalogoService servicioCatalogo) {
		this.servicioDetalle = servicioDetalle;
		this.servicioEstadia = servicioEstadia;
		this.servicioCatalogo = servicioCatalogo;
	}

	@GetMapping
	public String leerDetalle(Model model) {
		cargarListasModel(model);
		if (!model.containsAttribute("detalle")) {
			model.addAttribute("detalle", new DetalleRequestDto());
		}
		return "detalle/listardetalle";
	}

	private void cargarListasModel(Model model) {
		model.addAttribute("detalles", servicioDetalle.listarTodos());
		model.addAttribute("estadias", servicioEstadia.listarTodos());
		model.addAttribute("servicios", servicioCatalogo.listarTodos());
	}

	private Map<String, String> crearMensaje(String type, String text) {
		Map<String, String> msg = new HashMap<>();
		msg.put("type", type);
		msg.put("text", text);
		return msg;
	}
}
