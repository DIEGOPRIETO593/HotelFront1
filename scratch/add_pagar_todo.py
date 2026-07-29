import sys

path = r'C:\Universida Israel\6to Semestre\Desarrollo de software\Code\HotelFront1-main\src\main\java\com\hotel\cosumoweb\controller\EstadiaController.java'

with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

imports = '''import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;
import com.hotel.cosumoweb.model.dto.response.MinibarResponseDto;
import com.hotel.cosumoweb.model.dto.request.DetalleRequestDto;
import com.hotel.cosumoweb.model.dto.response.DetalleResponseDto;
import java.util.List;
import java.util.stream.Collectors;
'''

if 'import com.hotel.cosumoweb.model.dto.request.MinibarRequestDto;' not in content:
    content = content.replace('import org.springframework.beans.factory.annotation.Autowired;', imports + 'import org.springframework.beans.factory.annotation.Autowired;')

method = '''
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
                form.setIdProducto(m.getIdProducto().intValue());
                form.setCantidad(m.getCantidad());
                form.setEstado("Pagado");
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
                detalleForm.setIdServicio(d.getIdServicio());
                detalleForm.setCantidad(d.getCantidad());
                detalleForm.setTotal(d.getTotal());
                detalleForm.setEstado("Pagado");
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
            servicioEstadia.actualizar(id, estadiaForm);

            redirect.addFlashAttribute("message", crearMensaje("success", "Se han pagado todos los consumos, servicios y la estadía exitosamente."));
            return "redirect:/estadia";
        } catch (Exception e) {
            redirect.addFlashAttribute("message", crearMensaje("danger", "Error al intentar pagar todo: " + e.getMessage()));
            return "redirect:/estadia";
        }
    }
'''

if 'pagarTodoEstadia' not in content:
    content = content.replace('private Map<String, String> crearMensaje(', method + '\n    private Map<String, String> crearMensaje(')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
