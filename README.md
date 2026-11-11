# Portal de Clientes

## 1. Qué es el Portal de Clientes

El Portal de Clientes es una API REST de gestión de clientes (alta, consulta, listado
paginado y baja lógica) construida con Spring Boot, Spring Data JPA y una base de datos
H2 en memoria. Sirve como proyecto base del curso *Claude Code y Alternativas IA para
Desarrolladores Java*.

## 2. Requisitos

- **JDK**: la versión de Java está en la propiedad `java.version` de [`pom.xml`](pom.xml).
- **Maven** instalado en el sistema: el proyecto **no** tiene Maven Wrapper (no hay `mvnw`).
  La versión de Spring Boot está en el bloque `<parent>` de [`pom.xml`](pom.xml).

Comprueba lo que tienes instalado:

```bash
java -version
mvn -version
```

## 3. Arranque y tests

Desde la raíz del repositorio:

```bash
# Arrancar la aplicación
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080/clientes` (puerto indicado en
[CLAUDE.md](CLAUDE.md#comandos); `application.properties` no fija `server.port`).

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo el test de contrato de la API
mvn test -Dtest=ClienteContratoTest
```

Base de datos (`src/main/resources/application.properties`):

- H2 en memoria, URL JDBC `jdbc:h2:mem:demodb`, usuario `sa`. El esquema se crea al
  arrancar y **los datos se pierden al parar la aplicación**.
- La consola de H2 está activada (`spring.h2.console.enabled=true`). Ruta de la consola:
  `http://localhost:8080/h2-console` [PENDIENTE DE COMPROBAR].

## 4. API

### Endpoints

| Método | Ruta | Recibe | Devuelve |
|---|---|---|---|
| `GET` | `/clientes` | Parámetros opcionales `page` (desde 0), `size` (20 por defecto, máximo 100) y `sort` (por `id` por defecto) | 200 con el listado paginado de clientes **activos** (`PagedModel<ClienteResponse>`) |
| `GET` | `/clientes/{id}` | `id` en la ruta | 200 con `ClienteResponse`; 404 si no existe o está dado de baja |
| `POST` | `/clientes` | JSON `ClienteRequest`: `{ "nombre", "email" }`. Cualquier otro campo (`id`, `activo`…) se ignora | 200 con `ClienteResponse`; 400 si el email no tiene formato válido; 409 si ya existe un cliente activo con ese email (sin distinguir mayúsculas ni espacios alrededor) |
| `DELETE` | `/clientes/{id}` | `id` en la ruta | 204 siempre (baja lógica; también si no existe o ya estaba dado de baja) |

- `ClienteResponse`: `{ "id", "nombre", "email" }`. La API nunca devuelve la entidad JPA
  ni el campo `activo` (ver [ADR-0001](docs/adr/0001-dtos-en-la-api.md)).
- Los errores 400 y 409 se devuelven como `ProblemDetail` (`application/problem+json`),
  sin trazas.

### Formato del listado paginado

`GET /clientes` devuelve un objeto con `content` (los clientes de la página) y `page`
(metadatos de paginación):

```json
{
  "content": [
    { "id": 1, "nombre": "Cliente Ficticio Uno", "email": "cliente001@example.com" }
  ],
  "page": { "size": 20, "number": 0, "totalElements": 1, "totalPages": 1 }
}
```

Los tests comprueban `content`, `page` y `page.totalElements`. Los demás nombres de campo
dentro de `page` (`size`, `number`, `totalPages`) están [PENDIENTE DE COMPROBAR].

### Regenerar la lista de endpoints

La tabla anterior se obtiene de las anotaciones `*Mapping` de los controladores. Para
comprobar que sigue al día:

```bash
git grep -n "Mapping" -- "*Controller*.java"
```

Salida esperada hoy:

```text
src/main/java/com/imagina/demo/controller/ClienteController.java:15:@RequestMapping("/clientes")
src/main/java/com/imagina/demo/controller/ClienteController.java:24:    @GetMapping
src/main/java/com/imagina/demo/controller/ClienteController.java:29:    @GetMapping("/{id}")
src/main/java/com/imagina/demo/controller/ClienteController.java:37:    @PostMapping
src/main/java/com/imagina/demo/controller/ClienteController.java:42:    @DeleteMapping("/{id}")
```

Si aparece una línea nueva, hay que actualizar la tabla.

## 5. Ejemplo con curl

Con la aplicación arrancada. Como la base de datos empieza vacía, primero da de alta un
cliente ficticio:

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Cliente Ficticio Uno","email":"cliente001@example.com"}'
```

Después, pide el listado (primera página, 20 elementos):

```bash
curl "http://localhost:8080/clientes?page=0&size=20"
```

La respuesta sigue el formato descrito en [Formato del listado paginado](#formato-del-listado-paginado).

## 6. Estructura del proyecto

```text
.
├── pom.xml
├── CLAUDE.md                         # Convenciones del equipo
├── docs/
│   ├── arquitectura.md
│   ├── CODEBASE_MAP.md
│   └── adr/                          # Decisiones de arquitectura
└── src/
    ├── main/
    │   ├── java/com/imagina/demo/
    │   │   ├── DemoApplication.java  # Arranque (@SpringBootApplication)
    │   │   ├── controller/           # ClienteController, ClienteRequest, ClienteResponse, ClienteMapper
    │   │   ├── service/              # ClienteService, ClienteValidator y excepciones de negocio
    │   │   ├── repository/           # ClienteRepository (Spring Data JPA)
    │   │   └── model/                # Entidad Cliente
    │   └── resources/
    │       └── application.properties
    └── test/java/com/imagina/demo/   # Tests de integración y de servicio
```

Capas: `controller` → `service` → `repository`. Más detalle en:

- [docs/arquitectura.md](docs/arquitectura.md): capas, diagrama, regla «la API nunca expone
  entidades JPA», contrato de la API y deuda pendiente.
- [docs/CODEBASE_MAP.md](docs/CODEBASE_MAP.md): mapa del código, recorrido de una petición
  y ficheros por ticket. Ojo: describe el código en la etiqueta `tema-09`, anterior a la
  baja lógica, la paginación, los duplicados y los DTOs.
- [docs/adr/](docs/adr/): registros de decisiones de arquitectura (por ejemplo,
  [ADR-0001: DTOs en lugar de entidades en la API](docs/adr/0001-dtos-en-la-api.md)).

## 7. Cómo contribuir

Antes de tocar código, lee las convenciones del equipo en [CLAUDE.md](CLAUDE.md), en especial:

- [Tickets y ramas](CLAUDE.md#tickets-y-ramas): de dónde parte cada cambio, nombre de las
  ramas y formato de los mensajes de commit.
- [Tests](CLAUDE.md#tests) y [Testing](CLAUDE.md#testing): qué tests acompañan a cada cambio.
