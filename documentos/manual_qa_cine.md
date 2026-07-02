# 🎬 Manual de Pruebas QA — Sistema de Gestión de Butacas de Cine
**Responsable QA:** Encargado de calidad  
**Fecha:** 2026-07-01  
**Versión del sistema:** 1.0  
**Interfaz:** JFrame (Swing — Tema oscuro moderno)

---

> [!IMPORTANT]
> Este manual cubre pruebas **manuales interactivas** sobre la interfaz gráfica JFrame.
> Se deben ejecutar en orden para que cada bloque de pruebas tenga el estado correcto.
> Al finalizar cada sección, **reinicia la aplicación** para limpiar el estado de la sala, salvo que se indique lo contrario.

---

## 🗺️ Mapa de la Interfaz (Referencia Rápida)

```
┌──────────────────────────────────────────────────────────────────────────┐
│  🎬  Header: "Sistema de Gestión de Butacas de Cine"        Sala: 5×6   │
├───────────────────────────────────┬──────────────────────────────────────┤
│                                   │  ⚙ Panel de Control                  │
│   PanelSala — Grilla de Butacas   │  ┌─────────────────────────────────┐ │
│                                   │  │ Seleccionar Butaca               │ │
│   [C1][C2][C3][C4][C5][C6]        │  │ Fila: [↑1↓]  Columna: [↑1↓]    │ │
│F1 [  ][  ][  ][  ][  ][  ]        │  └─────────────────────────────────┘ │
│F2 [  ][  ][  ][  ][  ][  ]        │  ┌─────────────────────────────────┐ │
│F3 [  ][  ][  ][  ][  ][  ]        │  │ Operaciones                      │ │
│F4 [  ][  ][  ][  ][  ][  ]        │  │ [Reservar Butaca]  (violeta)     │ │
│F5 [  ][  ][  ][  ][  ][  ]        │  │ [Cancelar Reserva] (rojo)        │ │
│                                   │  │ [Contar Libres]    (verde)       │ │
│   ┌──────────────────────────┐    │  └─────────────────────────────────┘ │
│   │ ● Libre ● Reservado ● Ocp│    │  ┌─────────────────────────────────┐ │
│   └──────────────────────────┘    │  │ Estadísticas                     │ │
│       PanelLeyenda                │  │ Libres: 30  Reservadas: 0  ...   │ │
│                                   │  └─────────────────────────────────┘ │
├───────────────────────────────────┴──────────────────────────────────────┤
│  🟢 Libres: 30   🟡 Reservadas: 0   🔴 Ocupadas: 0   ← Barra de Estado  │
└──────────────────────────────────────────────────────────────────────────┘
```

**Colores de butacas:**
| Color | Estado | Código Hex |
|-------|--------|------------|
| 🟢 Verde | LIBRE | `#22c55e` |
| 🟡 Amarillo | RESERVADO | `#fbbf24` |
| 🔴 Rojo | OCUPADO | `#ef4444` |

---

## 📋 BLOQUE 1 — Verificación Visual del Arranque (Estado Inicial)

> **Objetivo:** Confirmar que la UI carga correctamente sin interacción del usuario.

### TC-001 — Verificar ventana principal al iniciar

| Campo | Detalle |
|-------|---------|
| **Componente** | `MainFrame` |
| **Precondición** | Sistema recién iniciado |
| **Pasos** | 1. Ejecutar la aplicación |
| **Resultado esperado** | La ventana aparece centrada en pantalla, con fondo oscuro (`#0a0a14`) |

**Lista de verificación visual:**
[ X ] El header muestra gradiente de color oscuro de izquierda a derecha con línea violeta en la parte inferior
[ X ] El título dice exactamente: `🎬  Sistema de Gestión de Butacas de Cine`
[ X ] El subtítulo superior derecho dice: `Sala: 5 filas × 6 columnas`
[ X ] La grilla muestra **30 butacas** (5 filas × 6 columnas), todas en **verde** (`LIBRE`)
[ X ] Los encabezados de columna muestran `C1` a `C6` en gris claro
[ X ] Los encabezados de fila muestran `F1` a `F5` en gris claro
[ X ] La leyenda inferior izquierda muestra tres círculos: **Verde=Libre**, **Amarillo=Reservado**, **Rojo=Ocupado** (con bordes redondeados)
[ X ] El Panel de Control derecho muestra: spinners en Fila=1, Columna=1; los tres botones operativos; y el panel de estadísticas
[ X ] La **barra de estado inferior** muestra: `🟢 Libres: 30   🟡 Reservadas: 0   🔴 Ocupadas: 0`
[ X ] Las estadísticas del panel de control muestran: `Libres: 30`, `Reservadas: 0`, `Ocupadas: 0`, `Total: 30`

---

