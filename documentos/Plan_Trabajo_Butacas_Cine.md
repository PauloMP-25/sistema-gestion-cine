# 🎬 PLAN DE TRABAJO: SISTEMA DE GESTIÓN DE BUTACAS DE CINE

**Objetivo:** Desarrollar el sistema yendo *directo a la programación*, sin diagramas UML ni documentación externa extensa. El código debe ser la única fuente de verdad, aplicando **4 paradigmas de programación** (Orientado a Objetos, Imperativo, Funcional y Orientado a Eventos).

**Tamaño del Equipo:** 6 Personas (3 Backend, 2 Frontend, 1 QA).

---

## 1. EQUIPO BACKEND (3 Desarrolladores)

Este equipo construye **toda** la lógica del sistema. Su código no debe depender de ninguna clase visual. El Frontend depende del Backend, nunca al revés.

---

### 🗂️ Arquitectura del Proyecto (3 Capas)

El proyecto se organiza en **3 capas** representadas como paquetes Java. Esta estructura debe crearse desde el primer día y todos los archivos deben vivir en su carpeta correcta:

```
sistema-gestion-cine/
│
├── src/
│   ├── model/                      ← Capa de Datos (Entidades)
│   │   ├── Butaca.java
│   │   └── EstadoButaca.java       (enum)
│   │
│   ├── service/                    ← Capa de Lógica de Negocio
│   │   ├── interfaces/
│   │   │   ├── ISalaService.java   (contrato de operaciones)
│   │   │   └── ISalaQuery.java     (contrato de consultas)
│   │   ├── SalaService.java        (implementa ISalaService)
│   │   ├── SalaQuery.java          (implementa ISalaQuery)
│   │   ├── SalaManager.java        (Singleton: gestiona la sala)
│   │   └── Validador.java          (métodos estáticos de validación)
│   │
│   ├── exception/                  ← Excepciones personalizadas
│   │   ├── AsientoOcupadoException.java
│   │   ├── AsientoYaReservadoException.java
│   │   ├── AsientoNoReservadoException.java
│   │   └── PosicionInvalidaException.java
│   │
│   └── view/                       ← Capa Visual (SOLO del equipo Frontend)
│       ├── MainFrame.java
│       └── PanelSala.java
│
└── test/                           ← Pruebas del QA
    └── SalaServiceTest.java
```

> **Regla de Oro:** Un archivo en `model/` no puede hacer `import service.*`. Un archivo en `service/` no puede hacer `import view.*`. Las dependencias van **de arriba hacia abajo** en la estructura.

---

### 📌 Clases del Backend: Mapa Completo

| Clase / Archivo | Capa | Responsable | Propósito |
|---|---|---|---|
| `EstadoButaca.java` | model | Dev 1 | Enum con los 3 estados posibles. |
| `Butaca.java` | model | Dev 1 | Entidad que representa un asiento. |
| `ISalaService.java` | service/interfaces | Dev 2 | Contrato de operaciones (reservar, cancelar, etc). |dime 
| `ISalaQuery.java` | service/interfaces | Dev 3 | Contrato de consultas (contar libres, listar, etc). |
| `SalaManager.java` | service | Dev 2 | Singleton. Inicializa y posee la matriz `Butaca[][]`. |
| `SalaService.java` | service | Dev 2 | Implementa `ISalaService`. Reglas de reserva/cancelación. |
| `SalaQuery.java` | service | Dev 3 | Implementa `ISalaQuery`. Consultas con Streams. |
| `Validador.java` | service | Dev 3 | Métodos estáticos para validar rangos y estados. |
| `AsientoOcupadoException.java` | exception | Dev 3 | Lanzada si se intenta reservar un asiento OCUPADO. |
| `AsientoYaReservadoException.java` | exception | Dev 3 | Lanzada si se intenta reservar un asiento ya RESERVADO. |
| `AsientoNoReservadoException.java` | exception | Dev 3 | Lanzada si se intenta cancelar un asiento que no está RESERVADO. |
| `PosicionInvalidaException.java` | exception | Dev 3 | Lanzada si la fila o columna está fuera de rango. |

---

### 📜 Reglas de Negocio (Business Rules)

El código debe implementar estas reglas de forma estricta y sin excepción:

