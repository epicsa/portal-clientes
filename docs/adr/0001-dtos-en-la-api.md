# ADR-0001: DTOs en lugar de entidades en la API

| Campo | Valor |
|---|---|
| Estado | Aceptado |
| Fecha | 2026-11-09 (fusión `fe8d279` «Merge feature/redmine-21-dtos» en `main`, etiqueta `tema-15`; se comprueba con `git log -1 --format=%cd --date=iso tema-15`) |
| Decisores | El equipo |
| Ticket | Redmine #21 |

## 1. Contexto

Hasta la etiqueta `tema-14`, `ClienteController` recibía y devolvía la entidad JPA `Cliente`. Se comprueba con:

```bash
git show tema-14:src/main/java/com/imagina/demo/controller/ClienteController.java
```

Esa forma de trabajar producía tres problemas:

1. **El campo `activo` aparece en las respuestas.** En `tema-14`, `obtener` devuelve `ResponseEntity<Cliente>`, `crear` devuelve `Cliente` y `listar` devuelve `Page<Cliente>`. La entidad `src/main/java/com/imagina/demo/model/Cliente.java` tiene el campo `activo` con su getter `isActivo()`, así que forma parte del JSON de la entidad.
2. **Un `POST` con `id` o `activo` en el JSON se acepta (asignación masiva).** En `tema-14`, el alta es `crear(@RequestBody Cliente cliente)`, y `Cliente` tiene `setId` y `setActivo`. `docs/CODEBASE_MAP.md` lo describe en el §2 (paso 1: «cualquier campo de la entidad, incluido `id`, se acepta»; paso 4: Hibernate «la actualiza si llega un `id` existente») y lo anota como deuda en el §4 («Entidades JPA expuestas en la API […] asignación masiva», a resolver en el «Tema 15 (DTOs)»).
3. **El listado devuelve `Page` directamente.** En `tema-14`, `listar` es `public Page<Cliente> listar(@PageableDefault(size = 20, sort = "id") Pageable pageable)`, que serializa el objeto `Page` de Spring Data tal cual. `docs/arquitectura.md` §7 menciona el aviso `PlainPageSerializationWarning` asociado a esta serialización, pero lo marca como no verificado.

## 2. Alternativas consideradas

Para cada alternativa se separa lo que se comprueba en el repositorio del motivo del equipo, que no está escrito en ningún fichero.

### 2.1. Entidad JPA directa (mantener `Cliente` en la API)

- **Hechos del repositorio:** es la situación de `tema-14` descrita en el Contexto, que produce los tres problemas.
- **Motivo del equipo:** [PENDIENTE DE JOSÉ]

### 2.2. DTO de clase

- **Hechos del repositorio:** no hay ningún DTO de clase en el repositorio; los DTOs que existen son records (`ClienteRequest.java`, `ClienteResponse.java`).
- **Motivo del equipo para descartarla:** [PENDIENTE DE JOSÉ]

### 2.3. Record (elegida)

- **Hechos del repositorio:** `ClienteRequest` y `ClienteResponse` se declaran como `record` en `src/main/java/com/imagina/demo/controller/`. Un record no tiene setters. El proyecto usa Java 17 (`pom.xml`, propiedad `java.version`; `CLAUDE.md`, «Stack y versiones»).
- **Motivo del equipo para elegirla:** [PENDIENTE DE JOSÉ]

### 2.4. Proyección de Spring Data

- **Hechos del repositorio:** `ClienteRepository` no declara ninguna proyección; sus métodos devuelven la entidad (`Page<Cliente> findByActivoTrue(Pageable)`, `Optional<Cliente> findByIdAndActivoTrue(Long)`). La conversión a `ClienteResponse` se hace en el controller con `ClienteMapper`.
- **Motivo del equipo para descartarla:** [PENDIENTE DE JOSÉ]

## 3. Decisión

Decidimos que la API no expone la entidad JPA `Cliente`:

- La entrada de `POST /clientes` es el record `ClienteRequest(String nombre, String email)` (`controller/ClienteRequest.java`).
- La salida es el record `ClienteResponse(Long id, String nombre, String email)` (`controller/ClienteResponse.java`).
- La conversión es manual, en `controller/ClienteMapper.java`: `toEntity` crea un `Cliente` nuevo solo con `nombre` y `email` («el id y el estado no se toman nunca de la petición», según su Javadoc), y `toResponse` copia `id`, `nombre` y `email`.
- `GET /clientes` devuelve `PagedModel<ClienteResponse>` (`controller/ClienteController.java`, líneas 25-26).

La regla queda escrita en `docs/arquitectura.md` §3: «La API nunca expone entidades JPA».

## 4. Consecuencias

### Buenas

- Las respuestas no incluyen `activo`: `ClienteResponse` solo tiene `id`, `nombre` y `email`.
- Un `POST` con `id` o `activo` no los aplica: `ClienteRequest` no tiene esos campos y `ClienteMapper.toEntity` no los asigna.
- El listado tiene el formato `content` + `page`, sin `pageable` (lo afirma `elListadoDevuelveContentYPageSinPageableNiActivo` en `ClienteContratoTest`).
- `ClienteService` y `ClienteRepository` siguen trabajando con `Cliente`; el cambio se concentra en el paquete `controller` (`git diff --stat tema-14 tema-15`: solo cambian ficheros de `controller`, el test nuevo y `docs/arquitectura.md`).

