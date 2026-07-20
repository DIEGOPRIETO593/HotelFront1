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

    @Autowired
    private IEstadiaService servicioEstadia;

    @Autowired
    private IHuespedService servicioHuesped;

    @Autowired
    private IHabitacionService servicioHabitacion;

    // 1. LISTAR PRINCIPAL
    @GetMapping
    public String leerEstadia(Model model) {
        cargarListasModel(model);
        if (!model.containsAttribute("estadia")) {
            model.addAttribute("estadia", new EstadiaRequestDto());
        }
        return "estadia/listarestadia";
    }

    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute("estadia") EstadiaRequestDto request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            cargarListasModel(model);
            model.addAttribute("showModal", true);
            return "estadia/listarestadia";
        }

        try {
            servicioEstadia.guardar(request);
            redirect.addFlashAttribute("message", crearMensaje("success", "Estadía procesada correctamente."));
        } catch (WebClientResponseException e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API Backend: " + e.getStatusCode()));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error al guardar los datos."));
        }

        return "redirect:/estadia";
    }

    @GetMapping("/editar/{id}")
    public String editarEstadia(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            EstadiaResponseDto dtoEncontrado = servicioEstadia.buscarPorId(id);

            EstadiaRequestDto estadiaForm = new EstadiaRequestDto();
            estadiaForm.setIdEstadia(dtoEncontrado.getIdEstadia());
            estadiaForm.setIdHuesped(dtoEncontrado.getIdHuesped());
            estadiaForm.setIdHabitacion(dtoEncontrado.getIdHabitacion());
            estadiaForm.setFechaIngreso(dtoEncontrado.getFechaIngreso());
            estadiaForm.setFechaSalida(dtoEncontrado.getFechaSalida());
            estadiaForm.setCantidadHuespedes(dtoEncontrado.getCantidadHuespedes());
            estadiaForm.setTotalPagar(dtoEncontrado.getTotalPagar());

            cargarListasModel(model);
            model.addAttribute("estadia", estadiaForm);
            model.addAttribute("showModal", true);

            return "estadia/listarestadia";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró la estadía a editar."));
            return "redirect:/estadia";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEstadia(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            servicioEstadia.eliminar(id);
            redirect.addFlashAttribute("message", crearMensaje("success", "Estadía eliminada exitosamente."));
        } catch (WebClientResponseException e) {
            redirect.addFlashAttribute("message",
                crearMensaje("danger", "Error " + e.getStatusCode() + " al eliminar la estadía. Verifique si tiene detalles de servicio asociados."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar la estadía."));
        }
        return "redirect:/estadia";
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