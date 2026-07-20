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

    @Autowired
    private ICatalogoService Catalogo;

    // 1. LISTAR PRINCIPAL
    @GetMapping
    public String leerCatalogo(Model model) {
        List<CatalogoResponseDto> resultadoBD = Catalogo.listarTodos();
        model.addAttribute("s", resultadoBD);
        if (!model.containsAttribute("")) {
            model.addAttribute("", new CatalogoRequestDto());
        }
        return "catalogo/listarcatalogo";
    }

    // 2. GUARDAR / ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(@Validated @ModelAttribute("") CatalogoRequestDto request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            model.addAttribute("s", Catalogo.listarTodos());
            model.addAttribute("showModal", true);
            return "catalogo/listarcatalogo";
        }

        try {
            Catalogo.guardar(request);
            redirect.addFlashAttribute("message", crearMensaje("success", " procesado correctamente."));
        } catch (WebClientResponseException e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API Backend: " + e.getStatusCode()));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Ocurrió un error al guardar los datos."));
        }

        return "redirect:/catalogo";
    }

    // 3. CARGAR DATOS PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editarCatalogo(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
        try {
            CatalogoResponseDto dtoEncontrado = Catalogo.buscarPorId(id);

            CatalogoRequestDto Form = new CatalogoRequestDto();
            Form.setIdServicio(dtoEncontrado.getIdServicio());
            Form.setNombreServicio(dtoEncontrado.getNombreServicio());
            Form.setTarifa(dtoEncontrado.getTarifa());

            model.addAttribute("s", Catalogo.listarTodos());
            model.addAttribute("", Form);
            model.addAttribute("showModal", true);

            return "catalogo/listarcatalogo";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se encontró el  a editar."));
            return "redirect:/catalogo";
        }
    }

    // 4. ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarCatalogo(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            Catalogo.eliminar(id);
            redirect.addFlashAttribute("message", crearMensaje("success", " eliminado exitosamente."));
        } catch (WebClientResponseException e) {
            redirect.addFlashAttribute("message",
                crearMensaje("danger", "Error " + e.getStatusCode() + " al eliminar el . Verifique si tiene consumos asignados en estadías."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo eliminar el ."));
        }
        return "redirect:/catalogo";
    }

    private Map<String, String> crearMensaje(String type, String text) {
        Map<String, String> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("text", text);
        return msg;
    }
}