### Malas

- El mapeo es manual: cada campo nuevo que deba viajar por la API hay que añadirlo a mano en el record y en `ClienteMapper`.
- Los campos desconocidos del JSON se ignoran en silencio, sin error 400 (Javadoc de `ClienteRequest`: «Cualquier otro campo del JSON se ignora»; `docs/arquitectura.md` §4). Un cliente de la API que envíe `id` o `activo` no recibe aviso.
- `ClienteRequest` sigue sin validación de entrada (`@Valid`): un `nombre` vacío se acepta (`docs/arquitectura.md` §6, previsto para el tema 18).
- Cambia el contrato del listado para quien consumiera `GET /clientes`: antes era el JSON de `Page<Cliente>`, ahora el de `PagedModel<ClienteResponse>`.
- `ClienteMapper` vive en el paquete `controller`, no en una capa propia; si otra capa necesitara la conversión, habría que moverlo.

## 5. Cómo se comprueba

Test de contrato `src/test/java/com/imagina/demo/ClienteContratoTest.java` (`mvn test -Dtest=ClienteContratoTest`), con cinco tests:

| Test | Qué afirma |
|---|---|
| `altaValidaNoDevuelveActivo` | El `POST` responde 200 con `id`, `nombre` y `email`, sin `activo`. |
| `obtenerPorIdNoDevuelveActivo` | `GET /clientes/{id}` responde 200 con `id`, `nombre` y `email`, sin `activo`. |
| `altaConActivoFalseCreaUnClienteActivo` | Un `POST` con `"activo": false` crea un cliente que luego se obtiene con 200 (está activo). |
| `altaConElIdDeOtroClienteCreaUnoNuevoYNoModificaElOriginal` | Un `POST` con el `id` de otro cliente crea uno nuevo con otro `id` y el original conserva su `nombre` y `email`. |
| `elListadoDevuelveContentYPageSinPageableNiActivo` | `GET /clientes` devuelve `content` y `page` (con `totalElements`), sin `pageable`, y los elementos no tienen `activo`. |

Los datos de los tests son ficticios (`cliente001@example.com` … `cliente006@example.com`).

Búsquedas con `git grep` del tema 15:

1. [PENDIENTE DE JOSÉ]
2. [PENDIENTE DE JOSÉ]

## 6. Referencias

- Etiquetas: `tema-14` (antes de la decisión) y `tema-15` (fusión de la decisión).
- Commits (`git log --oneline tema-14..tema-15`):
  - `6b8b5b7` test: contrato de la API sin entidades, en rojo (refs #21)
  - `312c259` feat: DTOs record en la API y PagedModel en el listado (refs #21)
  - `e9d58dc` docs: arquitectura de capas y contrato de la API (refs #21)
  - `fe8d279` Merge feature/redmine-21-dtos
- Ficheros:
  - `src/main/java/com/imagina/demo/controller/ClienteController.java`
  - `src/main/java/com/imagina/demo/controller/ClienteRequest.java`
  - `src/main/java/com/imagina/demo/controller/ClienteResponse.java`
  - `src/main/java/com/imagina/demo/controller/ClienteMapper.java`
  - `src/main/java/com/imagina/demo/model/Cliente.java`
  - `src/main/java/com/imagina/demo/repository/ClienteRepository.java`
  - `src/test/java/com/imagina/demo/ClienteContratoTest.java`
  - `docs/arquitectura.md` (§3 regla, §4 contrato, §6 deuda, §7 no verificado)
  - `docs/CODEBASE_MAP.md` (§2 recorrido de `POST /clientes`, §4 deuda técnica; describe el código en `tema-09`)
  - `CLAUDE.md` (stack, capas y regla de dependencias)

## Sugerencia de Claude, no considerada por el equipo

Estas opciones no aparecen en el repositorio ni se compararon en el tema 15. Se dejan solo como punto de discusión:

- **Mapeador generado (por ejemplo, MapStruct)** en lugar de `ClienteMapper` manual. Exigiría añadir una dependencia al `pom.xml`, y `CLAUDE.md` («Dependencias») pide consultarlo antes con el equipo.
- **Anotaciones de Jackson sobre la entidad** (`@JsonIgnore` en `activo`, `@JsonProperty(access = READ_ONLY)` en `id`). Mantendría la entidad en la API, de modo que no resolvería la regla de `docs/arquitectura.md` §3.
- **Rechazar campos desconocidos con 400** (por ejemplo, `spring.jackson.deserialization.fail-on-unknown-properties=true`) en lugar de ignorarlos en silencio. Cambiaría el comportamiento que hoy afirman `altaConActivoFalseCreaUnClienteActivo` y `altaConElIdDeOtroClienteCreaUnoNuevoYNoModificaElOriginal`.
