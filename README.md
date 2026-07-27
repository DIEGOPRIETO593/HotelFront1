# Sistema de Gestión Hotelera - Frontend (Cliente Web) 🏨

Este repositorio contiene la aplicación frontend (cliente web) para el Sistema de Gestión Hotelera. Está desarrollada con **Spring Boot 3 (Java 21)** y **Thymeleaf**, y proporciona una interfaz visual moderna, responsiva y de diseño premium para gestionar habitaciones, huéspedes, estadías, catálogo de servicios, consumos de minibar y detalles contables.

---

## 🚀 Tecnologías Utilizadas

- **Java 21 (JDK 21)**
- **Spring Boot 3** (Web, WebFlux para WebClient)
- **Thymeleaf** (Motor de plantillas HTML del lado del servidor)
- **Bootstrap 4 / Stisla UI** (Framework de diseño con extensiones CSS personalizadas)
- **Lombok** (Reducción de código boilerplate)
- **Spring WebClient** (Consumo reactivo y asíncrono de la API REST del backend)
- **Vanilla JavaScript & jQuery** (Motor de búsqueda en tiempo real, cálculos en el cliente y modales dinámicas)

---

## ⚙️ Configuración y Ejecución del Proyecto

A diferencia del backend, este proyecto **no requiere una base de datos propia**, ya que toda la información se obtiene consumiendo la API REST del backend en tiempo real.

### 1. Requisitos del Entorno
- **JDK 21** configurado en variable de entorno (`JAVA_HOME`).
- **Backend (`hotel-master`)** ejecutándose previamente en el puerto `8081`.

### 2. Puerto de Ejecución (`application.properties`)
La aplicación se ejecuta en el puerto **8082** para evitar conflictos con el backend:
```properties
spring.application.name=cosumoweb
server.port=8082
```

### 3. Conexión con el Backend (`webClientConfig.java`)
El frontend se comunica con el backend a través de un Bean de **Spring WebClient**. La URL base está configurada para apuntar al puerto 8081:
```java
@Bean
WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("http://localhost:8081/api").build(); 
}
```

### 4. Compilación y Ejecución
```powershell
# Compilar y validar el código
.\mvnw.cmd clean test-compile

# Levantar el cliente web en puerto 8082
.\mvnw.cmd spring-boot:run
```
Una vez iniciado, accede desde tu navegador web a: **http://localhost:8082**

---

## 📡 Arquitectura y Consumo de la API REST (¿Cómo se conecta con el Backend?)

La aplicación implementa el patrón **MVC (Model-View-Controller)** adaptado para arquitecturas orientadas a servicios (SOA / API REST):

1. **Controladores (`@Controller`)**: Atienden las peticiones HTTP del navegador (ej. `/estadia`, `/minibar`, `/habitaciones`). Manejan la navegación, validación visual y gestión de mensajes Flash.
2. **Servicios y WebClient**: En cada controlador o servicio, se inyecta `WebClient` para disparar peticiones HTTP (`GET`, `POST`, `PUT`, `DELETE`) hacia la API REST del backend (`http://localhost:8081/api/...`).
3. **Mapeo con DTOs (Data Transfer Objects)**:
   - **`ResponseDto`**: Los objetos JSON recibidos desde la API REST se deserializan automáticamente en clases Java de respuesta (ej. `EstadiaResponseDto`, `HabitacionResponseDto`) usando Jackson.
   - **`RequestDto`**: Al enviar formularios HTML, los datos capturados se empaquetan en objetos de petición (ej. `EstadiaRequestDto`) y se envían como JSON en el cuerpo (`body`) de la petición web.
4. **Renderizado Dinámico (Thymeleaf)**: Los objetos DTO se inyectan en el modelo (`Model.addAttribute`) para que las plantillas HTML (en `src/main/resources/templates/...`) construyan dinámicamente las tablas, modales e insignias utilizando sintaxis declarativa (`th:each`, `th:text`, `th:if`).

---

## 💎 Características Premium de Interfaz y Experiencia de Usuario (UX/UI)

1. **Buscador Universal en Tiempo Real (`custom.js`)**:
   - Todas las vistas del sistema (Huéspedes, Habitaciones, Estadías, Minibares, Servicios, Catálogo y Productos) incorporan una barra de búsqueda instantánea inyectada automáticamente sobre las tablas de datos.
   - Filtra filas en tiempo real por cualquier columna o texto coincidente, muestra un contador dinámico de registros visibles y presenta un estado informativo visual ("0 resultados coincidentes") en caso de búsquedas vacías.
2. **Diseño Premium de Formularios (`custom.css`)**:
   - Todos los menús desplegables (`<select>`) del sistema cuentan con un diseño estilizado: flechas chevron SVG en tono morado pastel integradas en el fondo, bordes redondeados modernos, padding optimizado y efectos de sombra/resplandor en eventos de foco y hover.
3. **Gestión de Estados e Inmutabilidad ("Pagado" / "Por Cobrar")**:
   - **Fuerza de Estado Inicial**: Al registrar nuevas estadías o consumos, el sistema asigna por defecto el estado `"Por Cobrar"`, inhabilitando la edición manual de este campo en el formulario de alta.
   - **Blindaje de Registros Pagados**: Cuando una estadía, minibar o servicio pasa a estado `"Pagado"`, el frontend deshabilita visualmente los botones de edición y eliminación (`btn-secondary disabled`), y el controlador intercepta y rechaza cualquier intento de modificación sobre dichos registros.
4. **Cálculos e Interactividad en Tiempo Real**:
   - **Cobro Inteligente de Estadías**: Al hacer clic en el botón *"Pagar"* desde el modal de detalles de estadía, el sistema verifica que todas las cuentas de minibar y servicios hayan sido canceladas o unifica el saldo del cliente antes de liberar la habitación.
   - **Cálculo de Subtotales y Noches**: JavaScript calcula dinámicamente el total de noches de alojamiento y los montos multiplicando cantidad por precio unitario en modales interactivos.

---
*Diseñado por Diego Prieto, Jhoel León y Pablo Torres - Universidad Israel*