### TC-002 — Verificar tooltip de butaca al pasar el cursor

| Campo | Detalle |
|-------|---------|
| **Componente** | `BotonButaca` |
| **Precondición** | Sistema recién iniciado |
| **Pasos** | 1. Mover el cursor sobre la butaca en posición Fila 1, Columna 1 (esquina superior izquierda) |
| **Resultado esperado** | Aparece un tooltip que dice: `Fila 1 - Columna 1 (LIBRE)` |

**Verificación adicional:**
- [ X ] Al pasar el cursor, el color de la butaca cambia a **verde más claro** (efecto hover `#4ade80`)
- [ X ] Al retirar el cursor, el color regresa al verde normal (`#22c55e`)
- [ X ] La butaca tiene una forma de silla: respaldo oscuro arriba, cuerpo principal abajo

---

### TC-003 — Verificar tamaño mínimo de ventana

| Campo | Detalle |
|-------|---------|
| **Componente** | `MainFrame` |
| **Pasos** | 1. Intentar reducir la ventana arrastrando desde una esquina hacia adentro |
| **Resultado esperado** | La ventana **no se puede reducir** por debajo de 820×600 píxeles |

- [ X ] El sistema impide que la ventana quede más pequeña que el mínimo definido
- [ X ] Al llegar al límite, la ventana "rebota" y no permite seguir reduciendo

---

## 📋 BLOQUE 2 — Pruebas del Flujo de Reserva via PanelSala (Clicks en Grilla)

> **Objetivo:** Verificar la transición de estados LIBRE → RESERVADO → OCUPADO → LIBRE mediante click directo en las butacas.

### TC-010 — Reservar una butaca haciendo clic (LIBRE → RESERVADO)

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelSala` → `BotonButaca` |
| **Precondición** | Sala en estado inicial (todas libres) |
| **Pasos** | 1. Hacer clic en la butaca **F1-C1** (verde) |
| **Resultado esperado** | La butaca **F1-C1** cambia de verde a **amarillo** |

**Verificación visual completa:**
- [ X ] La butaca F1-C1 ahora tiene color **amarillo** (`#fbbf24`)
- [ X ] El tooltip de F1-C1 ahora dice: `Fila 1 - Columna 1 (RESERVADO)`
- [ X ] La barra de estado inferior actualiza a: `🟢 Libres: 29   🟡 Reservadas: 1   🔴 Ocupadas: 0`
- [ X ] Las estadísticas del panel de control muestran: `Libres: 29`, `Reservadas: 1`, `Total: 30`
- [ X ] Las demás butacas siguen en verde (sin cambios)

---

### TC-011 — Ocupar una butaca ya reservada (RESERVADO → OCUPADO)

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelSala` → `BotonButaca` |
| **Precondición** | TC-010 ejecutado (F1-C1 está en RESERVADO) |
| **Pasos** | 1. Hacer clic **nuevamente** en la butaca **F1-C1** (amarilla) |
| **Resultado esperado** | La butaca F1-C1 cambia de amarillo a **rojo** |

**Verificación visual:**
- [ ] La butaca F1-C1 ahora tiene color **rojo** (`#ef4444`)
- [ ] El tooltip de F1-C1 ahora dice: `Fila 1 - Columna 1 (OCUPADO)`
- [ ] La barra de estado inferior actualiza a: `🟢 Libres: 29   🟡 Reservadas: 0   🔴 Ocupadas: 1`
- [ ] Las estadísticas del panel de control actualizan correctamente

---

### TC-012 — Liberar una butaca ocupada (OCUPADO → LIBRE)

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelSala` → `BotonButaca` |
| **Precondición** | TC-011 ejecutado (F1-C1 está en OCUPADO) |
| **Pasos** | 1. Hacer clic **nuevamente** en la butaca **F1-C1** (roja) |
| **Resultado esperado** | La butaca F1-C1 regresa a **verde** |

**Verificación visual:**
- [ ] La butaca F1-C1 ahora tiene color **verde** (`#22c55e`)
- [ ] El tooltip de F1-C1 ahora dice: `Fila 1 - Columna 1 (LIBRE)`
- [ ] La barra de estado regresa a: `🟢 Libres: 30   🟡 Reservadas: 0   🔴 Ocupadas: 0`
- [ ] El ciclo completo LIBRE → RESERVADO → OCUPADO → LIBRE se completó sin errores visuales

---

### TC-013 — Efecto hover en butaca RESERVADO y OCUPADO

| Campo | Detalle |
|-------|---------|
| **Componente** | `BotonButaca` |
| **Precondición** | Tener una butaca en RESERVADO (amarillo) y otra en OCUPADO (rojo) |
| **Pasos** | 1. Mover cursor sobre butaca amarilla. 2. Mover cursor sobre butaca roja |

