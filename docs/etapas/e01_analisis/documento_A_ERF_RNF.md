# Documento A — Especificación de Requerimientos Funcionales (ERF)

> **Criterios aplicados a cada RF:**
> 1. Representa una **capacidad** del sistema, no un atributo.
> 2. **No** contiene reglas de negocio (van al Documento B — Reglas de Negocio).
> 3. **No** contiene detalles de implementación (van al Documento D — Modelo de Dominio, o a notas de diseño aparte).
> 4. Es **verificable** (se puede escribir un criterio de aceptación tipo test).
> 5. Sirve como fuente directa para el Documento C — Casos de Uso.
>
> Este documento **reemplaza** `requerimientos_funcionales.md`. Cambios respecto a esa versión: se separó Cancelación de Ventas como RF propio (mejor verificabilidad), se eliminó "Consultas" como RF independiente (redundante con el "consultar" de cada gestión), se mantiene Auditoría como RF explícito.

---

## RF01. Autenticación

El sistema deberá permitir la autenticación de los trabajadores mediante credenciales de acceso.

Como mínimo deberá permitir:
- Iniciar sesión.
- Cerrar sesión.
- Identificar el rol del usuario autenticado.
- Controlar el acceso a las funcionalidades según dicho rol.

**Criterio de verificación:** un trabajador con credenciales válidas puede iniciar sesión y accede únicamente a las funciones permitidas por su rol; con credenciales inválidas, el acceso es rechazado.

---

## RF02. Gestión de trabajadores

El sistema deberá permitir administrar la información de los trabajadores.

Como mínimo deberá permitir:
- Registrar, modificar, consultar y desactivar trabajadores (con filtros de búsqueda).
- Registrar el horario asignado a cada trabajador.
- Consultar el historial de sesiones de un trabajador.
- Identificar al trabajador responsable de cada venta registrada.

**Criterio de verificación:** un administrador puede dar de alta un trabajador, asignarle horario, y luego consultar cuándo inició/cerró sesión efectivamente.

---

## RF03. Gestión de productos

El sistema deberá permitir administrar los productos comercializados por la licorería.

Como mínimo deberá permitir:
- Registrar, modificar, consultar y desactivar productos (con filtros de búsqueda).
- Clasificar cada producto en una categoría (cerveza, gaseosa, cigarrillo, refresco, trago, combo).

**Criterio de verificación:** un administrador puede crear un producto de cualquier categoría, editarlo, consultarlo por filtro de categoría o nombre, y desactivarlo sin eliminarlo del historial.

---

## RF04. Gestión de cigarrillos

El sistema deberá permitir administrar la venta de cigarrillos.

Como mínimo deberá permitir:
- Vender cajetillas completas.
- Vender cigarrillos por unidad.
- Registrar y mantener el stock de ambas modalidades de venta de forma diferenciada.

**Criterio de verificación:** el sistema permite vender una cajetilla completa o unidades sueltas, y refleja correctamente la disponibilidad de cada modalidad tras la venta.

---

## RF05. Gestión de combos

El sistema deberá permitir administrar los combos comercializados por la empresa.

Como mínimo deberá permitir:
- Registrar, modificar, consultar y desactivar combos.
- Asociar los productos que conforman cada combo.
- Configurar un descuento asociado al combo.

**Criterio de verificación:** un administrador puede crear un combo con sus componentes y un descuento, y el sistema lo ofrece como producto vendible.

---

## RF06. Registro de ventas

El sistema deberá permitir registrar ventas.

Como mínimo deberá permitir:
- Registrar una venta con uno o varios productos.
- Registrar, para cada venta: trabajador responsable, fecha y hora, productos, cantidades, precio aplicado y total.
- Verificar disponibilidad de stock antes de confirmar la venta.
- Actualizar automáticamente el inventario al confirmarse la venta.

