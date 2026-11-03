# Convenciones del equipo — Portal de Clientes

Este documento define cómo trabajamos en este repositorio. Cualquier revisión de código,
propia o asistida por IA, debe verificar el cumplimiento de estas reglas.

## Arquitectura

- Capas estrictas: `controller` → `service` → `repository`. Un controller nunca accede
  directamente a un repository.
- Los controllers no contienen lógica de negocio; solo orquestan la llamada al service
  y traducen el resultado a una respuesta HTTP.

## Acceso a datos

- Toda consulta personalizada a la base de datos debe usar `@Query` con parámetros
  nombrados o posicionales de Spring Data JPA. **Nunca** construir una consulta
  concatenando strings con datos de entrada del usuario.
- Las relaciones `@OneToMany` se declaran `FetchType.LAZY` por defecto. Si un endpoint
  necesita los datos relacionados, debe usar una consulta con `JOIN FETCH` explícita
  para evitar el problema N+1, nunca iterar la colección lazy en un bucle.

## Manejo de errores

- Nunca se captura una excepción y se descarta en silencio (`catch` vacío o que solo
  hace `log.debug`). Toda excepción capturada debe: (1) loguearse con nivel adecuado,
  y (2) relanzarse como una excepción de negocio propia o propagarse.
- No se exponen mensajes de excepción ni stack traces directamente en la respuesta HTTP
  al cliente de la API.

## Testing

- Todo endpoint nuevo debe llevar al menos un test de integración que cubra el caso
  feliz y, si aplica, el caso de error principal (404, 400, etc.).
- No se aprueba un PR que añade un endpoint sin el test correspondiente.

## Seguridad

- Ningún secreto (contraseñas, API keys, tokens) se guarda hardcodeado en el código
  ni en los ficheros de configuración versionados. Se usan variables de entorno o un
  gestor de secretos.

## Stack y versiones
- Java 17 y Spring Boot 3.3.4, con Maven.
- Base de datos de desarrollo: H2 en memoria. Se vacía al parar la aplicación.

## Comandos
- Arrancar: `mvn spring-boot:run` (API en http://localhost:8080/clientes).
- Probar: `mvn test`.
- No hay Maven Wrapper: se usa el `mvn` instalado.

## Estructura de paquetes
- Paquete base: `com.imagina.demo`, con `controller`, `service`, `repository` y `model`.
- Un endpoint nuevo toca las capas en este orden: controller, service, repository.

## Dependencias
- No añadir dependencias al `pom.xml` sin consultarlo antes con el equipo.

## Datos de prueba
- Solo datos ficticios. Nunca datos reales de ciudadanos ni de clientes.

## Tickets y ramas
<!-- Convención pendiente hasta el tema 11 -->
Convención pendiente de definir.
