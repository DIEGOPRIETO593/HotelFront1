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

import com.hotel.cosumoweb.model.dto.request.HabitacionRequestDto;
import com.hotel.cosumoweb.model.dto.response.HabitacionResponseDto;
import com.hotel.cosumoweb.services.IHabitacionService;

@Controller
@RequestMapping("/habitacion")
public class habitacionController {

	private final IHabitacionService servicioHabitacion;

	public habitacionController(IHabitacionService servicioHabitacion) {
		this.servicioHabitacion = servicioHabitacion;
	}

	@GetMapping
	public String leerHabitacion(Model model) {
		List<HabitacionResponseDto> resultadoBD = servicioHabitacion.listarTodos();
		model.addAttribute("habitaciones", resultadoBD);
		if (!model.containsAttribute("habitacion")) {
			model.addAttribute("habitacion", new HabitacionRequestDto());
		}
		return "habitacion/listarhabitacion";
	}

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("habitacion") HabitacionRequestDto request, 
			BindingResult result,
			Model model,
			RedirectAttributes redirect) {
		
		if (result.hasErrors()) {
			model.addAttribute("habitaciones", servicioHabitacion.listarTodos());
			model.addAttribute("showModal", true);
			return "habitacion/listarhabitacion";
		}

		try {
			servicioHabitacion.guardar(request);
			redirect.addFlashAttribute("message", crearMensaje("success", "Habitación procesada correctamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API Backend: " + e.getStatusCode()));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error al guardar los datos."));
		}

		return "redirect:/habitacion";
	}

	@GetMapping("/editar/{id}")
	public String editarHabitacion(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			HabitacionResponseDto dtoEncontrado = servicioHabitacion.buscarPorId(id);
			
			HabitacionRequestDto habitacionForm = new HabitacionRequestDto();
			habitacionForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
			habitacionForm.setNumero(dtoEncontrado.getNumero());
			habitacionForm.setEstado(dtoEncontrado.getEstado());
			habitacionForm.setPiso(dtoEncontrado.getPiso());
			habitacionForm.setEstrellas(dtoEncontrado.getEstrellas());
			habitacionForm.setCapacidad(dtoEncontrado.getCapacidad());

			model.addAttribute("habitaciones", servicioHabitacion.listarTodos());
			model.addAttribute("habitacion", habitacionForm);
			model.addAttribute("showModal", true);

			return "habitacion/listarhabitacion";
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró la habitación a editar."));
			return "redirect:/habitacion";
		}
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarHabitacion(@PathVariable("id") Integer id, RedirectAttributes redirect) {
		try {
			servicioHabitacion.eliminar(id);
			redirect.addFlashAttribute("message", crearMensaje("success", "Habitación eliminada exitosamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", 
				crearMensaje("danger", "Error " + e.getStatusCode() + " al eliminar la habitación. Verifique si tiene estadías asociadas."));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar la habitación."));
		}
		return "redirect:/habitacion";
	}

	private Map<String, String> crearMensaje(String type, String text) {
		Map<String, String> msg = new HashMap<>();
		msg.put("type", type);
		msg.put("text", text);
		return msg;
	}
}