**Criterio de verificación:** al registrar una venta con productos disponibles, el sistema calcula el total correctamente, descuenta el stock, y asocia la venta al trabajador y momento en que ocurrió; si algún producto no tiene stock suficiente, la venta no se confirma.

---

## RF07. Cancelación de ventas

El sistema deberá permitir cancelar una venta ya registrada.

Como mínimo deberá permitir:
- Cancelar una venta en curso (antes de confirmar el pago).
- Cancelar una venta ya confirmada.
- Conservar el registro de la venta cancelada (no eliminarla).

**Criterio de verificación:** al cancelar una venta ya confirmada, el inventario se restituye y la venta queda visible en el historial con estado "cancelada".

---

## RF08. Gestión de pagos

El sistema deberá permitir registrar pagos utilizando los siguientes métodos:
- Efectivo (con cálculo de cambio).
- Código QR.

Para pagos mediante QR, el sistema deberá generar el código correspondiente y gestionar la confirmación de la transacción antes de cerrar la venta.

**Criterio de verificación:** un pago en efectivo calcula correctamente el cambio; un pago QR no cierra la venta hasta que la transacción sea confirmada.

---

## RF09. Gestión de inventario

El sistema deberá permitir administrar el inventario de productos.

Como mínimo deberá permitir:
- Consultar existencias actuales, con filtros de búsqueda.
- Consultar productos con bajo stock.
- Consultar el historial de movimientos de inventario.

**Criterio de verificación:** el sistema muestra la cantidad disponible de cualquier producto y permite filtrar productos por debajo de un umbral de stock.

---

## RF10. Gestión de compras y abastecimiento

El sistema deberá permitir registrar el abastecimiento de productos provenientes de proveedores.

Como mínimo deberá permitir:
- Consultar qué proveedores suministran un producto determinado.
- Registrar una compra, identificando: proveedor, productos recibidos y cantidades.
- Actualizar automáticamente el inventario al confirmar la compra.

**Criterio de verificación:** al registrar una compra, el stock del/los producto(s) recibidos aumenta según la cantidad indicada, y queda asociado al proveedor correspondiente.

---

## RF11. Gestión de proveedores

El sistema deberá permitir administrar los proveedores.

Como mínimo deberá permitir:
- Registrar, modificar, consultar y desactivar proveedores (con filtros de búsqueda).
- Asociar productos con uno o más proveedores.

**Criterio de verificación:** un administrador puede registrar un proveedor, asociarlo a uno o más productos, y consultar qué proveedores existen para un producto dado.

---

## RF12. Reportes

El sistema deberá permitir generar reportes de información del negocio.

Como mínimo deberá generar reportes sobre:
- Productos vendidos y cantidad vendida por producto.
- Ventas y monto total vendido por trabajador.
- Ventas por fecha o rango de fechas.
- Estado consolidado del inventario.

**Criterio de verificación:** un administrador puede obtener, para un rango de fechas dado, el total vendido por cada trabajador y los productos más vendidos.

---

## RF13. Configuración del sistema

El sistema deberá permitir administrar los parámetros generales necesarios para su funcionamiento.

Como mínimo deberá permitir configurar:
- El porcentaje por defecto de incremento de precio para cigarrillos vendidos por unidad.
- El umbral de bajo stock.
- Los datos generales del negocio.

**Criterio de verificación:** un administrador puede modificar un parámetro de configuración, y el nuevo valor se usa como valor por defecto en los registros posteriores (sin afectar retroactivamente los ya existentes).

---

## RF14. Auditoría

El sistema deberá permitir registrar y consultar un historial de operaciones críticas.

Como mínimo deberá registrar y permitir consultar:
- Ventas, cancelaciones, compras.
- Cambios en productos, precios y proveedores.
- Inicios y cierres de sesión.

Cada registro deberá indicar qué ocurrió, cuándo y quién fue responsable.

**Criterio de verificación:** un administrador puede consultar, para un trabajador y rango de fechas dado, todas las operaciones críticas que realizó.