**Verificación:**
- [ ] Butaca amarilla en hover: cambia a amarillo más claro (`#fcd34d`)
- [ ] Butaca roja en hover: cambia a rojo más claro (`#f87171`)
- [ ] Al retirar el cursor, cada butaca regresa a su color de estado correspondiente
- [ ] **No hay parpadeos, flickering ni distorsiones visuales** durante el hover

---

## 📋 BLOQUE 3 — Pruebas del Panel de Control (PanelControl)

> **Objetivo:** Verificar que los spinners, botones de acción y el diálogo de confirmación funcionan correctamente.

### TC-020 — Reservar una butaca con el botón "Reservar Butaca"

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` → `DialogReserva` |
| **Precondición** | Sala en estado inicial |
| **Pasos** | 1. En el spinner Fila, establecer **3**. 2. En el spinner Columna, establecer **4**. 3. Clic en botón **"Reservar Butaca"** (violeta) |
| **Resultado esperado** | Aparece el `DialogReserva` (ventana modal) |

**Verificación visual del diálogo:**
- [ ] El diálogo tiene fondo oscuro (`#121223`) y bordes redondeados
- [ ] Muestra el emoji 🎟 grande en la parte superior
- [ ] El texto dice: `¿Confirma la reserva de la butaca?`
- [ ] La fila y columna aparecen en **amarillo dorado**: `Fila 3 - Columna 4`
- [ ] Hay dos botones: **"Confirmar"** (verde) y **"Cancelar"** (rojo)
- [ ] El cursor sobre los botones cambia a **manita** (cursor HAND)
- [ ] El diálogo está centrado sobre la ventana principal
- [ ] El diálogo **bloquea la interacción** con la ventana principal mientras está abierto

---

### TC-021 — Confirmar la reserva en el DialogReserva

| Campo | Detalle |
|-------|---------|
| **Precondición** | TC-020: diálogo visible con Fila 3, Columna 4 |
| **Pasos** | 1. Clic en botón **"Confirmar"** (verde) |
| **Resultado esperado** | El diálogo cierra y la butaca F3-C4 cambia a amarillo |

**Verificación:**
- [ ] El diálogo se cierra
- [ ] La butaca **F3-C4** en la grilla ahora es **amarilla** (RESERVADO)
- [ ] La barra de estado actualiza: `🟡 Reservadas: 1`
- [ ] Las estadísticas del panel de control actualizan inmediatamente
- [ ] El tooltip de F3-C4 ahora dice: `Fila 3 - Columna 4 (RESERVADO)`

---

### TC-022 — Cancelar la reserva en el DialogReserva

| Campo | Detalle |
|-------|---------|
| **Precondición** | Sala en estado inicial |
| **Pasos** | 1. Spinners en Fila=2, Columna=2. 2. Clic en **"Reservar Butaca"**. 3. En el diálogo, clic en **"Cancelar"** (rojo) |
| **Resultado esperado** | El diálogo cierra **sin reservar** la butaca |

**Verificación:**
- [ ] El diálogo se cierra
- [ ] La butaca **F2-C2** sigue en **verde** (LIBRE) — sin cambio
- [ ] La barra de estado **no cambia**: `🟡 Reservadas: 0`
- [ ] Las estadísticas del panel no cambian

---

### TC-023 — Cerrar el DialogReserva con la "X"

| Campo | Detalle |
|-------|---------|
| **Precondición** | Sala en estado inicial |
| **Pasos** | 1. Clic en **"Reservar Butaca"**. 2. Clic en la **X** de la esquina superior del diálogo |
| **Resultado esperado** | El diálogo cierra sin reservar |

**Verificación:**
- [ ] El diálogo se cierra correctamente
- [ ] La butaca objetivo sigue en **verde** (sin cambio de estado)
- [ ] La ventana principal queda activa y responsive

---

### TC-024 — Cancelar una reserva con el botón "Cancelar Reserva"

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` |
| **Precondición** | F1-C1 en estado RESERVADO |
| **Pasos** | 1. Spinners en Fila=1, Columna=1. 2. Clic en **"Cancelar Reserva"** (rojo) |
| **Resultado esperado** | Un mensaje informativo aparece y la butaca regresa a verde |

**Verificación:**
- [ ] Aparece un `JOptionPane` de información (`ℹ️`) con el texto: `Reserva cancelada en F1-C1.`
- [ ] Al cerrar el diálogo informativo, la butaca **F1-C1** es **verde** (LIBRE)
- [ ] La barra de estado actualiza: `🟡 Reservadas: 0`

---

### TC-025 — Usar el botón "Contar Libres"

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` |
| **Pasos** | 1. Reservar manualmente 3 butacas (usando click en grilla). 2. Ocupar 1 butaca adicional. 3. Clic en **"Contar Libres"** (verde) |
| **Resultado esperado** | Aparece un diálogo de información con el conteo correcto |

