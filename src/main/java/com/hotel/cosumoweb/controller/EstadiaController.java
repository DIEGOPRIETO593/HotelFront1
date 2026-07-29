package com.hotel.cosumoweb.controller;

import java.util.HashMap;
import java.util.Map;

import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;
import com.hotel.cosumoweb.model.dto.response.MinibarResponseDto;
import com.hotel.cosumoweb.model.dto.request.DetalleRequestDto;
import com.hotel.cosumoweb.model.dto.response.DetalleResponseDto;
import java.util.List;
import java.util.stream.Collectors;
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
import com.hotel.cosumoweb.services.IMinibarService;
import com.hotel.cosumoweb.services.IDetalleService;

/**
 * Controlador MVC: Gestión de vistas y consumo web para Estadías.
 * Patrón: Model-View-Controller (Spring MVC + Thymeleaf).
 * Responsabilidad: Conectar las vistas HTML (listarestadia.html) con la API REST del backend mediante WebClient.
 * Aplica reglas de seguridad visuales y de servidor como el bloqueo de edición en registros pagados.
 */
@Controller
@RequestMapping("/estadia")
public class EstadiaController {

    private final IEstadiaService servicioEstadia;
    private final IHuespedService servicioHuesped;
    private final IHabitacionService servicioHabitacion;
    private final IMinibarService servicioMinibar;
    private final IDetalleService servicioDetalle;

    public EstadiaController(IEstadiaService servicioEstadia, IHuespedService servicioHuesped, IHabitacionService servicioHabitacion, IMinibarService servicioMinibar, IDetalleService servicioDetalle) {
        this.servicioEstadia = servicioEstadia;
        this.servicioHuesped = servicioHuesped;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioMinibar = servicioMinibar;
        this.servicioDetalle = servicioDetalle;
    }

    // 1. LISTAR PRINCIPAL
    @GetMapping
    /**
     * GET /estadia - Carga la vista principal de estadías.
     * Consume los endpoints REST de estadías, huéspedes y habitaciones mediante WebClient para popular las tablas y modales.
     */
    public String leerEstadia(Model model) {
        cargarListasModel(model);
        if (!model.containsAttribute("estadia")) {
            model.addAttribute("estadia", new EstadiaRequestDto());
        }
        return "estadia/listarestadia";
    }

    @PostMapping("/guardar")
    /**
     * POST /estadia/guardar - Procesa el alta o modificación de una estadía.
     * Seguridad: Fuerza el estado 'Por Cobrar' por defecto para evitar alteraciones manuales o inyección HTML.
     * Manejo de errores: Intercepta WebClientResponseException para decodificar mensajes JSON del backend (ej. Habitación ocupada).
     */
    public String guardar(@Validated @ModelAttribute("estadia") EstadiaRequestDto request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        // 1. VALIDACIÓN MANUAL: La fecha de salida no puede ser menor o igual a la de ingreso
        if (request.getFechaIngreso() != null && request.getFechaSalida() != null) {
            if (!request.getFechaSalida().isAfter(request.getFechaIngreso())) {
                result.rejectValue("fechaSalida", "error.estadia", "La fecha de salida debe ser posterior a la fecha de ingreso.");
            }
        }
        if (result.hasErrors()) {
            cargarListasModel(model);
            model.addAttribute("showModal", true);
            return "estadia/listarestadia";
        }

        try {
            if (request.getIdEstadia() != null && request.getIdEstadia() > 0) {
                try {
                    EstadiaResponseDto existente = servicioEstadia.buscarPorId(request.getIdEstadia());
                    if (existente != null && "Pagado".equalsIgnoreCase(existente.getEstado())) {
                        redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede modificar la estadía: ya fue pagada."));
                        return "redirect:/estadia";
                    }
                } catch (Exception ex) {
                    // Ignore if not found
                }
            }
            request.setEstado("Por Cobrar");
            servicioEstadia.guardar(request);
            redirect.addFlashAttribute("message", crearMensaje("success", "Estadía procesada correctamente."));
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
            redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error al guardar los datos."));
        }

        return "redirect:/estadia";
    }