1. **Límites:** No se puede interactuar con una butaca cuya fila o columna esté fuera de `[0, MAX_FILAS-1]` o `[0, MAX_COLS-1]`. Si ocurre, lanzar `PosicionInvalidaException`.
2. **Reserva — Estado requerido `LIBRE`:** Si la butaca está `OCUPADO`, lanzar `AsientoOcupadoException`. Si está `RESERVADO`, lanzar `AsientoYaReservadoException`. Solo si está `LIBRE` se cambia a `RESERVADO`.
3. **Cancelación — Estado requerido `RESERVADO`:** Solo si la butaca está `RESERVADO` se cambia a `LIBRE`. Si está en cualquier otro estado, lanzar `AsientoNoReservadoException`.
4. **Inmutabilidad de la consulta:** Los métodos de `SalaQuery` **nunca** modifican el estado de ninguna butaca. Solo leen.
5. **Constantes obligatorias:** Los tamaños de la sala se definen como constantes en `SalaManager`:
   ```java
   public static final int MAX_FILAS = 5;
   public static final int MAX_COLS  = 6;
   ```

---

### 📐 Principios SOLID aplicados

| Principio | Cómo se aplica en este proyecto |
|---|---|
| **S** — Single Responsibility | `Butaca` solo representa el asiento. `SalaManager` solo gestiona la matriz. `Validador` solo valida. Ninguna clase hace más de una cosa. |
| **O** — Open/Closed | Si en el futuro se agrega lógica (ej. "butaca VIP"), se crea una subclase de `Butaca`, no se modifica `Butaca.java`. |
| **L** — Liskov Substitution | Las excepciones personalizadas extienden `RuntimeException`. Se pueden usar donde se espera una excepción genérica sin romper el sistema. |
| **I** — Interface Segregation | Las interfaces están separadas: `ISalaService` (operaciones de escritura) e `ISalaQuery` (operaciones de lectura). El Frontend solo recibe `ISalaService`, no `ISalaQuery`. |
| **D** — Dependency Inversion | El Frontend **no instancia** `SalaService` directamente. Recibe una interfaz `ISalaService`. Esto permite sustituir la implementación sin cambiar el Frontend. |

---

### 🔨 Patrones de Diseño a usar

1. **Singleton — `SalaManager`:**
   La sala solo puede existir una vez. `SalaManager` controla su propia instancia. Se accede mediante `SalaManager.getInstance()`.
   ```java
   // Solo existe UNA sala en toda la aplicación
   private static SalaManager instancia;
   public static SalaManager getInstance() {
       if (instancia == null) instancia = new SalaManager();
       return instancia;
   }
   ```

2. **Facade (Fachada) — `SalaService`:**
   El Frontend solo llama a `salaService.reservar(fila, col)`. Internamente, `SalaService` coordina la validación (`Validador`), el acceso a la matriz (`SalaManager`) y el cambio de estado (`Butaca`). El Frontend no sabe cómo funciona por dentro.

3. **Strategy (implícito) — Excepciones:**
   En lugar de devolver códigos de error numéricos, se lanzan excepciones específicas. El Frontend decide qué mensaje mostrar según el tipo de excepción que recibe.

---

### 📏 Reglas de Escritura de Métodos (Obligatorio)

- **Máximo ~15 líneas por método.** Si un método crece más, se debe dividir en métodos privados auxiliares con nombres descriptivos.
- **Un método = una acción.** `reservarButaca()` no valida, reserva Y actualiza el contador. Cada responsabilidad es un método separado:
  ```
  reservarButaca(fila, col)
      └── llama a: validarPosicion(fila, col)
      └── llama a: verificarEstadoParaReserva(butaca)
      └── llama a: cambiarEstado(butaca, RESERVADO)
  ```
- **Nombres de métodos son verbos:** `reservar()`, `cancelar()`, `contar()`, `validarRango()`. Nunca nombres genéricos como `proceso()` o `hacer()`.
- **Parámetros máximo 3.** Si un método necesita más, es señal de que hace demasiado.

---

### 🧠 Paradigmas a usar

1. **Orientado a Objetos:** Clases, encapsulamiento (atributos `private`, getters/setters), herencia en excepciones personalizadas.
2. **Imperativo / Estructurado:** Bucles `for` anidados para inicializar la matriz. Condicionales `if/else` secuenciales para validar estado y rango.
3. **Funcional:** Streams de Java en `SalaQuery` para consultas declarativas:
   ```java
   // PARADIGMA: Funcional — contar butacas libres sin bucle explícito
   Arrays.stream(matriz)
         .flatMap(Arrays::stream)
         .filter(b -> b.getEstado() == EstadoButaca.LIBRE)
         .count();
   ```

