import os

base_pkg = 'com.hotel.cosumoweb'
base_dir = r'C:\Universida Israel\6to Semestre\Desarrollo de software\Code\HotelFront1-main\src\main\java\com\hotel\cosumoweb'
resources_dir = r'C:\Universida Israel\6to Semestre\Desarrollo de software\Code\HotelFront1-main\src\main\resources\templates'

files = {
    # ------------------ PRODUCTO DTOs ------------------
    'model/dto/request/ProductoRequestDto.java': '''package {pkg}.model.dto.request;
public class ProductoRequestDto {
    private int idProducto;
    private String nombre;
    private double precio;
    private int stock;
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
'''.replace('{pkg}', base_pkg),

    'model/dto/response/ProductoResponseDto.java': '''package {pkg}.model.dto.response;
public class ProductoResponseDto {
    private Long idProducto;
    private String nombre;
    private Double precio;
    private Integer stock;
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
'''.replace('{pkg}', base_pkg),

    # ------------------ PRODUCTO SERVICE ------------------
    'services/IProductoService.java': '''package {pkg}.services;
import java.util.List;
import {pkg}.model.dto.request.ProductoRequestDto;
import {pkg}.model.dto.response.ProductoResponseDto;
public interface IProductoService {
    List<ProductoResponseDto> listarTodos();
    ProductoResponseDto buscarPorId(Integer id);
    void eliminar(Integer id);
    void guardar(ProductoRequestDto request);
}
'''.replace('{pkg}', base_pkg),

    'services/impl/ProductoServiceImpl.java': '''package {pkg}.services.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import {pkg}.model.dto.request.ProductoRequestDto;
import {pkg}.model.dto.response.ProductoResponseDto;
import {pkg}.services.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService {
    private final WebClient webClient;
    public ProductoServiceImpl(WebClient webClient) { this.webClient = webClient; }
    @Override
    public List<ProductoResponseDto> listarTodos() {
        return webClient.get().uri("/producto").retrieve().bodyToFlux(ProductoResponseDto.class).collectList().block();
    }
    @Override
    public ProductoResponseDto buscarPorId(Integer id) {
        return webClient.get().uri("/producto/{id}", id).retrieve().bodyToMono(ProductoResponseDto.class).block();
    }
    @Override
    public void eliminar(Integer id) {
        webClient.delete().uri("/producto/{id}", id).retrieve().toBodilessEntity().block();
    }
    @Override
    public void guardar(ProductoRequestDto request) {
        if (request.getIdProducto() > 0) {
            webClient.put().uri("/producto/{id}", request.getIdProducto()).bodyValue(request).retrieve().toBodilessEntity().block();
        } else {
            webClient.post().uri("/producto").bodyValue(request).retrieve().toBodilessEntity().block();
        }
    }
}
'''.replace('{pkg}', base_pkg),

    # ------------------ PRODUCTO CONTROLLER ------------------
    'controller/ProductoController.java': '''package {pkg}.controller;
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
import {pkg}.model.dto.request.ProductoRequestDto;
import {pkg}.model.dto.response.ProductoResponseDto;
import {pkg}.services.IProductoService;

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
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API: " + e.getStatusCode()));
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
'''.replace('{pkg}', base_pkg),

    # ------------------ MINIBAR DTOs ------------------
    'model/dto/request/MinibarRequestDto.java': '''package {pkg}.model.dto.request;
public class MinibarRequestDto {
    private int idMinibar;
    private int idHabitacion;
    private int idProducto;
    private int cantidad;
    public int getIdMinibar() { return idMinibar; }
    public void setIdMinibar(int idMinibar) { this.idMinibar = idMinibar; }
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
'''.replace('{pkg}', base_pkg),

    'model/dto/response/MinibarResponseDto.java': '''package {pkg}.model.dto.response;
public class MinibarResponseDto {
    private Long idMinibar;
    private Long idHabitacion;
    private Long idProducto;
    private Integer cantidad;
    public Long getIdMinibar() { return idMinibar; }
    public void setIdMinibar(Long idMinibar) { this.idMinibar = idMinibar; }
    public Long getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Long idHabitacion) { this.idHabitacion = idHabitacion; }
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
'''.replace('{pkg}', base_pkg),

    # ------------------ MINIBAR SERVICE ------------------
    'services/IMinibarService.java': '''package {pkg}.services;
import java.util.List;
import {pkg}.model.dto.request.MinibarRequestDto;
import {pkg}.model.dto.response.MinibarResponseDto;
public interface IMinibarService {
    List<MinibarResponseDto> listarTodos();
    MinibarResponseDto buscarPorId(Integer id);
    void eliminar(Integer id);
    void guardar(MinibarRequestDto request);
}
'''.replace('{pkg}', base_pkg),

    'services/impl/MinibarServiceImpl.java': '''package {pkg}.services.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import {pkg}.model.dto.request.MinibarRequestDto;
import {pkg}.model.dto.response.MinibarResponseDto;
import {pkg}.services.IMinibarService;

@Service
public class MinibarServiceImpl implements IMinibarService {
    private final WebClient webClient;
    public MinibarServiceImpl(WebClient webClient) { this.webClient = webClient; }
    @Override
    public List<MinibarResponseDto> listarTodos() {
        return webClient.get().uri("/minibar").retrieve().bodyToFlux(MinibarResponseDto.class).collectList().block();
    }
    @Override
    public MinibarResponseDto buscarPorId(Integer id) {
        return webClient.get().uri("/minibar/{id}", id).retrieve().bodyToMono(MinibarResponseDto.class).block();
    }
    @Override
    public void eliminar(Integer id) {
        webClient.delete().uri("/minibar/{id}", id).retrieve().toBodilessEntity().block();
    }
    @Override
    public void guardar(MinibarRequestDto request) {
        if (request.getIdMinibar() > 0) {
            webClient.put().uri("/minibar/{id}", request.getIdMinibar()).bodyValue(request).retrieve().toBodilessEntity().block();
        } else {
            webClient.post().uri("/minibar").bodyValue(request).retrieve().toBodilessEntity().block();
        }
    }
}
'''.replace('{pkg}', base_pkg),

    # ------------------ MINIBAR CONTROLLER ------------------
    'controller/MinibarController.java': '''package {pkg}.controller;
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
import {pkg}.model.dto.request.MinibarRequestDto;
import {pkg}.model.dto.response.MinibarResponseDto;
import {pkg}.services.IMinibarService;
import {pkg}.services.IProductoService;
import {pkg}.services.IHabitacionService;

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
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error en API: " + e.getStatusCode()));
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
'''.replace('{pkg}', base_pkg)
}

import os
for rel_path, content in files.items():
    full_path = os.path.join(base_dir, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, 'w', encoding='utf-8') as f:
        f.write(content)
print("Java files generated.")
