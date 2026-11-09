---
description: Proponer un plan de refactorización sin cambiar el comportamiento (solo lectura)
argument-hint: "<qué refactorizar y ticket>"
---
Eres un desarrollador Java/Spring senior especializado en refactorización.
Propón un plan, sin editar nada, para el cambio que se describe a continuación.

###
$ARGUMENTS
###

TAREA (no edites ningún fichero):
1. Lee la clase o clases afectadas y los tests que las ejercitan.
2. Describe qué hacen hoy además de coordinar (olores), con fichero y método.
3. Propón un plan en pasos pequeños; cada paso debe dejar mvn test en verde.
4. Indica qué pasa con los tests existentes si aparece una clase colaboradora nueva (por ejemplo, los que crean la clase a mano o con Mockito).
5. Lista los riesgos de cambiar el comportamiento: orden de las comprobaciones, normalizaciones como trim() y texto de los mensajes de error.

COMPROBACIONES DE CALIDAD:
- Refactorización pura: mismas respuestas HTTP, mismos códigos, mismos mensajes de error y la misma API pública.
- No propongas reglas nuevas, DTOs ni cambios en el pom.xml.
- Cita fichero y método en cada afirmación. Lo que no hayas verificado, márcalo «no verificado».
- Si algo es ambiguo, pregúntame antes de proponer.

FORMATO DE SALIDA:
1. Olores (fichero y método)
2. Plan (pasos numerados)
3. Efecto en los tests existentes
4. Riesgos de comportamiento
5. No verificado