**Verificación:**
- [ ] El `JOptionPane` tiene título: `Conteo de Butacas`
- [ ] Muestra en líneas separadas:
  - `Butacas Libres: 26`
  - `Butacas Reservadas: 3`
  - `Butacas Ocupadas: 1`
  - `Total: 30`
- [ ] Los números coinciden exactamente con el estado actual de la sala

---

### TC-026 — Verificar comportamiento de los Spinners (límites)

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` → Spinners |
| **Pasos** | 1. Hacer clic en la flecha "bajar" ↓ del spinner de Fila cuando está en valor **1**. 2. Hacer clic en la flecha "subir" ↑ cuando está en valor **5** |

**Verificación:**
- [ ] El spinner de Fila **no permite bajar de 1** (mínimo = 1)
- [ ] El spinner de Fila **no permite subir de 5** (máximo = 5 = `MAX_FILAS`)
- [ ] El spinner de Columna **no permite bajar de 1** (mínimo = 1)
- [ ] El spinner de Columna **no permite subir de 6** (máximo = 6 = `MAX_COLS`)
- [ ] Los spinners tienen fondo oscuro (`#0a0a14`), texto claro y borde gris

---

## 📋 BLOQUE 4 — Pruebas de Excepciones (Casos de Error)

> **Objetivo:** Provocar intencionalmente cada tipo de excepción y verificar que el sistema responde con un mensaje de error visual correcto y que la UI no se rompe.

---

### TC-030 — Excepción: `AsientoYaReservadoException` (desde PanelControl)

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoYaReservadoException` |
| **Componente** | `PanelControl.ejecutarReserva()` |
| **Cómo reproducir** | 1. Click en F2-C3 para reservarla (queda amarilla). 2. En spinners, poner Fila=2, Columna=3. 3. Clic en **"Reservar Butaca"**. 4. En el diálogo, clic en **"Confirmar"** |
| **Resultado esperado** | Aparece un `JOptionPane` de ERROR |

**Verificación:**
- [ ] El `JOptionPane` tiene ícono ❌ (ERROR)
- [ ] El título del diálogo es: **`Error`**
- [ ] El mensaje es exactamente: **`Este asiento ya fue reservado.`**
- [ ] La butaca F2-C3 **sigue en amarillo** (estado no cambia)
- [ ] La barra de estado y estadísticas **no cambian**
- [ ] Al cerrar el diálogo de error, la UI queda completamente funcional (probar otra operación)

---

### TC-031 — Excepción: `AsientoOcupadoException` (desde PanelControl)

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoOcupadoException` |
| **Componente** | `PanelControl.ejecutarReserva()` |
| **Cómo reproducir** | 1. Click en F1-C1 → queda amarilla. 2. Click nuevamente en F1-C1 → queda roja (OCUPADO). 3. Spinners: Fila=1, Columna=1. 4. Clic en **"Reservar Butaca"** → clic en **"Confirmar"** |
| **Resultado esperado** | Aparece un `JOptionPane` de ERROR |

**Verificación:**
- [ ] El `JOptionPane` tiene ícono ❌ (ERROR)
- [ ] El título es: **`Error`**
- [ ] El mensaje es: **`Este asiento está ocupado.`**
- [ ] La butaca F1-C1 **sigue en rojo** (estado no cambia)
- [ ] La UI queda funcional tras cerrar el error

---

### TC-032 — Excepción: `AsientoNoReservadoException` al intentar cancelar una butaca LIBRE

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoNoReservadoException` |
| **Componente** | `PanelControl.onCancelarClick()` |
| **Cómo reproducir** | 1. Sala en estado inicial (todas libres). 2. Spinners: Fila=3, Columna=3. 3. Clic en **"Cancelar Reserva"** (rojo) |
| **Resultado esperado** | Aparece un `JOptionPane` de ERROR |

**Verificación:**
- [ ] El `JOptionPane` tiene ícono ❌ (ERROR)
- [ ] El título es: **`Error`**
- [ ] El mensaje es: **`Este asiento no está reservado, no se puede cancelar.`**
- [ ] La butaca F3-C3 **sigue en verde** (estado no cambia)
- [ ] Ningún contador cambia

---

### TC-033 — Excepción: `AsientoNoReservadoException` al cancelar una butaca OCUPADA

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoNoReservadoException` |
| **Cómo reproducir** | 1. Click en F4-C4 → reservada. 2. Click en F4-C4 nuevamente → ocupada (roja). 3. Spinners: Fila=4, Columna=4. 4. Clic en **"Cancelar Reserva"** |
| **Resultado esperado** | Aparece un `JOptionPane` de ERROR |