---

## RF15. Gestión de clientes (opcional)

El sistema deberá permitir identificar opcionalmente al cliente de una venta.

Como mínimo deberá permitir:
- Registrar datos básicos de un cliente (nombre, contacto) al momento de la venta, o completar la venta sin identificarlo.
- Consultar el historial de compras de un cliente previamente identificado.

**Fuera de alcance de este RF:** facturación, venta a crédito y programas de fidelización — se documentan como posibles extensiones futuras, no implementadas.

**Criterio de verificación:** una venta puede registrarse con o sin cliente asociado; si se asocia uno, sus compras anteriores quedan disponibles para consulta.

---

# Documento A.1 — Requerimientos No Funcionales (RNF)

> Los RNF no son capacidades — son **atributos de calidad** que restringen *cómo* se cumplen los RF. Se verifican en distintos momentos: al escribir casos de uso (seguridad/permisos), al diseñar el dominio (integridad, auditoría, concurrencia), y durante implementación/pruebas (rendimiento, disponibilidad).

## RNF01. Usabilidad
El sistema debe ser fácil de aprender y utilizar por trabajadores con poca capacitación previa.

## RNF02. Rendimiento
El registro de una venta no debe superar los 3 segundos en condiciones normales de operación.

## RNF03. Disponibilidad
El sistema debe estar disponible durante todo el horario de atención de la licorería.

## RNF04. Seguridad
Cada trabajador debe autenticarse con usuario y contraseña. Solo el Administrador puede modificar productos, precios y proveedores (ver RF01, RN10).

## RNF05. Integridad de datos
El sistema no debe permitir vender un producto sin stock disponible. Toda venta debe quedar registrada de forma permanente, incluso si se cancela.

## RNF06. Confiabilidad
La confirmación del pago QR debe verificarse explícitamente antes de cerrar la venta (ver RF08).

## RNF07. Escalabilidad
El sistema debe permitir agregar nuevas categorías de productos, nuevos métodos de pago y, eventualmente, nuevas sucursales, sin modificar la estructura principal del sistema.

## RNF08. Mantenibilidad
El software debe permitir actualizar precios, proveedores y productos de manera sencilla, sin requerir cambios de código para ajustes de valores (ver RF13).

## RNF09. Compatibilidad
El sistema debe funcionar correctamente en computadoras con sistema operativo Windows.

## RNF10. Respaldo de información
El sistema debe permitir realizar copias de seguridad de la base de datos y restaurarlas cuando sea necesario.

## RNF11. Integridad de la auditoría
Todo registro de auditoría (RF14) debe ser inmutable una vez creado: no debe poder editarse ni eliminarse por ningún rol, incluyendo el Administrador.

## RNF12. Concurrencia
El sistema debe manejar correctamente operaciones simultáneas sobre el mismo producto (ej. dos trabajadores vendiendo el último ítem disponible al mismo tiempo), revalidando el stock en el momento de confirmar la venta y no solo al agregarla.

## RNF13. Resiliencia de integraciones externas
Si la pasarela de pago QR no responde o falla, el sistema debe seguir permitiendo ventas por el método de pago en efectivo sin interrupción del servicio.

---

## Nota de diseño (no forma parte de este documento, es insumo para B y D)

Los siguientes puntos discutidos **no son RF ni RNF** — son reglas de negocio o decisiones de modelo de dominio, y se incorporarán en los Documentos B y D:

- Mecánica de "abrir cajetilla" (transferencia de stock cajetilla → unidad suelta) — Documento D.
- Fórmula de precio del cigarrillo por unidad — Documento B (ya definida como RN01/RN02).
- Contenido del código QR (referencia de venta + monto, sin integración bancaria real) — Documento D.
- Extensión futura de descuentos por volumen (no implementada, solo interfaz abierta) — Documento D.
- Cálculo de precio sugerido por costo de adquisición (extensión futura, manual por ahora) — Documento D.
