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

import com.hotel.cosumoweb.model.dto.request.HuespedRequestDto;
import com.hotel.cosumoweb.model.dto.response.HuespedResponseDto;
import com.hotel.cosumoweb.services.IHuespedService;

@Controller
@RequestMapping("/huesped")
public class huespedController {

	@Autowired
	private IHuespedService serviciohuesped;

	// 1. LISTAR PRINCIPAL
	@GetMapping
	public String leerhuesped(Model model) {
		List<HuespedResponseDto> resultadoBD = serviciohuesped.listarHuespedes();
		model.addAttribute("huespedes", resultadoBD);
		if (!model.containsAttribute("huesped")) {
			model.addAttribute("huesped", new HuespedRequestDto());
		}
		return "huesped/listarhuesped";
	}

	// 2. GUARDAR / ACTUALIZAR
	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("huesped") HuespedRequestDto request, 
			BindingResult result,
			Model model,
			RedirectAttributes redirect) {
		
		// Si hay errores de validación, volvemos a renderizar la tabla con el modal abierto
		if (result.hasErrors()) {
			model.addAttribute("huespedes", serviciohuesped.listarHuespedes());
			model.addAttribute("showModal", true);
			return "huesped/listarhuesped";
		}

		try {
			serviciohuesped.guardarHuesped(request);
			redirect.addFlashAttribute("message", crearMensaje("success", "Huésped procesado correctamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API Backend: " + e.getStatusCode()));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error al guardar los datos."));
		}

		return "redirect:/huesped";
	}

	// 3. CARGAR DATOS PARA EDITAR
	@GetMapping("/editar/{id}")
	public String editarhuesped(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			HuespedResponseDto dtoEncontrado = serviciohuesped.buscarPorId(id);
			
			HuespedRequestDto huespedForm = new HuespedRequestDto();
			huespedForm.setIdHuesped(dtoEncontrado.getIdHuesped());
			huespedForm.setCedula(dtoEncontrado.getCedula());
			huespedForm.setNombre(dtoEncontrado.getNombre());
			huespedForm.setApellido(dtoEncontrado.getApellido());
			huespedForm.setTelefono(dtoEncontrado.getTelefono());

			model.addAttribute("huespedes", serviciohuesped.listarHuespedes());
			model.addAttribute("huesped", huespedForm);
			model.addAttribute("showModal", true);

			return "huesped/listarhuesped";
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró el huésped a editar."));
			return "redirect:/huesped";
		}
	}

	// 4. ELIMINAR
	@GetMapping("/eliminar/{id}")
	public String eliminarhuesped(@PathVariable("id") Integer id, RedirectAttributes redirect) {
		try {
			serviciohuesped.eliminarHuesped(id);
			redirect.addFlashAttribute("message", crearMensaje("success", "Huésped eliminado exitosamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", 
				crearMensaje("danger", "Error " + e.getStatusCode() + " al eliminar el huésped. Verifique si tiene reservaciones asociadas."));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar el huésped."));
		}
		return "redirect:/huesped";
	}

	// Método auxiliar para crear objetos de alerta compatibles con Thymeleaf
	private Map<String, String> crearMensaje(String type, String text) {
		Map<String, String> msg = new HashMap<>();
		msg.put("type", type);
		msg.put("text", text);
		return msg;
	}
}