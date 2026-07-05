# Plan de Refactorización Actualizado

## 🔄 Cambios respecto a la versión anterior

- Se incorpora la **gestión completa de salas** (crear, listar, eliminar) como parte del panel lobby.
- Se incorpora el **resumen/dashboard global** de todas las salas.
- El formulario de creación de sala pasa a pedir: **nombre**, **cantidad total de asientos**, **filas** y **columnas** en relación a la cantidad total de elementos.
- Ya no se abren ventanas nuevas por cada sala. **Todo vive en un único JFrame**, y la vista cambia dinámicamente entre "panel lobby" y "panel de sala" dentro de esa misma ventana.

---

## 📋 Requerimientos Funcionales Actualizados

- **RF1 — Salas de tamaño variable:** Cada sala define su propio número de filas y columnas al crearse, de forma independiente de cualquier otra sala.
- **RF2 — Múltiples salas en memoria:** El sistema permite crear, mantener y consultar varias salas durante la misma ejecución.
- **RF3 — Creación de sala con validación de asientos:** El formulario de creación debe solicitar cuatro datos: nombre de la sala, cantidad total de asientos, filas y columnas. Como `filas × columnas` ya determina el total de asientos, el sistema debe validar que el total ingresado por el usuario coincida con la multiplicación, y si no coincide, mostrar un error claro indicando el total esperado antes de permitir crear la sala. *(Alternativa a discutir con el programador: que el campo "total de asientos" se calcule y muestre automáticamente en cuanto el usuario ingresa filas y columnas, en lugar de ser editable — a definir según lo que sea más simple de implementar.)*
- **RF4 — Listado y gestión de salas (panel lobby):** El panel lobby debe listar todas las salas creadas mostrando nombre, dimensiones (filas x columnas) y un resumen de ocupación por sala. Debe permitir:
  - Crear una nueva sala.
  - Seleccionar una sala para abrir su vista de butacas.
  - Eliminar una sala existente.
- **RF5 — Restricción de borrado:** Si una sala tiene al menos una butaca en estado **RESERVADO** u **OCUPADO**, el sistema debe advertir al usuario antes de eliminarla (mensaje de confirmación indicando que hay butacas no libres), en lugar de bloquear el borrado por completo — la decisión final la toma el usuario.
- **RF6 — Navegación dentro de una sola ventana:** El sistema debe usar una única ventana principal (`JFrame`) en toda la ejecución. El contenido visible dentro de esa ventana cambia dinámicamente entre el panel lobby y el panel de gestión de butacas de la sala seleccionada, sin crear instancias nuevas de `JFrame`.
- **RF7 — Retorno al lobby:** Desde la vista de una sala, el usuario debe poder volver al panel lobby (por ejemplo con un botón "Volver a mis salas"), y al volver, el lobby debe reflejar cualquier cambio de estado que haya ocurrido en esa sala (butacas reservadas, ocupadas, etc.).
- **RF8 — Resumen global (dashboard):** El panel lobby debe mostrar estadísticas agregadas de todas las salas: total de salas creadas, y totales de butacas libres, reservadas y ocupadas sumando todas las salas existentes. Este resumen debe actualizarse cada vez que el usuario vuelve al lobby.
- **RF9 — Selección y reserva múltiple** *(ya definida previamente, sin cambios)*: Dentro de una sala, el usuario puede seleccionar varias butacas libres y confirmarlas en una sola operación de reserva, con retroalimentación de cuáles tuvieron éxito y cuáles no.

---

## 🛠️ Tareas de Implementación

### 1. Modelo de Datos
- Ajustar la clase de sala para recibir nombre, filas y columnas por constructor; cada instancia valida posiciones contra su propio tamaño (nada estático ni compartido).
- Crear la clase encargada de fabricar salas, responsable de validar que `filas × columnas` coincida con el total de asientos ingresado, y de validar los rangos permitidos de filas/columnas antes de construir la sala.
- Crear la clase gestora de salas en memoria, con operaciones para: agregar una sala, listar todas, obtener una por id, y eliminar una por id. Debe exponer también un método que calcule los totales agregados (libres/reservadas/ocupadas) recorriendo todas las salas registradas, pensado para alimentar el dashboard.