    @GetMapping("/editar/{id}")
    /**
     * GET /estadia/editar/{id} - Carga los datos de una estadía en el formulario modal.
     * Blindaje: Si la estadía tiene estado 'Pagado', bloquea la edición y retorna una alerta visual Flash.
     */
    public String editarEstadia(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);
            if ("Pagado".equalsIgnoreCase(dtoEncontrado.getEstado())) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede editar: la estadía ya se encuentra en estado 'Pagado'."));
                return "redirect:/estadia";
            }

            EstadiaRequestDto estadiaForm = new EstadiaRequestDto();
            estadiaForm.setIdEstadia(dtoEncontrado.getIdEstadia());
            estadiaForm.setIdHuesped(dtoEncontrado.getIdHuesped());
            estadiaForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
            estadiaForm.setFechaIngreso(dtoEncontrado.getFechaIngreso());
            estadiaForm.setFechaSalida(dtoEncontrado.getFechaSalida());
            estadiaForm.setCantidadHuespedes(dtoEncontrado.getCantidadHuespedes());
            estadiaForm.setTotalPagar(dtoEncontrado.getTotalPagar());
            estadiaForm.setEstado("Por Cobrar");
            estadiaForm.setObservaciones(dtoEncontrado.getObservaciones());
            cargarListasModel(model);
            model.addAttribute("estadia", estadiaForm);
            model.addAttribute("showModal", true);

            return "estadia/listarestadia";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró la estadía a editar."));
            return "redirect:/estadia";
        }
    }

    @GetMapping("/pagar/{id}")
    /**
     * GET /estadia/pagar/{id} - Ejecuta el cobro de la estadía.
     * Regla de negocio: Verifica que no existan cuentas pendientes en Minibar o Servicios.
     * Si todo está liquidado, cambia el estado a 'Pagado' y el backend automáticamente libera la habitación a 'Disponible'.
     */
    public String pagarEstadia(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);

            boolean minibarPendiente = servicioMinibar.listarTodos().stream()
                .anyMatch(m -> dtoEncontrado.getNumeroHabitacion() != null &&
                               dtoEncontrado.getNumeroHabitacion().equals(m.getNumeroHabitacion()) &&
                               !"Pagado".equalsIgnoreCase(m.getEstado()));

            boolean serviciosPendientes = servicioDetalle.listarTodos().stream()
                .anyMatch(d -> dtoEncontrado.getIdEstadia().equals(d.getIdEstadia()) &&
                               !"Pagado".equalsIgnoreCase(d.getEstado()));

            if (minibarPendiente || serviciosPendientes) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede pagar la estadía: existen consumos de Minibar o Servicios pendientes ('Por Cobrar'). Por favor cóbrelos primero."));
                return "redirect:/estadia";
            }

            EstadiaRequestDto estadiaForm = new EstadiaRequestDto();
            estadiaForm.setIdEstadia(dtoEncontrado.getIdEstadia());
            estadiaForm.setIdHuesped(dtoEncontrado.getIdHuesped());
            estadiaForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
            estadiaForm.setFechaIngreso(dtoEncontrado.getFechaIngreso());
            estadiaForm.setFechaSalida(dtoEncontrado.getFechaSalida());
            estadiaForm.setCantidadHuespedes(dtoEncontrado.getCantidadHuespedes());
            estadiaForm.setTotalPagar(dtoEncontrado.getTotalPagar());
            estadiaForm.setEstado("Pagado");
            estadiaForm.setObservaciones(dtoEncontrado.getObservaciones());
            servicioEstadia.guardar(estadiaForm);
            redirect.addFlashAttribute("message", crearMensaje("success", "Estadía cobrada exitosamente (Pagado). Habitación liberada a Disponible."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo procesar el pago de la estadía."));
        }
        return "redirect:/estadia";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEstadia(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);
            if (dtoEncontrado != null && "Pagado".equalsIgnoreCase(dtoEncontrado.getEstado())) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede eliminar: la estadía ya se encuentra en estado 'Pagado'."));
                return "redirect:/estadia";
            }
            servicioEstadia.eliminar(id);
            redirect.addFlashAttribute("message", crearMensaje("success", "Estadía eliminada exitosamente."));
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
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar la estadía."));
        }
        return "redirect:/estadia";
    }

    @PostMapping("/guardarObservaciones")
    public String guardarObservaciones(@org.springframework.web.bind.annotation.RequestParam("idEstadia") Integer id,
                                       @org.springframework.web.bind.annotation.RequestParam("observaciones") String observaciones,
                                       RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);
            if (dtoEncontrado != null && "Pagado".equalsIgnoreCase(dtoEncontrado.getEstado())) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede modificar: la estadía ya fue pagada."));
                return "redirect:/estadia";
            }
            EstadiaRequestDto estadiaForm = new EstadiaRequestDto();
            estadiaForm.setIdEstadia(dtoEncontrado.getIdEstadia());
            estadiaForm.setIdHuesped(dtoEncontrado.getIdHuesped());
            estadiaForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
            estadiaForm.setFechaIngreso(dtoEncontrado.getFechaIngreso());
            estadiaForm.setFechaSalida(dtoEncontrado.getFechaSalida());
            estadiaForm.setCantidadHuespedes(dtoEncontrado.getCantidadHuespedes());
            estadiaForm.setTotalPagar(dtoEncontrado.getTotalPagar());
            estadiaForm.setEstado(dtoEncontrado.getEstado());
            estadiaForm.setObservaciones(observaciones);
            servicioEstadia.guardar(estadiaForm);
            redirect.addFlashAttribute("message", crearMensaje("success", "Observación guardada correctamente."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo guardar la observación."));
        }
        return "redirect:/estadia";
    }

    private void cargarListasModel(Model model) {
        model.addAttribute("estadias", servicioEstadia.listarTodos());
        model.addAttribute("huespedes", servicioHuesped.listarHuespedes()); // O servicioHuesped.listarTodos()
        model.addAttribute("habitaciones", servicioHabitacion.listarTodos());
        model.addAttribute("minibares", servicioMinibar.listarTodos());
        model.addAttribute("detalles", servicioDetalle.listarTodos());
    }

    
    @GetMapping("/pagarTodo/{id}")
    public String pagarTodoEstadia(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);

            // Minibar
            List<MinibarResponseDto> minibares = servicioMinibar.listarTodos().stream()
                .filter(m -> dtoEncontrado.getNumeroHabitacion() != null &&
                             dtoEncontrado.getNumeroHabitacion().equals(m.getNumeroHabitacion()) &&
                             !"Pagado".equalsIgnoreCase(m.getEstado()))
                .collect(Collectors.toList());

            for (MinibarResponseDto m : minibares) {
                MinibarRequestDto form = new MinibarRequestDto();
                form.setIdMinibar(m.getIdMinibar().intValue());
                form.setIdHabitacion(m.getIdHabitacion().intValue());
                form.setEstado("Pagado");
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                java.util.List<Integer> cants = new java.util.ArrayList<>();
                if (m.getDetalles() != null) {
                    for (com.hotel.cosumoweb.model.dto.response.MinibarDetalleResponseDto det : m.getDetalles()) {
                        ids.add(det.getIdProducto().intValue());
                        cants.add(det.getCantidad());
                    }
                }
                form.setIdProductos(ids);
                form.setCantidades(cants);
                servicioMinibar.guardar(form);
            }

            // Servicios
            List<DetalleResponseDto> detalles = servicioDetalle.listarTodos().stream()
                .filter(d -> dtoEncontrado.getIdEstadia().equals(d.getIdEstadia()) &&
                             !"Pagado".equalsIgnoreCase(d.getEstado()))
                .collect(Collectors.toList());

            for (DetalleResponseDto d : detalles) {
                DetalleRequestDto detalleForm = new DetalleRequestDto();
                detalleForm.setIdDetalle(d.getIdDetalle());
                detalleForm.setIdEstadia(d.getIdEstadia());
                detalleForm.setEstado("Pagado");
                if (d.getItems() != null) {
                    for(com.hotel.cosumoweb.model.dto.response.DetalleItemResponseDto item : d.getItems()) {
                        detalleForm.getIdServicios().add(item.getIdServicio());
                        detalleForm.getCantidades().add(item.getCantidad());
                        detalleForm.getTotales().add(item.getTotal() != null ? java.math.BigDecimal.valueOf(item.getTotal()) : java.math.BigDecimal.ZERO);
                    }
                }
                servicioDetalle.guardar(detalleForm);
            }

            // Estadia
            EstadiaRequestDto estadiaForm = new EstadiaRequestDto();
            estadiaForm.setIdEstadia(dtoEncontrado.getIdEstadia());
            estadiaForm.setIdHuesped(dtoEncontrado.getIdHuesped());
            estadiaForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
            estadiaForm.setFechaIngreso(dtoEncontrado.getFechaIngreso());
            estadiaForm.setFechaSalida(dtoEncontrado.getFechaSalida());
            estadiaForm.setCantidadHuespedes(dtoEncontrado.getCantidadHuespedes());
            estadiaForm.setTotalPagar(dtoEncontrado.getTotalPagar());
            estadiaForm.setEstado("Pagado");
            estadiaForm.setObservaciones(dtoEncontrado.getObservaciones());
            servicioEstadia.guardar(estadiaForm);

            redirect.addFlashAttribute("message", crearMensaje("success", "Se han pagado todos los consumos, servicios y la estadía exitosamente."));
            return "redirect:/estadia";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error al intentar pagar todo: " + e.getMessage()));
            return "redirect:/estadia";
        }
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}