---

### 📝 Javadoc y Comentarios (Obligatorio)

**Javadoc:** Añadir en la cabecera de cada clase y en cada método `public`. Debe ser breve y claro:
```java
/**
 * Representa un asiento individual de la sala de cine.
 * Gestiona su estado: LIBRE, RESERVADO u OCUPADO.
 */
public class Butaca { ... }

/**
 * Reserva la butaca en la posición indicada.
 * @param fila    Índice de fila (0 a MAX_FILAS-1)
 * @param columna Índice de columna (0 a MAX_COLS-1)
 * @throws PosicionInvalidaException    si la posición está fuera de rango.
 * @throws AsientoOcupadoException      si el asiento está OCUPADO.
 * @throws AsientoYaReservadoException  si el asiento ya está RESERVADO.
 */
public void reservar(int fila, int columna) { ... }
```

**Comentarios internos de paradigmas** (requeridos por el profesor):
```java
// PARADIGMA: Orientado a Objetos — Encapsulamiento de estado
// PARADIGMA: Imperativo — Recorrido secuencial de la matriz
// PARADIGMA: Funcional — Consulta declarativa con Streams
// INICIO RUTINA: Validación de posición
// FIN RUTINA: Validación de posición
```

---

## 2. EQUIPO FRONTEND - JFRAME (2 Desarrolladores)

Este equipo construye **toda** la capa visual del sistema usando **Java Swing**. Solo habla con el Backend a través de las interfaces `ISalaService` e `ISalaQuery`, nunca con clases concretas.

---

### 🗂️ Paquete `view/` — Clases del Frontend

Todos los archivos de este equipo viven dentro del paquete `view/`:

```
src/
└── view/
    ├── MainFrame.java          ← Ventana principal (JFrame raíz)
    ├── PanelSala.java          ← Panel con la matriz visual de butacas
    ├── PanelControl.java       ← Panel con botones de acción
    ├── PanelLeyenda.java       ← Panel que muestra la leyenda de colores
    ├── BotonButaca.java        ← JButton personalizado que representa una butaca
    ├── DialogReserva.java      ← JDialog modal de confirmación
    └── UIConstants.java        ← Constantes visuales (colores, fuentes, tamaños)
```

> **Regla de Oro del Frontend:** Ninguna clase en `view/` puede hacer `import service.SalaService` o `import service.SalaManager` directamente. Solo puede importar las **interfaces** (`import service.interfaces.ISalaService`). La instancia concreta la entrega el QA al momento de integrar.

---

### 📌 Mapa de Clases del Frontend

| Clase | Responsable | Componente Swing | Propósito |
|---|---|---|---|
| `UIConstants.java` | Dev 4 | — | Define todos los colores, fuentes y dimensiones. No hay "números mágicos" en los demás archivos. |
| `MainFrame.java` | Dev 4 | `JFrame` | Ventana raíz. Contiene todos los paneles y gestiona el layout principal (`BorderLayout`). |
| `PanelControl.java` | Dev 4 | `JPanel` | Botones de acción: Reservar, Cancelar Reserva, Contar Libres. Cada botón tiene su `ActionListener`. |
| `DialogReserva.java` | Dev 4 | `JDialog` | Ventana modal que aparece al confirmar una reserva. Muestra fila, columna y pide confirmación. |
| `PanelLeyenda.java` | Dev 4 | `JPanel` | Muestra la leyenda de colores (Verde=Libre, Amarillo=Reservado, Rojo=Ocupado). |
| `BotonButaca.java` | Dev 5 | `JButton` | Botón personalizado. Tiene `fila` y `columna` como atributos. Cambia de color según el `EstadoButaca`. |
| `PanelSala.java` | Dev 5 | `JPanel` + `GridLayout` | Crea dinámicamente la grilla de `BotonButaca` y la etiqueta de filas/columnas. |

---

### 📐 Principios SOLID aplicados al Frontend

