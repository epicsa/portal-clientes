# Prompt: paginación de GET /clientes (ticket Redmine #13)

## Objetivo
Que `GET /clientes` devuelva los clientes por páginas en lugar de la lista completa.

## Contexto
- En `src/main/java/com/imagina/demo/`: ClienteController (método `listar()`), ClienteService (`listarTodos()`) y ClienteRepository (extiende JpaRepository).
- El test `listarClientesDevuelveOk` de ClienteControllerTest lee hoy un array.
- Propuesta de partida: página que empieza en 0 y tamaño por defecto 20.
- El ticket de baja lógica (`docs/prompts/baja-logica.md`) toca el mismo listado.

## Límites
- No añadir dependencias al `pom.xml`.
- Orden estable en el listado.
- El tamaño máximo de página no está decidido: pregúntamelo.

## Éxito
- `mvn test` en verde.
- Tests de estos casos: primera página, última página incompleta y tamaño de página por encima del máximo.

## Entrega
Solo un plan, sin editar ficheros: cómo cambia lo que devuelve `GET /clientes` y qué tests existentes se romperían.

Si algo es ambiguo, pregúntame antes de dar por hecha ninguna decisión.

## Preguntas abiertas
<!-- Preguntas planteadas por Claude en modo plan. Las decide el equipo, no Claude ni el desarrollador. -->
- ¿Cuál es el tamaño máximo de página? ¿Qué se hace si se pide uno mayor: se recorta al máximo o se responde 400?
- ¿Se acepta romper el contrato actual (un array JSON) y devolver un objeto de página (`content`, total de elementos…)? ¿Hay consumidores de la API que dependan del array?
- ¿Por qué campo se ordena por defecto (por ejemplo, `id` ascendente)? ¿Se permite que el cliente elija el orden con `sort`?
- ¿Qué se responde a parámetros inválidos (página negativa, tamaño 0)?
- ¿El listado paginado debe excluir ya a los clientes dados de baja (ticket #12)?
