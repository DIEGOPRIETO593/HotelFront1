import os

templates = {
    'habitacion/listarhabitacion.html': 'habitacionModal',
    'estadia/listarestadia.html': 'estadiaModal',
    'catalogo/listarcatalogo.html': 'catalogoModal',
    'minibar/listarminibar.html': 'minibarModal',
    'detalle/listardetalle.html': 'detalleModal',
    'producto/listarproducto.html': 'productoModal'
}

base_dir = r'C:\Universida Israel\6to Semestre\Desarrollo de software\Code\HotelFront1-main\src\main\resources\templates'

for path, modal_id in templates.items():
    full_path = os.path.join(base_dir, path)
    if os.path.exists(full_path):
        with open(full_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Replace data-bs-toggle with both data-toggle and data-bs-toggle
        content = content.replace(
            f'data-bs-toggle=\"modal\" data-bs-target=\"#{modal_id}\"',
            f'data-bs-toggle=\"modal\" data-bs-target=\"#{modal_id}\" data-toggle=\"modal\" data-target=\"#{modal_id}\"'
        )
        
        with open(full_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f'Fixed {path}')
