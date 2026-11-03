# Prompt: baja lógica de Cliente (ticket Redmine #12)

## Objetivo
Sustituir el borrado físico de clientes por una baja lógica: un cliente dado de baja deja de aparecer en la API, pero su fila se conserva en la base de datos.

## Contexto
- Código en `src/main/java/com/imagina/demo/`: ClienteController, ClienteService, ClienteRepository y la entidad Cliente.
- Tests en `src/test/java/com/imagina/demo/`: ClienteControllerTest.
- Las normas generales (capas, consultas con parámetros, un test por endpoint) ya están en CLAUDE.md.

## Límites
- No añadir dependencias al `pom.xml`.
- No cambiar la ruta ni el método HTTP de los endpoints existentes.
- Solo datos ficticios en pruebas y ejemplos.

## Éxito
- `mvn test` en verde.
- Tests de estos casos: baja de un cliente que existe, baja de un id que no existe, y un cliente dado de baja que no aparece en `GET /clientes`.

## Entrega
No implementes nada ni edites ficheros todavía. Dame un plan: qué ficheros tocarías y en qué orden, qué cambia en la entidad y en los datos que ya existen, y qué tests se ven afectados.

Si algo es ambiguo, pregúntame antes de dar por hecha ninguna decisión.

## Preguntas abiertas
<!-- Preguntas planteadas por Claude en modo plan. Las decide el equipo, no Claude ni el desarrollador. -->
- ¿Qué devuelve `GET /clientes/{id}` de un cliente dado de baja: 404 o el cliente marcado como inactivo?
- ¿Se puede reactivar un cliente dado de baja? Si es así, ¿con qué endpoint?
- ¿Qué devuelve `DELETE /clientes/{id}` si el id no existe o el cliente ya estaba dado de baja: 204 como hoy, o 404?
- ¿Cómo se implementa: campo explícito (`activo`) con consultas derivadas, o anotaciones de Hibernate (`@SQLDelete` + `@SQLRestriction`, `@SoftDelete`)?
- ¿Qué valor toman los clientes que ya existen al añadir la columna nueva (activos por defecto)?
- ¿Se permite dar de alta un cliente con el email de uno dado de baja?
- ¿Debe aparecer el estado (`activo`) en el JSON de respuesta?