### 2. Capa de Servicio
- Adaptar los servicios de reserva/cancelación/ocupación para validar contra el tamaño propio de cada sala recibida por constructor.
- Agregar una operación de servicio para reservar múltiples posiciones en un solo llamado, devolviendo el detalle de qué posiciones se reservaron y cuáles fallaron (y el motivo), sin detener el proceso ante el primer error.
- Definir en la capa de servicio o en el gestor de salas la operación de "eliminar sala", que primero permita consultar si tiene butacas no libres (para que la vista decida si pide confirmación).

### 3. Capa de Vista — Ventana Principal Única
- Modificar la ventana principal para que administre un contenedor cuyo contenido pueda intercambiarse en tiempo de ejecución entre "panel lobby" y "panel de sala", sin cerrar ni recrear el `JFrame`. *(Sugerencia de mecanismo a evaluar por el programador: un layout que permita mostrar un panel a la vez y cambiar cuál está visible, o bien remover el panel actual y agregar el nuevo dentro del mismo contenedor.)*
- La ventana principal debe iniciar siempre mostrando el panel lobby.

### 4. Capa de Vista — Panel Lobby
Crear el panel lobby con:
- Una lista o tabla de las salas existentes (nombre, filas x columnas, resumen de ocupación), obtenida desde el gestor de salas.
- Un bloque de estadísticas globales (dashboard) con los totales agregados de todas las salas.
- Un botón **"Nueva sala"** que abra el formulario de creación.
- Una acción por sala para **"Abrir"** (cambia la vista al panel de esa sala) y otra para **"Eliminar"** (con el flujo de confirmación de RF5).
- El panel lobby debe poder refrescarse (recalcular lista y estadísticas) cada vez que se vuelve a mostrar, para reflejar cambios hechos dentro de una sala.

### 5. Capa de Vista — Formulario de Creación de Sala
- Adaptar el diálogo actual de configuración de tamaño para incluir un campo de texto para el nombre y un campo para el total de asientos, junto a los campos existentes de filas y columnas.
- Al confirmar, validar que el total de asientos ingresado coincida con `filas × columnas` antes de invocar la creación en el gestor de salas; si no coincide, mostrar el error sin cerrar el diálogo.
- Al crear la sala exitosamente, el lobby debe actualizarse mostrando la nueva sala en la lista.

### 6. Capa de Vista — Panel de Sala *(el actual MainFrame convertido en panel)*
- Convertir el contenido de la actual ventana de gestión de butacas en un panel reutilizable (ya no una subclase de `JFrame`), que reciba por constructor el servicio y consulta de la sala correspondiente, igual que antes.
- Agregar un botón o acción **"Volver a mis salas"** que le indique a la ventana principal que vuelva a mostrar el panel lobby.
- Incorporar la funcionalidad de selección múltiple de butacas y el botón de **"Reservar seleccionadas"**, mostrando el detalle de éxitos/fallos tras la operación.

### 7. Ajustes en el Punto de Entrada del Sistema
- La clase principal debe inicializar el gestor de salas (vacío al inicio) y lanzar la ventana principal mostrando directamente el panel lobby, eliminando el flujo anterior de "diálogo de tamaño + ventana única".

### 8. Pruebas Manuales Sugeridas
- [ ] Crear una sala con un total de asientos que no coincida con `filas × columnas` y confirmar que el sistema lo rechaza con un mensaje claro.
- [ ] Crear dos o más salas con tamaños distintos y confirmar que sus estados de butacas son independientes.
- [ ] Abrir una sala, reservar/ocupar butacas, volver al lobby y confirmar que la lista y el dashboard reflejan los cambios.
- [ ] Intentar eliminar una sala sin butacas ocupadas/reservadas (debe eliminarse sin advertencia) y luego una con butacas no libres (debe pedir confirmación).
- [ ] Verificar que nunca se abre una segunda ventana: todo el cambio de vista ocurre dentro del mismo `JFrame`.
- [ ] Probar selección múltiple: seleccionar varias butacas libres, reservarlas todas, y luego intentar una selección que incluya una butaca que otro flujo dejó como reservada, confirmando que el sistema informa cuáles fallaron.