**Verificación:**
- [ ] Mensaje: **`Este asiento no está reservado, no se puede cancelar.`**
- [ ] La butaca F4-C4 **sigue en rojo** (OCUPADO — estado no cambia)
- [ ] La barra de estado permanece igual

---

### TC-034 — Excepción: `AsientoYaReservadoException` (desde PanelSala — click en grilla)

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoYaReservadoException` |
| **Componente** | `PanelSala.onButacaClick()` |
| **Cómo reproducir** | *Este caso no ocurre desde click en grilla* porque el ciclo es LIBRE→RESERVADO→OCUPADO→LIBRE. Sin embargo, podría ocurrir si se fuerza el estado. Ver TC-030 para la versión desde PanelControl. |

> [!NOTE]
> Desde la grilla de butacas, `AsientoYaReservadoException` **no es alcanzable** en condiciones normales porque al hacer clic en una butaca RESERVADA el sistema la pasa a OCUPADO (no intenta reservarla de nuevo). Esta excepción solo se activa desde el PanelControl.

---

### TC-035 — Excepción: `AsientoOcupadoException` (desde PanelSala — click en grilla)

> [!NOTE]
> Desde la grilla, al hacer clic en una butaca OCUPADA, el sistema llama a `cancelar()`, no a `reservar()`. Por lo tanto, `AsientoOcupadoException` **no es alcanzable** desde un click directo en la grilla. Solo se activa desde PanelControl. Ver TC-031.

---

### TC-036 — Excepción: `AsientoNoReservadoException` (desde PanelSala — click en grilla)

| Campo | Detalle |
|-------|---------|
| **Excepción** | `AsientoNoReservadoException` |
| **Componente** | `PanelSala.onButacaClick()` (caso default: `cancelar()` en butaca LIBRE) |
| **Observación** | En la implementación actual, el `default` del switch llama a `cancelar()`. Si el estado fuera LIBRE, lanzaría esta excepción. Sin embargo, el switch cubre correctamente LIBRE → reservar y RESERVADO → ocupar, dejando solo OCUPADO en el `default`. |

> [!NOTE]
> En la práctica, el flujo de la grilla es: LIBRE→reservar, RESERVADO→ocupar, OCUPADO→cancelar. La excepción `AsientoNoReservadoException` desde la grilla **no es alcanzable** en uso normal, ya que al hacer clic en una butaca OCUPADA el sistema llama a `cancelar()` correctamente.

---

## 📋 BLOQUE 5 — Pruebas de Sincronización entre Paneles

> **Objetivo:** Verificar que cuando se opera desde PanelControl, la grilla de PanelSala se actualiza visualmente; y viceversa.

### TC-040 — Sincronización: PanelControl → PanelSala

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Spinners: Fila=5, Columna=6. 2. Clic en **"Reservar Butaca"** → Confirmar |
| **Resultado esperado** | La butaca F5-C6 en la grilla cambia de verde a amarillo |

**Verificación:**
- [ ] La butaca **F5-C6** (esquina inferior derecha) cambia a **amarillo** en la grilla
- [ ] La barra de estado actualiza: `🟡 Reservadas: 1`
- [ ] Las estadísticas del PanelControl actualizan: `Reservadas: 1`
- [ ] Todos los cambios ocurren **sin necesidad de refrescar o hacer clic adicional**

---

### TC-041 — Sincronización: PanelSala → PanelControl

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Hacer clic directamente en la butaca **F3-C2** en la grilla |
| **Resultado esperado** | Las estadísticas del PanelControl se actualizan automáticamente |

**Verificación:**
- [ ] La butaca F3-C2 cambia de verde a amarillo
- [ ] Las estadísticas del panel de control actualizan inmediatamente
- [ ] La barra de estado inferior también actualiza inmediatamente
- [ ] **No hay delay visible** entre el clic y la actualización

---

### TC-042 — Sincronización bidireccional completa

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Reservar F1-C1 desde la grilla (clic). 2. Reservar F1-C2 desde el PanelControl (botón). 3. Cancelar F1-C1 desde el PanelControl. 4. Ocupar F1-C2 desde la grilla (clic) |
| **Resultado esperado** | Todos los cambios reflejan correctamente en ambos paneles y la barra de estado |

**Verificación final:**
- [ ] F1-C1: Verde (libre)
- [ ] F1-C2: Rojo (ocupado)
- [ ] Barra de estado: `🟢 Libres: 29   🟡 Reservadas: 0   🔴 Ocupadas: 1`
- [ ] Estadísticas: `Libres: 29`, `Reservadas: 0`, `Ocupadas: 1`, `Total: 30`

---

## 📋 BLOQUE 6 — Pruebas de Diseño Visual (Integridad Estética)

> **Objetivo:** Verificar que el diseño no se rompe ante diferentes interacciones y estados.

### TC-050 — Verificar la Leyenda de colores (PanelLeyenda)

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelLeyenda` |
| **Pasos** | 1. Observar el panel de leyenda debajo de la grilla |