| Principio | Cómo se aplica |
|---|---|
| **S** — Single Responsibility | `PanelSala` solo dibuja la grilla. `PanelControl` solo gestiona los botones. `PanelLeyenda` solo muestra la leyenda. Ninguno hace todo. |
| **O** — Open/Closed | Si se necesita agregar un nuevo botón de acción, se agrega a `PanelControl` sin tocar `MainFrame` ni `PanelSala`. |
| **L** — Liskov Substitution | `BotonButaca` extiende `JButton`. Se puede usar en cualquier lugar donde se espere un `JButton`. |
| **I** — Interface Segregation | El Frontend recibe `ISalaService` para operaciones y opcionalmente `ISalaQuery` para consultas. Nunca la misma interfaz para todo. |
| **D** — Dependency Inversion | `MainFrame` recibe `ISalaService` e `ISalaQuery` en su constructor, **no los instancia**. Esto permite que el QA inyecte las implementaciones reales al integrar. |

**Ejemplo de Inyección de Dependencias en `MainFrame`:**
```java
// CORRECTO — depende de la abstracción, no de la implementación
public MainFrame(ISalaService salaService, ISalaQuery salaQuery) {
    this.salaService = salaService;
    this.salaQuery   = salaQuery;
    inicializarComponentes();
}

// INCORRECTO — el Frontend nunca debe hacer esto:
// SalaService salaService = new SalaService(); ← PROHIBIDO
```

---

### 🔨 Patrones de Diseño a usar

1. **Observer / Listener (nativo de Swing):**
   El patrón Observer es la base del paradigma Orientado a Eventos. Cada `BotonButaca` es un *sujeto observable*. Los `ActionListener` son los *observadores*. Cuando el usuario hace clic, el listener reacciona.
   ```java
   // PARADIGMA: Orientado a Eventos — ActionListener como Observer
   botonButaca.addActionListener(e -> onButacaClick(boton.getFila(), boton.getColumna()));
   ```

2. **Factory Method (implícito) — `PanelSala`:**
   `PanelSala` crea dinámicamente todos los `BotonButaca` en un bucle. Centraliza la creación de los botones en un único lugar, facilitando cambios futuros.
   ```java
   // INICIO RUTINA: Creación de la grilla de butacas
   for (int i = 0; i < MAX_FILAS; i++) {
       for (int j = 0; j < MAX_COLS; j++) {
           BotonButaca boton = crearBotonButaca(i, j); // método privado auxiliar
           panelGrilla.add(boton);
       }
   }
   // FIN RUTINA: Creación de la grilla de butacas
   ```

3. **MVC (Modelo-Vista-Controlador) simplificado:**
   - **Modelo:** Las clases del Backend (`Butaca`, `SalaManager`).
   - **Vista:** Las clases del Frontend (`PanelSala`, `MainFrame`).
   - **Controlador:** Los `ActionListener` dentro de `PanelControl` que reciben el evento, llaman al servicio y actualizan la vista.

---

### 📏 Reglas de Escritura de Métodos (Obligatorio)

- **Máximo ~15 líneas por método.** Si un `ActionListener` crece, se extrae su lógica a un método privado con nombre descriptivo:
  ```
  // EN LUGAR DE ESTO (lambda gigante):
  btnReservar.addActionListener(e -> { ...30 líneas... });

  // HACER ESTO (delegar a método privado):
  btnReservar.addActionListener(e -> onReservarClick());

  private void onReservarClick() {
      // lógica aquí, máximo ~15 líneas
      // llamar a submétodos si es necesario
  }
  ```
- **Nombrar los handlers con el prefijo `on`:** `onReservarClick()`, `onCancelarClick()`, `onContarClick()`, `onButacaClick(fila, col)`.
- **Separar la inicialización de la lógica:** Un método `inicializarComponentes()` solo crea y posiciona componentes. Un método `configurarEventos()` solo agrega los listeners. Nunca mezclados.

---

### 🎨 Reglas Visuales y de UI

- **Constantes en `UIConstants.java`** (ningún color hardcodeado en otro lugar):
  ```java
  public class UIConstants {
      public static final Color COLOR_LIBRE     = new Color(76, 175, 80);   // Verde
      public static final Color COLOR_RESERVADO = new Color(255, 193, 7);   // Amarillo
      public static final Color COLOR_OCUPADO   = new Color(244, 67, 54);   // Rojo
      public static final Font  FUENTE_BOTON    = new Font("Arial", Font.BOLD, 12);
      public static final int   TAMANO_BOTON    = 60; // px
  }
  ```
