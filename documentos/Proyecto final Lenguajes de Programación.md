# Proyecto final Lenguajes de Programación

## LENGUAJES DE PROGRAMACIÓN - MONOGRAFÍA DEL PROYECTO FINAL

### OBJETIVO 
El proyecto está orientado a aplicar los conocimientos adquiridos en el curso de Lenguajes de programación, aplicando diversos paradigmas de programación.

### NOMBRE DEL PROYECTO
**--- SISTEMA DE GESTIÓN DE BUTACAS DE CINE ---**

### PLANTEAMIENTO DEL CASO
Una sala de cine necesita un programa que permita gestionar la disponibilidad de sus butacas. La sala estará representada mediante un arreglo bidimensional, donde las filas y columnas representan los asientos disponibles.

Cada asiento podrá estar:
- Libre 
- Reservado 
- Ocupado 

El sistema debe permitir consultar la sala, reservar butacas, cancelar reservas y calcular la cantidad de butacas disponibles.

### OBJETIVO DEL PROYECTO
Desarrollar una aplicación sencilla aplicando:
- Tres paradigmas de programación vistos en curso. 
- Arreglos bidimensionales.

### CLASES SUGERIDAS

#### Clase `Butaca`
Representa un asiento individual.
- **Atributos:**
  - `fila`
  - `columna`
  - `estado`
- **Métodos sugeridos:**
  - `reservar()`
  - `ocupar()`
  - `liberar()`
  - `estado()`
  - `mostrarEstado()`

#### Clase `SalaCine`
Representa toda la sala del cine.
- **Atributos:**
  - `butacas[][]` (arreglo bidimensional)
  - `butaca`
  - `filas`
  - `columnas`
- **Métodos sugeridos:**
  - `mostrarSala()`
  - `reservarButaca()`
  - `cancelarReserva()`
  - `contarButacasLibres()`

#### Clase Principal
Contiene el menú principal del programa.

```text
[[[[ GESTIÓN DE BUTACAS DE CINE ]]]]
1. Mostrar butacas de sala
2. Reservar butaca
3. Cancelar reserva
4. Contar butacas libres
5. Salir
```

### REPRESENTACIÓN VISUAL DE LA SALA
Ejemplo para una sala de 5 filas x 6 columnas:

**Leyenda:**
- **L** = Libre
- **R** = Reservado
- **O** = Ocupado

|    | C1 | C2 | C3 | C4 | C5 | C6 |
|----|----|----|----|----|----|----|
| **F1** | L  | L  | L  | L  | L  | L  |
| **F2** | L  | R  | L  | L  | O  | L  |
| **F3** | L  | L  | L  | R  | L  | L  |
| **F4** | O  | L  | L  | L  | L  | L  |
| **F5** | L  | L  | R  | L  | L  | L  |

### REGLAS DEL SISTEMA
1. No se puede reservar una butaca ocupada. 
2. No se puede reservar una butaca ya reservada. 
3. Solo se puede cancelar una reserva si la butaca está reservada. 
4. El usuario debe ingresar una fila y columna válidas para una butaca. 
5. El sistema debe mostrar mensajes claros al usuario. 

### ACTIVIDADES PARA LOS ALUMNOS
Los alumnos deberán:
- Crear la clase `Butaca`. 
- Crear la clase `SalaCine`. 
- Usar un arreglo bidimensional de objetos `Butaca`. 
- Implementar un menú interactivo. 
- Validar los datos ingresados. 
- Mostrar el estado actualizado de la sala. 

### RESULTADO ESPERADO
Al finalizar, el alumno habrá desarrollado una aplicación sencilla que permite administrar las butacas de una sala de cine, aplicando correctamente diversos paradigmas de programación y arreglos bidimensionales.

### ENTREGABLES
- Código fuente en Java del programa con todos los casos implementados.
- Un análisis final que describa los resultados obtenidos al probar el sistema con distintos datos.

### CRITERIOS DE EVALUACIÓN
- Vestimenta formal.
- Dominio del tema asignado durante la exposición del proyecto por parte del equipo.
- Correcto funcionamiento de cada opción del menú.
- Precisión en los resultados.
- Claridad y buena estructuración del código de programación.
- Comentarios en el código de programación que indique el inicio de cada rutina o uso de paradigma de programación.

> Este proyecto no solo les permitirá aplicar conocimientos de programación, sino también entender conceptos básicos de análisis de datos y toma de decisiones en función de resultados del sistema. 

**¡A trabajar y buena suerte!**