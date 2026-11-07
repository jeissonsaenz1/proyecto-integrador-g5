# FarmaCare -- Sistema de gestión farmacéutica

Aplicación web para la gestión de información farmacéutica, desarrollada
como proyecto integrador. El sistema permite administrar laboratorios,
medicamentos, inventario, ventas y fórmulas, con persistencia en una
base de datos PostgreSQL.

## Tecnologías utilizadas

-   **Java 21**
-   **Spring Boot 4.1.1**
-   **Vaadin 25.3.0** para la interfaz web
-   **Spring Data JPA / Jakarta Persistence** para la persistencia de
    entidades
-   **PostgreSQL** como motor de base de datos
-   **Maven Wrapper 3.9.12** para compilar y ejecutar el proyecto
-   **Apache POI** para generar reportes de inventario en Excel
-   **OpenPDF** para generar reportes de inventario en PDF

## Funcionalidades

La aplicación está organizada en módulos:

-   **Laboratorios:** registro, consulta, edición y eliminación de
    laboratorios.
-   **Medicamentos:** administración de medicamentos y su relación con
    un laboratorio.
-   **Inventarios:** control de lotes, existencias, fechas de
    vencimiento y estado de los registros.
-   **Ventas:** administración de los encabezados de las ventas.
-   **Detalles de venta:** registro de los medicamentos y cantidades que
    componen cada venta.
-   **Fórmulas:** administración de fórmulas.
-   **Fórmula--Medicamento:** asociación de medicamentos con fórmulas.
-   **Reportes:** generación de reportes de inventario en formatos Excel
    y PDF.

Los módulos cuentan con operaciones CRUD (crear, consultar, actualizar y
eliminar) desde la interfaz.

### Control de existencias en las ventas

El descuento de inventario se realiza al registrar un **detalle de
venta**, no al crear únicamente el encabezado de la venta. Al guardar un
detalle, el servicio de inventario busca un lote activo, vigente y con
existencias suficientes, descuenta las unidades y relaciona el detalle
con el lote utilizado.

La lógica contempla también la devolución de unidades al editar o
eliminar un detalle. Al eliminar una venta completa, se restituyen las
unidades asociadas a sus detalles antes de eliminar el registro.

La selección de lotes utiliza el criterio FEFO (*First Expired, First
Out*): se prioriza el lote con la fecha de vencimiento más próxima. La
operación se ejecuta dentro de una transacción para evitar dejar cambios
parciales si ocurre un error.

> **Nota sobre el modelo:** la imagen del modelo entidad-relación
> incluida con el proyecto representa las relaciones principales. La
> implementación del control de inventario en los detalles de venta
> incorpora además la relación opcional `inventario_id` en
> `detalle_venta`; por tanto, conviene actualizar el diagrama para
> reflejarla.

## Modelo de datos

El modelo entidad-relación contempla las siguientes tablas:

  -----------------------------------------------------------------------
  Tabla                               Responsabilidad
  ----------------------------------- -----------------------------------
  `laboratorio`                       Información de los laboratorios.

  `medicamento`                       Datos de los medicamentos y
                                      referencia al laboratorio.

  `inventario`                        Lotes, stock y fechas de
                                      vencimiento de los medicamentos.

  `venta`                             Encabezado de cada venta.

  `detalle_venta`                     Medicamentos, cantidades y
                                      subtotales de cada venta.

  `formula`                           Información de las fórmulas.

  `formula_medicamento`               Relación entre fórmulas y
                                      medicamentos.
  -----------------------------------------------------------------------

### Relaciones principales

-   Un **laboratorio** puede estar asociado con varios medicamentos;
    cada medicamento referencia un laboratorio.
-   Un **medicamento** puede tener varios registros de inventario; cada
    registro de inventario corresponde a un medicamento.
-   Una **venta** puede contener varios detalles de venta; cada detalle
    pertenece a una venta.
-   Un **medicamento** puede aparecer en varios detalles de venta.
-   Una **fórmula** puede relacionarse con varios medicamentos mediante
    `formula_medicamento`.
-   Un **medicamento** puede participar en varias fórmulas mediante
    `formula_medicamento`.