- **Leyenda obligatoria:** `PanelLeyenda` debe estar siempre visible en la ventana.
- **`JOptionPane` para todos los mensajes:** Nunca imprimir en consola desde la vista (`System.out.println` está prohibido en `view/`).
- **Captura de excepciones del Backend en la Vista:**
  ```java
  try {
      salaService.reservar(fila, col);
      boton.actualizarColor(EstadoButaca.RESERVADO);
  } catch (AsientoOcupadoException ex) {
      JOptionPane.showMessageDialog(this, "Este asiento está ocupado.", "Error", JOptionPane.ERROR_MESSAGE);
  } catch (AsientoYaReservadoException ex) {
      JOptionPane.showMessageDialog(this, "Este asiento ya fue reservado.", "Error", JOptionPane.ERROR_MESSAGE);
  }
  ```

---

### 🧠 Paradigma a usar

4. **Orientado a Eventos:** Es el paradigma principal de este equipo. Todo flujo parte de un evento del usuario:

| Evento del Usuario | Listener que lo captura | Acción resultante |
|---|---|---|
| Clic en un `BotonButaca` | `ActionListener` en `BotonButaca` | Llama a `salaService.reservar()` y actualiza el color |
| Clic en "Cancelar Reserva" | `ActionListener` en `PanelControl` | Llama a `salaService.cancelar()` y refresca la grilla |
| Clic en "Contar Libres" | `ActionListener` en `PanelControl` | Llama a `salaQuery.contarLibres()` y muestra `JOptionPane` |
| Cierre de ventana (`X`) | `WindowListener` en `MainFrame` | Muestra `JDialog` de confirmación antes de cerrar |

---

### 📝 Javadoc y Comentarios (Obligatorio)

**Javadoc** en cabecera de clase y métodos `public`:
```java
/**
 * Panel visual que representa la matriz de butacas de la sala.
 * Genera dinámicamente los botones y sincroniza su color con el estado del backend.
 */
public class PanelSala extends JPanel { ... }

/**
 * Actualiza el color del botón según el estado actual de la butaca en el backend.
 * @param estado El nuevo estado de la butaca (LIBRE, RESERVADO u OCUPADO).
 */
public void actualizarColor(EstadoButaca estado) { ... }
```

**Comentarios internos de paradigmas** (requeridos por el profesor):
```java
// PARADIGMA: Orientado a Eventos — ActionListener reacciona al clic del usuario
// INICIO RUTINA: Renderizado de la grilla de butacas
// FIN RUTINA: Renderizado de la grilla de butacas
// PARADIGMA: Orientado a Objetos — BotonButaca extiende JButton (herencia)
```

---

## 3. EQUIPO QA E INTEGRACIÓN (1 Desarrollador)

Este equipo es el puente entre el Backend y el Frontend. Su responsabilidad es garantizar que el sistema funcione de extremo a extremo, que las capas estén correctamente conectadas y que el entregable final cumpla con todos los criterios de evaluación del profesor.

---

### 🗂️ Archivos y Paquetes de Responsabilidad QA

El QA no crea clases de negocio ni de vista, pero sí tiene sus propios archivos:

```
src/
└── app/
    └── App.java           ← Punto de entrada del sistema (método main)
                              Aquí se instancian los servicios y se inyectan
                              en el MainFrame. Es el único lugar donde se
                              permite usar `new SalaService()`.

test/
└── SalaServiceTest.java   ← Pruebas manuales o automatizadas de los servicios
```

> **Regla de Oro del QA:** `App.java` es el único archivo de todo el proyecto autorizado a instanciar clases concretas del Backend (`new SalaService()`, `new SalaQuery()`) y pasarlas al Frontend. El resto del sistema trabaja con interfaces.

---

### 📌 Mapa de Tareas del Equipo QA

| # | Tarea | Descripción |
|---|---|---|
| 1 | **Integración del sistema** | Escribir `App.java`: instanciar `SalaService`, `SalaQuery` e `ISalaService`, e inyectarlos en el constructor de `MainFrame`. |
| 2 | **Pruebas de reglas de negocio** | Verificar manualmente que las 5 reglas del negocio del Backend se cumplan en la UI. |
| 3 | **Pruebas de casos borde** | Ejecutar los escenarios problemáticos que pueden romper el sistema. |
| 4 | **Auditoría de paradigmas** | Verificar que los 4 paradigmas estén presentes y correctamente comentados en el código. |
| 5 | **Auditoría de arquitectura** | Revisar que ningún archivo esté en el paquete incorrecto ni tenga imports prohibidos. |
| 6 | **Auditoría de Javadoc** | Verificar que todas las clases y métodos `public` del Backend y Frontend tengan Javadoc. |
| 7 | **Cierre y entregable** | Asegurar que el proyecto compila limpio (0 errores, 0 warnings ignorados) y está listo para la exposición. |

