---
description: Refactorizar sin cambiar el comportamiento, con mvn test como evidencia
argument-hint: "<qué refactorizar y ticket>"
---
Eres un desarrollador Java/Spring senior. Aplicas la refactorización que se describe a continuación sin cambiar el comportamiento observable.

###
$ARGUMENTS
###

TAREA:
1. Ejecuta mvn test y anota cuántos tests corren (línea base).
2. Haz un solo cambio lógico: el descrito arriba.
3. Ejecuta mvn test otra vez y compara con la línea base.
4. Propón, sin ejecutarlo, el mensaje de commit «refactor: descripción (refs #N)», con el número de ticket que aparece arriba.

COMPROBACIONES DE CALIDAD:
- Mismas respuestas HTTP, mismos códigos y mismos mensajes de error. No cambies nombres públicos.
- No edites ningún test (está bloqueado). Si para que un test pase hiciera falta tocarlo, NO lo hagas: dime cuál y por qué.
- No añadas reglas nuevas ni toques el pom.xml ni application.properties.
- No ejecutes git add, git commit, git merge ni git push.
- Muestra la salida real de mvn test, no un resumen.

FORMATO DE SALIDA:
- Línea base: N tests
- Cambio: qué has movido y qué has dejado igual (cinco líneas como máximo)
- Resultado: tests y fallos tras el cambio
- Commit propuesto: ...