**Verificación:**
- [ ] La leyenda tiene fondo de tarjeta oscura con bordes redondeados
- [ ] Tres ítems en fila: cuadrado **verde** + "Libre", cuadrado **amarillo** + "Reservado", cuadrado **rojo** + "Ocupado"
- [ ] Los cuadros de color tienen bordes redondeados (no son cuadrados perfectos)
- [ ] El texto usa fuente "Segoe UI" en gris claro
- [ ] La leyenda NO se superpone con ningún otro elemento

---

### TC-051 — Verificar aspecto visual de los botones de operación

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` — Botones |
| **Pasos** | 1. Observar los tres botones de operación en el PanelControl |

**Verificación:**
- [ ] **"Reservar Butaca"**: color violeta/púrpura (`#8b5cf6`), texto blanco, sin borde visible
- [ ] **"Cancelar Reserva"**: color rojo (`#ef4444`), texto blanco, sin borde visible
- [ ] **"Contar Libres"**: color verde (`#22c55e`), texto blanco, sin borde visible
- [ ] Los tres botones tienen el mismo ancho y alto (`altura 38px`)
- [ ] El cursor sobre los botones es **manita** (HAND cursor)
- [ ] La fuente de los botones es "Segoe UI Bold 12"

---

### TC-052 — Verificar tarjetas del PanelControl

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` — Tarjetas |
| **Pasos** | Observar las tres tarjetas del panel de control |

**Verificación:**
- [ ] Cada tarjeta tiene **fondo oscuro** (`#1c1c32`) con **bordes redondeados** (radio 14)
- [ ] Cada tarjeta tiene un **borde sutil** de 1px en gris (`#333350`)
- [ ] Las tarjetas tienen padding interno visible (los elementos no están pegados a los bordes)
- [ ] El panel de control completo tiene ancho fijo de **260px** y se mantiene a la derecha

---

### TC-053 — Verificar estadísticas coloreadas en PanelControl

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelControl` — Estadísticas |
| **Pasos** | 1. Reservar algunas butacas y ocupar otras. 2. Observar la tarjeta de Estadísticas |

**Verificación:**
- [ ] La cifra de **Libres** aparece en **verde** (`#22c55e`) con negrita
- [ ] La cifra de **Reservadas** aparece en **amarillo** (`#fbbf24`) con negrita
- [ ] La cifra de **Ocupadas** aparece en **rojo** (`#ef4444`) con negrita
- [ ] La cifra de **Total** aparece en **gris** (`#94a3b8`) con negrita
- [ ] Las cifras son correctas y no están invertidas

---

### TC-054 — Verificar que las butacas tienen forma de silla

| Campo | Detalle |
|-------|---------|
| **Componente** | `BotonButaca.paintComponent()` |
| **Pasos** | Observar visualmente cualquier botón de butaca |

**Verificación:**
- [ ] Cada butaca tiene un **respaldo** (franja más oscura) en la parte superior
- [ ] El **cuerpo** del asiento ocupa la parte principal (color base del estado)
- [ ] Hay un **borde sutil blanco semi-transparente** alrededor del cuerpo
- [ ] La etiqueta `F-C` (por ejemplo `1-1`) aparece centrada en la butaca con fuente pequeña blanca
- [ ] El diseño es **consistente** en todas las butacas de la grilla

---

### TC-055 — Verificar encabezados de la grilla

| Campo | Detalle |
|-------|---------|
| **Componente** | `PanelSala` — Encabezados |

**Verificación:**
- [ ] La fila 0 muestra `C1`, `C2`, `C3`, `C4`, `C5`, `C6` centrados en gris
- [ ] La columna 0 muestra `F1`, `F2`, `F3`, `F4`, `F5` en gris a la izquierda
- [ ] Los encabezados tienen la misma fuente ("Segoe UI Bold 11") y no se superponen con las butacas
- [ ] El espaciado entre butacas es uniforme (4px vertical, 5px horizontal)

---

## 📋 BLOQUE 7 — Pruebas de Cierre y Diálogo de Salida

> **Objetivo:** Verificar el comportamiento del diálogo de confirmación al cerrar la ventana.

### TC-060 — Cerrar ventana con el botón "X" y confirmar salida

| Campo | Detalle |
|-------|---------|
| **Componente** | `MainFrame.onCerrarVentana()` |
| **Pasos** | 1. Clic en la **X** de la ventana principal |
| **Resultado esperado** | Aparece un diálogo de confirmación |

**Verificación:**
- [ ] Aparece un `JOptionPane` de pregunta (`?`) con el texto: `¿Está seguro que desea salir del sistema?`
- [ ] El título es: **`Confirmar salida`**
- [ ] Hay dos botones: **"Sí"** y **"No"**
- [ ] El ícono del diálogo es el de pregunta (no error, no información)