-   En la implementación del descuento de stock, cada detalle de venta
    puede quedar asociado al lote de inventario del que se descontaron
    las unidades.

## Requisitos previos

Antes de ejecutar el proyecto, asegúrese de tener instalado:

1.  JDK 21.
2.  Git.
3.  Acceso a una instancia PostgreSQL.
4.  Conexión a Internet para que Maven pueda descargar las dependencias
    la primera vez.

Verifique las versiones desde PowerShell:

``` powershell
java -version
git --version
```

## Configuración de la base de datos

La aplicación utiliza PostgreSQL. Configure la conexión en el archivo
local:

`src/main/resources/application.properties`

Ajuste la URL JDBC, el usuario y la contraseña de acuerdo con su
instancia PostgreSQL. No publique credenciales reales ni suba este
archivo con secretos al repositorio.

Si el proyecto utiliza variables de entorno para las credenciales,
defínalas en el entorno local antes de iniciar la aplicación y mantenga
los nombres de variables que espera la configuración del proyecto.

La configuración JPA del proyecto puede actualizar el esquema al
iniciar, según el valor establecido para
`spring.jpa.hibernate.ddl-auto`. Revise esa propiedad antes de ejecutar
la aplicación contra una base de datos con información importante.

## Instalación y ejecución

Clone el repositorio:

``` powershell
git clone https://github.com/jeissonsaenz1/proyecto-integrador-g5.git
```

Ingrese a la carpeta del proyecto:

``` powershell
cd proyecto-integrador-g5
```

Compile el proyecto:

``` powershell
.\mvnw.cmd clean compile
```

Ejecute las pruebas, si están configuradas:

``` powershell
.\mvnw.cmd test
```

Inicie la aplicación:

``` powershell
.\mvnw.cmd spring-boot:run
```

Cuando Spring Boot haya iniciado correctamente, abra en el navegador:

<http://localhost:8080>

## Estructura general del proyecto

``` text
src/
└── main/
    ├── java/
    │   └── com/example/
    │       ├── MainView.java
    │       ├── Application.java
    │       ├── model/
    │       │   ├── Laboratorio.java
    │       │   ├── Medicamento.java
    │       │   ├── Inventario.java
    │       │   ├── Venta.java
    │       │   ├── DetalleVenta.java
    │       │   ├── Formula.java
    │       │   └── FormulaMedicamento.java
    │       └── service/
    │           ├── CrudService.java
    │           ├── InventarioVentaService.java
    │           ├── ReporteExcelService.java
    │           └── ReportePdfService.java
    └── resources/
        └── application.properties
```

La estructura anterior resume los componentes principales. Los nombres o
ubicaciones pueden variar si se realizan cambios posteriores en el
código.

## Flujo básico de uso

1.  Registrar los laboratorios.
2.  Registrar los medicamentos y asociarlos con su laboratorio.
3.  Crear los lotes de inventario indicando medicamento, lote, stock y
    fecha de vencimiento.
4.  Crear el encabezado de una venta.
5.  En **Detalles de venta**, seleccionar la venta y el medicamento,
    indicar la cantidad y el subtotal, y guardar.
6.  Verificar que el stock del lote se haya actualizado.
7.  Consultar o descargar el reporte de inventario en Excel o PDF.

Para probar el control de existencias, puede crear un lote con 50
unidades y registrar un detalle de venta por 3 unidades. El stock
esperado después de guardar el detalle es de 47 unidades.

## Control de versiones y colaboración

Repositorio GitHub:

https://github.com/jeissonsaenz1/proyecto-integrador-g5

Para consultar el estado de los cambios:

``` powershell
git status
```

Para registrar cambios:

``` powershell
git add .
git commit -m "Descripción breve de los cambios"
git push
```

Cada integrante debe configurar su identidad de Git antes de realizar
commits:

``` powershell
git config user.name "Nombre del integrante"
git config user.email "correo-configurado-en-GitHub"
```

No agregue al repositorio contraseñas, tokens, claves privadas ni
archivos locales que contengan credenciales.

## Estado y alcance

Este README documenta la estructura y las funcionalidades identificadas
en el proyecto. La ejecución correcta depende de configurar una base de
datos PostgreSQL accesible y de mantener las dependencias y versiones
indicadas en el `pom.xml`.
