package com.hotel.cosumoweb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hotel.cosumoweb.model.dto.response.DetalleItemResponseDto;
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

	@PostMapping("/guardar")
	public String guardar(@Validated @ModelAttribute("detalle") DetalleRequestDto request, BindingResult result,
			Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {
			cargarListasModel(model);
			model.addAttribute("showModal", true);
			return "detalle/listardetalle";
		}

		try {
			boolean isNew = (request.getIdDetalle() == null || request.getIdDetalle() == 0);
			DetalleResponseDto existente = null;
			if (!isNew) {
				try {
					existente = servicioDetalle.buscarPorId(request.getIdDetalle());
					if (existente != null && "Pagado".equalsIgnoreCase(existente.getEstado())) {
						redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede modificar el consumo: ya fue pagado."));
						return "redirect:/detalle";
					}
				} catch (Exception ex) {
					isNew = true;
				}
			}

			if (request.getEstado() == null) {
				request.setEstado("Por Cobrar");
			}

			servicioDetalle.guardar(request);
			redirect.addFlashAttribute("message", crearMensaje("success", "Consumo de servicio guardado correctamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API Backend: " + e.getStatusCode()));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurri un error al guardar los datos."));
		}

		return "redirect:/detalle";
	}

	@GetMapping("/editar/{id}")
	public String editarDetalle(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			DetalleResponseDto dto = servicioDetalle.buscarPorId(id);
			if (dto != null && "Pagado".equalsIgnoreCase(dto.getEstado())) {
				redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede editar: el consumo ya est pagado."));
				return "redirect:/detalle";
			}

			DetalleRequestDto detalleForm = new DetalleRequestDto();
			detalleForm.setIdDetalle(dto.getIdDetalle());
			detalleForm.setIdEstadia(dto.getIdEstadia());
			detalleForm.setEstado(dto.getEstado());
			
			if (dto.getItems() != null) {
				for(DetalleItemResponseDto item : dto.getItems()) {
					detalleForm.getIdServicios().add(item.getIdServicio());
					detalleForm.getCantidades().add(item.getCantidad());
					detalleForm.getTotales().add(item.getTotal() != null ? java.math.BigDecimal.valueOf(item.getTotal()) : java.math.BigDecimal.ZERO);
				}
			}

			cargarListasModel(model);
			model.addAttribute("detalle", detalleForm);
			model.addAttribute("showModal", true);

			return "detalle/listardetalle";
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontr el consumo."));
			return "redirect:/detalle";
		}
	}

	@GetMapping("/pagar/{id}")
	public String pagarDetalle(@PathVariable("id") Integer id, RedirectAttributes redirect) {
		try {
			DetalleResponseDto dto = servicioDetalle.buscarPorId(id);
			DetalleRequestDto form = new DetalleRequestDto();
			form.setIdDetalle(dto.getIdDetalle());
			form.setIdEstadia(dto.getIdEstadia());
			form.setEstado("Pagado");
			if(dto.getItems() != null) {
				for(DetalleItemResponseDto item : dto.getItems()) {
					form.getIdServicios().add(item.getIdServicio());
					form.getCantidades().add(item.getCantidad());
					form.getTotales().add(item.getTotal() != null ? java.math.BigDecimal.valueOf(item.getTotal()) : java.math.BigDecimal.ZERO);
				}
			}
			servicioDetalle.guardar(form);
			redirect.addFlashAttribute("message", crearMensaje("success", "Servicios consumidos cobrados (Pagados)."));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo procesar el pago de los servicios."));
		}
		return "redirect:/detalle";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarDetalle(@PathVariable("id") Integer id, RedirectAttributes redirect) {
		try {
			DetalleResponseDto dto = servicioDetalle.buscarPorId(id);
			if (dto != null && "Pagado".equalsIgnoreCase(dto.getEstado())) {
				redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede eliminar: el consumo ya se encuentra en estado 'Pagado'."));
				return "redirect:/detalle";
			}
			servicioDetalle.eliminar(id);
			redirect.addFlashAttribute("message", crearMensaje("success", "Consumo eliminado exitosamente."));
		} catch (WebClientResponseException e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "Error " + e.getStatusCode() + " al eliminar el detalle."));
		} catch (Exception e) {
			redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar el consumo."));
		}
		return "redirect:/detalle";
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