---

### TC-061 — Confirmar salida (clic en "Sí")

| Campo | Detalle |
|-------|---------|
| **Precondición** | TC-060 ejecutado (diálogo visible) |
| **Pasos** | 1. Clic en **"Sí"** |
| **Resultado esperado** | La aplicación **se cierra completamente** |

**Verificación:**
- [ ] La ventana de la aplicación desaparece
- [ ] El proceso Java termina (la JVM se cierra)
- [ ] No quedan ventanas huérfanas ni procesos zombi

---

### TC-062 — Cancelar cierre (clic en "No")

| Campo | Detalle |
|-------|---------|
| **Precondición** | TC-060 ejecutado (diálogo visible) |
| **Pasos** | 1. Clic en **"No"** |
| **Resultado esperado** | El diálogo cierra y la aplicación **permanece abierta** |

**Verificación:**
- [ ] La ventana principal sigue visible y completamente funcional
- [ ] El estado de la sala **no cambia** (butacas siguen como estaban)
- [ ] Se puede seguir interactuando normalmente con el sistema

---

### TC-063 — Cerrar el diálogo de salida con su propia "X"

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Clic en la X de la ventana principal. 2. En el diálogo de confirmación, clic en la **X** del propio diálogo |
| **Resultado esperado** | El diálogo cierra y la aplicación permanece abierta |

**Verificación:**
- [ ] Equivale a presionar "No": la aplicación sigue funcionando
- [ ] La ventana principal queda activa

---

## 📋 BLOQUE 8 — Pruebas de Escenarios Completos (End-to-End)

### TC-070 — Sala llena: reservar todas las butacas

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Hacer clic en **todas las butacas** (5×6 = 30) hasta que todas queden en RESERVADO (amarillo) |
| **Resultado esperado** | La sala completa en amarillo, sin errores visuales |

**Verificación:**
- [ ] Las 30 butacas están en amarillo
- [ ] Barra de estado: `🟢 Libres: 0   🟡 Reservadas: 30   🔴 Ocupadas: 0`
- [ ] Estadísticas: `Libres: 0`, `Reservadas: 30`, `Total: 30`
- [ ] La grilla no tiene distorsiones visuales con todas las butacas en estado RESERVADO
- [ ] El PanelLeyenda sigue visible y correcto

---

### TC-071 — Intentar reservar una butaca que ya está en RESERVADO (todos los 30 asientos reservados)

| Campo | Detalle |
|-------|---------|
| **Precondición** | TC-070 completado (sala llena en RESERVADO) |
| **Pasos** | 1. Spinners: Fila=1, Columna=1. 2. Clic en **"Reservar Butaca"** → **"Confirmar"** |
| **Resultado esperado** | Error: `AsientoYaReservadoException` |

**Verificación:**
- [ ] Diálogo de error: **`Este asiento ya fue reservado.`**
- [ ] La sala sigue llena en amarillo (sin cambios)

---

### TC-072 — Escenario de operación mixta (estado complejo)

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Reservar F1-C1, F1-C2, F1-C3 (desde grilla). 2. Ocupar F2-C1, F2-C2 (desde grilla: 2 clics cada uno). 3. Desde PanelControl, cancelar F1-C2. 4. Desde PanelControl, usar "Contar Libres" |
| **Estado final esperado** | F1-C1: amarillo, F1-C2: verde, F1-C3: amarillo, F2-C1: rojo, F2-C2: rojo, resto: verde |

**Verificación:**
- [ ] El estado de cada butaca es exactamente el esperado
- [ ] "Contar Libres" muestra: `Libres: 25`, `Reservadas: 2`, `Ocupadas: 2`, `Total: 30`  
- [ ] La barra de estado dice: `🟢 Libres: 25   🟡 Reservadas: 2   🔴 Ocupadas: 2` *(ajustar números al estado real)*
- [ ] El diseño visual permanece íntegro (sin superposiciones, sin colores incorrectos)

---

## 📋 BLOQUE 9 — Pruebas de Redimensionamiento (Responsividad de Swing)

### TC-080 — Redimensionar la ventana al tamaño máximo

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Maximizar la ventana (botón cuadrado en la barra de título) |
| **Resultado esperado** | La interfaz se adapta al nuevo tamaño |

**Verificación:**
- [ ] El header se estira horizontalmente con el gradiente correcto
- [ ] La grilla de butacas queda centrada o utiliza el espacio disponible
- [ ] El panel de control mantiene su ancho de **260px** a la derecha
- [ ] La leyenda se mantiene visible debajo de la grilla
- [ ] La barra de estado se estira horizontalmente
- [ ] No hay elementos cortados ni superpuestos