---

### 🔌 Integración del Sistema — `App.java`

Este archivo es el más importante del QA. Es donde todas las capas se conectan:

```java
// INICIO RUTINA: Punto de entrada del sistema
public class App {
    public static void main(String[] args) {

        // Única instanciación concreta del sistema
        SalaManager  manager  = SalaManager.getInstance();
        ISalaService servicio = new SalaService(manager);
        ISalaQuery   consulta = new SalaQuery(manager);

        // Inyección de dependencias en el Frontend
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame(servicio, consulta);
            ventana.setVisible(true);
        });
    }
}
// FIN RUTINA: Punto de entrada del sistema
```

---

### 🧪 Casos de Prueba Obligatorios

El equipo QA debe ejecutar y documentar el resultado de cada uno de estos escenarios antes de entregar:

| # | Escenario | Resultado Esperado |
|---|---|---|
| 1 | Reservar una butaca **Libre** | La butaca cambia a color **Amarillo** (RESERVADO). |
| 2 | Reservar una butaca **ya Reservada** | Se muestra `JOptionPane` de error: "Este asiento ya fue reservado." |
| 3 | Reservar una butaca **Ocupada** | Se muestra `JOptionPane` de error: "Este asiento está ocupado." |
| 4 | Cancelar reserva de butaca **Reservada** | La butaca vuelve a color **Verde** (LIBRE). |
| 5 | Cancelar reserva de butaca **Libre** | Se muestra `JOptionPane` de error: la cancelación falla. |
| 6 | Cancelar reserva de butaca **Ocupada** | Se muestra `JOptionPane` de error: la cancelación falla. |
| 7 | Contar butacas libres tras reservas múltiples | El número mostrado es matemáticamente correcto. |
| 8 | Cerrar la ventana con la `X` | Aparece `JDialog` de confirmación antes de salir. |

---

### 🔍 Auditoría de Arquitectura (Checklist)

Antes de la entrega final, verificar cada punto:

- [ ] Todos los archivos de `model/` son solo entidades (sin lógica de Swing, sin `System.out.println`).
- [ ] Ninguna clase en `service/` hace `import javax.swing.*`.
- [ ] Ninguna clase en `view/` hace `import service.SalaService` o `import service.SalaManager`.
- [ ] `App.java` es el único punto de instanciación concreta.
- [ ] Las constantes de tamaño de sala (`MAX_FILAS`, `MAX_COLS`) están definidas en un solo lugar.
- [ ] Las constantes de colores están únicamente en `UIConstants.java`.

---

### 🏷️ Auditoría de Paradigmas (Checklist)

El profesor evaluará que los 4 paradigmas sean identificables. Verificar que existan en el código:

| Paradigma | Dónde debe estar | Comentario requerido |
|---|---|---|
| **Orientado a Objetos** | `Butaca.java`, `BotonButaca.java` | `// PARADIGMA: Orientado a Objetos` |
| **Imperativo / Estructurado** | `SalaService.java`, `SalaManager.java` | `// PARADIGMA: Imperativo` |
| **Funcional** | `SalaQuery.java` (Streams) | `// PARADIGMA: Funcional` |
| **Orientado a Eventos** | `PanelControl.java`, `BotonButaca.java` | `// PARADIGMA: Orientado a Eventos` |

---

### 📝 Javadoc y Comentarios — Auditoría Final

Verificar que cada clase pública tenga al menos este bloque mínimo de Javadoc:
```java
/**
 * [Descripción breve de la clase en una línea.]
 * [Descripción de su responsabilidad principal.]
 */
```

Y cada método `public` tenga:
```java
/**
 * [Descripción de lo que hace el método.]
 * @param nombre [Descripción del parámetro]
 * @return [Qué devuelve, si aplica]
 * @throws [Excepción] [Cuándo se lanza, si aplica]
 */
```