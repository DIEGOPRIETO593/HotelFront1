# Sistema de Gestión Hotelera - Frontend (Cliente Web)

Este repositorio contiene la aplicación frontend (cliente web) para el Sistema de Gestión Hotelera. Está desarrollada con **Spring Boot** y **Thymeleaf**, y se encarga de proporcionar la interfaz de usuario para gestionar habitaciones, huéspedes, estadías, catálogos de servicios, detalles de consumos y minibares.

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot** (Web, WebFlux para WebClient)
- **Thymeleaf** (Motor de plantillas)
- **Bootstrap / Stisla** (Diseño y UI)
- **Lombok** (Reducción de código boilerplate)
- **WebClient** (Consumo de API REST)

---

## ⚙️ Configuración del Proyecto

A diferencia del backend, este proyecto **no requiere una base de datos propia**, ya que toda la información se obtiene consumiendo la API REST del backend.

### 1. Puerto de Ejecución
La aplicación está configurada para ejecutarse en el puerto **8082** para evitar conflictos con el backend.
`properties
# application.properties
spring.application.name=cosumoweb
server.port=8082
`

### 2. Conexión con el Backend (API REST)
El frontend se comunica con el backend a través de **Spring WebClient**. La URL base de la API está configurada en la clase webClientConfig.java.

- **URL del Backend Esperada:** http://localhost:8081/api

Si necesitas cambiar el puerto o la URL del backend en el futuro, debes modificar el archivo src/main/java/com/hotel/cosumoweb/configuration/webClientConfig.java:
`java
@Bean
WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("http://localhost:8081/api").build(); 
}
`

---

## 📡 ¿Cómo consume los datos?

La aplicación sigue el patrón MVC (Model-View-Controller) modificado para consumir una API:

1. **Controladores (@Controller)**: Atienden las peticiones HTTP del navegador (ej. /estadia, /detalle).
2. **Servicios (@Service)**: Utilizan WebClient para hacer peticiones HTTP (GET, POST, PUT, DELETE) hacia la URL del backend (http://localhost:8081/api/...).
3. **DTOs (Data Transfer Objects)**: Los datos JSON recibidos del backend se mapean automáticamente a objetos de Java (ResponseDto) usando Jackson. Cuando se envían datos desde un formulario, se empaquetan en un RequestDto.
4. **Vistas (Thymeleaf)**: Los Controladores pasan estos objetos DTO al modelo (Model), y Thymeleaf se encarga de renderizar el HTML dinámico utilizando expresiones como ${estadia.nombreHuesped}.

---

## 💼 Reglas de Negocio y Lógicas del Frontend

Aunque el backend aplica las reglas de persistencia, el frontend maneja ciertas reglas y facilidades de experiencia de usuario (UX):

- **Cálculos Automáticos en Tiempo Real**:
  - **Detalle de Estadía (Consumo de Servicios)**: Al seleccionar un servicio de la lista y colocar una cantidad, el frontend calcula automáticamente el **Subtotal** (Precio del Servicio * Cantidad) usando JavaScript antes de enviarlo al servidor.
  - **Minibar**: Funciona de la misma manera; al seleccionar un producto (ej. Gaseosa) e ingresar la cantidad, calcula el costo total del minibar a facturar en la estadía.
  - **Cálculo de Noches (Estadía)**: Al crear una nueva estadía, el frontend usa un script para calcular y mostrar de inmediato el número de noches en base a la fecha de ingreso y salida seleccionadas.

- **Manejo de Formularios y Modales**:
  - Todos los CRUDs utilizan ventanas modales de Bootstrap en lugar de redireccionar a otra página, ofreciendo una experiencia tipo "Single Page Application".
  - Se pasan los datos (data-id, data-huesped, etc.) hacia las modales de visualización de detalles (ej. "Factura de Servicio" o "Resumen de Estadía") usando JavaScript nativo.

- **Validaciones Básicas**:
  - Se utilizan atributos equired y min="1" u min="0" en los campos numéricos y de fechas para asegurar que el usuario no envíe formularios vacíos o incoherentes hacia el backend.

---

## ▶️ Cómo Ejecutar el Proyecto

1. Asegúrate de tener **levantado primero el proyecto Backend** en el puerto 8081.
2. Clona este repositorio y ábrelo en tu IDE (Eclipse, IntelliJ, VSCode).
3. Espera que Maven descargue las dependencias.
4. Ejecuta la clase principal CosumowebApplication.java.
5. Abre en tu navegador web: **http://localhost:8082**