---

### TC-081 — Redimensionar la ventana al mínimo permitido (820×600)

| Campo | Detalle |
|-------|---------|
| **Pasos** | 1. Reducir la ventana al tamaño mínimo |
| **Resultado esperado** | La interfaz es usable sin distorsiones |

**Verificación:**
- [ ] Todos los elementos son visibles (no cortados)
- [ ] Los botones del panel de control son clickeables
- [ ] La grilla de butacas no se deforma
- [ ] El texto del header es legible
- [ ] La barra de estado es legible

---

## 📋 BLOQUE 10 — Resumen de Excepciones y sus Mensajes Esperados

> Referencia rápida para verificar que cada excepción produce el mensaje correcto.

| Excepción | Acción que la genera | Mensaje en pantalla | Componente |
|-----------|---------------------|---------------------|------------|
| `AsientoYaReservadoException` | Reservar butaca RESERVADA | `Este asiento ya fue reservado.` | PanelControl |
| `AsientoOcupadoException` | Reservar butaca OCUPADA | `Este asiento está ocupado.` | PanelControl |
| `AsientoNoReservadoException` | Cancelar butaca LIBRE | `Este asiento no está reservado, no se puede cancelar.` | PanelControl |
| `AsientoNoReservadoException` | Cancelar butaca OCUPADA | `Este asiento no está reservado, no se puede cancelar.` | PanelControl |
| `AsientoNoReservadoException` | Click en butaca LIBRE/OCUPADA sin reserva | `Este asiento no tiene una reserva activa.` | PanelSala |
| `AsientoOcupadoException` | Click complejo (no alcanzable en uso normal) | `Este asiento está ocupado.` | PanelSala |
| `PosicionInvalidaException` | Posición fuera de rango (no alcanzable por spinners) | `Posición inválida: [detalle]` | Ambos |

> [!WARNING]
> La `PosicionInvalidaException` **no es alcanzable** mediante el uso normal de la interfaz gráfica, ya que los `JSpinner` limitan el rango válido (Fila 1-5, Columna 1-6). Solo sería provocable mediante modificación directa del código o pruebas unitarias programáticas.

---

## 📋 BLOQUE 11 — Lista de Verificación Final (Checklist de Regresión)

Ejecutar después de cualquier cambio en el código para asegurar que nada se rompió:

### Funcionalidad Core
- [ ] Click en grilla: LIBRE → RESERVADO ✓
- [ ] Click en grilla: RESERVADO → OCUPADO ✓
- [ ] Click en grilla: OCUPADO → LIBRE ✓
- [ ] Botón "Reservar Butaca" + DialogReserva + Confirmar: reserva correctamente ✓
- [ ] Botón "Reservar Butaca" + DialogReserva + Cancelar: no reserva ✓
- [ ] Botón "Cancelar Reserva": cancela butaca RESERVADA ✓
- [ ] Botón "Cancelar Reserva" en LIBRE: muestra error correcto ✓
- [ ] Botón "Cancelar Reserva" en OCUPADA: muestra error correcto ✓
- [ ] Botón "Contar Libres": muestra conteo correcto ✓
- [ ] Cierre con X: muestra diálogo de confirmación ✓

### Sincronización
- [ ] Operación desde PanelControl → grilla actualiza ✓
- [ ] Operación desde grilla → estadísticas de PanelControl actualizan ✓
- [ ] Operación desde grilla → barra de estado actualiza ✓

### Visuales
- [ ] Colores de butacas correctos (verde/amarillo/rojo) ✓
- [ ] Efecto hover en butacas ✓
- [ ] Tooltips correctos ✓
- [ ] Estadísticas en colores correctos (verde/amarillo/rojo/gris) ✓
- [ ] Leyenda visible y correcta ✓
- [ ] Barra de estado visible y correcta ✓
- [ ] Diseño oscuro íntegro (sin fondo blanco inesperado) ✓

---

## 🐛 Plantilla de Reporte de Defecto

Cuando encuentres un defecto, documentarlo con este formato:

```
ID Defecto:     DEF-XXX
TC Relacionado: TC-XXX
Componente:     [PanelSala / PanelControl / DialogReserva / BotonButaca / MainFrame]
Severidad:      [Alta / Media / Baja]
Tipo:           [Excepción no manejada / Visual roto / Mensaje incorrecto / Funcionalidad incorrecta]

Descripción:
[Descripción breve del defecto]

Pasos para reproducir:
1. ...
2. ...

Resultado obtenido:
[Lo que ocurrió realmente]

Resultado esperado:
[Lo que debería haber ocurrido]

Evidencia:
[Captura de pantalla / descripción visual]
```

---

*Manual generado para uso exclusivo del equipo de QA — Sistema de Gestión de Butacas de Cine v1.0*
