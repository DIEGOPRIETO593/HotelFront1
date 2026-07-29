package com.hotel.cosumoweb.controller;

import java.util.ArrayList;
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
import com.hotel.cosumoweb.model.dto.response.MinibarDetalleResponseDto;
import com.hotel.cosumoweb.model.dto.request.ProductoRequestDto;
import com.hotel.cosumoweb.model.dto.response.ProductoResponseDto;
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
            boolean isNew = (request.getIdMinibar() == 0);
            MinibarResponseDto existente = null;
            if (!isNew) {
                try {
                    existente = servicio.buscarPorId(request.getIdMinibar());
                    if (existente != null && "Pagado".equalsIgnoreCase(existente.getEstado())) {
                        redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede modificar el consumo: ya fue pagado."));
                        return "redirect:/minibar";
                    }
                } catch (Exception ex) {
                    isNew = true;
                }
            }

            Map<Integer, Integer> stockReq = new HashMap<>();
            if (request.getIdProductos() != null && request.getCantidades() != null) {
                for (int i = 0; i < request.getIdProductos().size(); i++) {
                    int pid = request.getIdProductos().get(i);
                    int qty = request.getCantidades().get(i);
                    stockReq.put(pid, stockReq.getOrDefault(pid, 0) + qty);
                }
            }

            if (existente != null && existente.getDetalles() != null) {
                for (MinibarDetalleResponseDto det : existente.getDetalles()) {
                    int pid = det.getIdProducto().intValue();
                    stockReq.put(pid, stockReq.getOrDefault(pid, 0) - det.getCantidad());
                }
            }

            for (Map.Entry<Integer, Integer> entry : stockReq.entrySet()) {
                if (entry.getValue() > 0) {
                    ProductoResponseDto p = productoService.buscarPorId(entry.getKey());
                    if (p.getStock() < entry.getValue()) {
                        redirect.addFlashAttribute("message", crearMensaje("warning", "No hay suficiente stock del producto: " + p.getNombre() + " (Disponible: " + p.getStock() + ")"));
                        return "redirect:/minibar";
                    }
                }
            }

            servicio.guardar(request);

            for (Map.Entry<Integer, Integer> entry : stockReq.entrySet()) {
                if (entry.getValue() != 0) {
                    ProductoResponseDto p = productoService.buscarPorId(entry.getKey());
                    ProductoRequestDto pReq = new ProductoRequestDto();
                    pReq.setIdProducto(p.getIdProducto().intValue());
                    pReq.setNombre(p.getNombre());
                    pReq.setPrecio(p.getPrecio() != null ? p.getPrecio() : 0.0);
                    pReq.setStock(p.getStock() - entry.getValue());
                    productoService.guardar(pReq);
                }
            }
            
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
            if ("Pagado".equalsIgnoreCase(dto.getEstado())) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede editar: el consumo ya se encuentra en estado 'Pagado'."));
                return "redirect:/minibar";
            }
            MinibarRequestDto form = new MinibarRequestDto();
            form.setIdMinibar(dto.getIdMinibar().intValue());
            form.setIdHabitacion(dto.getIdHabitacion().intValue());
            
            List<Integer> ids = new ArrayList<>();
            List<Integer> cants = new ArrayList<>();
            if (dto.getDetalles() != null) {
                for (MinibarDetalleResponseDto det : dto.getDetalles()) {
                    ids.add(det.getIdProducto().intValue());
                    cants.add(det.getCantidad());
                }
            }
            form.setIdProductos(ids);
            form.setCantidades(cants);
            
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

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            MinibarResponseDto dto = servicio.buscarPorId(id);
            MinibarRequestDto form = new MinibarRequestDto();
            form.setIdMinibar(dto.getIdMinibar().intValue());
            form.setIdHabitacion(dto.getIdHabitacion().intValue());
            form.setEstado("Pagado");
            
            List<Integer> ids = new ArrayList<>();
            List<Integer> cants = new ArrayList<>();
            if (dto.getDetalles() != null) {
                for (MinibarDetalleResponseDto det : dto.getDetalles()) {
                    ids.add(det.getIdProducto().intValue());
                    cants.add(det.getCantidad());
                }
            }
            form.setIdProductos(ids);
            form.setCantidades(cants);
            
            servicio.guardar(form);
            redirect.addFlashAttribute("message", crearMensaje("success", "Consumo de Minibar cobrado (Pagado)."));
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "No se pudo procesar el pago del minibar."));
        }
        return "redirect:/minibar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        try {
            MinibarResponseDto dto = servicio.buscarPorId(id);
            if (dto != null && "Pagado".equalsIgnoreCase(dto.getEstado())) {
                redirect.addFlashAttribute("message", crearMensaje("warning", "No se puede eliminar: el consumo ya se encuentra en estado 'Pagado'."));
                return "redirect:/minibar";
            }
            servicio.eliminar(id);
            
            if (dto != null && dto.getDetalles() != null) {
                for (MinibarDetalleResponseDto det : dto.getDetalles()) {
                    try {
                        ProductoResponseDto prod = productoService.buscarPorId(det.getIdProducto().intValue());
                        ProductoRequestDto pReq = new ProductoRequestDto();
                        pReq.setIdProducto(prod.getIdProducto().intValue());
                        pReq.setNombre(prod.getNombre());
                        pReq.setPrecio(prod.getPrecio() != null ? prod.getPrecio() : 0.0);
                        pReq.setStock(prod.getStock() + det.getCantidad());
                        productoService.guardar(pReq);
                    } catch (Exception ex) {}
                }
            }
            redirect.addFlashAttribute("message", crearMensaje("success", "Eliminado correctamente. Stock restaurado."));
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
