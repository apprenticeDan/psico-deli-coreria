# Documento C — Casos de Uso

> Reescrito usando **únicamente** el Documento A (RF) y el Documento B (RN). Cada caso de uso referencia las reglas de negocio por número (`Ver RNxx`) en vez de repetir su texto — el contenido completo de cada regla vive solo en el Documento B, para evitar duplicación y desincronización entre documentos.
> Reemplaza a `casos_de_uso_licoreria.md`.

## Actores

| Actor | Descripción |
|---|---|
| **Trabajador** | Rol base: opera el punto de venta (vende, cobra, consulta inventario). |
| **Administrador** | Trabajador con permisos ampliados: gestiona productos, precios, proveedores, otros trabajadores, configuración y reportes. |
| **Pasarela QR** | Actor externo que genera y confirma el pago mediante código QR. |

---

## RF01 — Autenticación

### UC01. Iniciar sesión
- **Actor:** Trabajador, Administrador
- **Precondición:** El usuario tiene una cuenta creada.
- **Flujo principal:**
  1. El usuario ingresa usuario y contraseña.
  2. El sistema valida las credenciales.
  3. El sistema identifica el rol del usuario y habilita las funciones correspondientes.
  4. El sistema registra la hora de inicio de sesión.
- **Flujo alternativo:** 2a. Credenciales inválidas → el sistema rechaza el acceso.
- **Postcondición:** Sesión activa.
- **Reglas de negocio:** RN10.

### UC02. Cerrar sesión
- **Actor:** Trabajador, Administrador
- **Precondición:** Sesión activa.
- **Flujo principal:**
  1. El usuario solicita cerrar sesión.
  2. El sistema finaliza la sesión y registra la hora de cierre.
- **Postcondición:** El usuario debe volver a autenticarse para operar el sistema.

---

## RF02 — Gestión de trabajadores

### UC03. Gestionar trabajadores
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador.
- **Flujo principal:**
  1. El administrador registra un trabajador (nombre, credenciales, rol, horario asignado).
  2. El sistema guarda la información y habilita el acceso.
- **Flujos alternativos:**
  - Modificar datos de un trabajador existente.
  - Desactivar un trabajador (se conserva el historial asociado).
  - Consultar trabajadores con filtros de búsqueda.
- **Postcondición:** Trabajador registrado/actualizado.
- **Reglas de negocio:** RN09, RN10, RN14.

### UC04. Consultar historial de sesiones
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; existen sesiones registradas (UC01/UC02).
- **Flujo principal:**
  1. El administrador selecciona un trabajador y, opcionalmente, un rango de fechas.
  2. El sistema muestra el historial: fecha, hora de inicio, hora de cierre.
- **Postcondición:** El administrador puede comparar el horario asignado contra las sesiones reales.
- **Reglas de negocio:** RN14.

---

## RF03 — Gestión de productos

### UC05. Gestionar producto
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador.
- **Flujo principal:**
  1. El administrador selecciona la categoría del producto (cerveza, gaseosa, cigarrillo, refresco, trago, combo).
  2. Ingresa los datos base: nombre, precio, proveedor(es) asociado(s), stock inicial.
  3. Si la categoría es cigarrillo → continúa en UC06. Si es combo → continúa en UC08.
  4. El sistema guarda el producto.
- **Flujos alternativos:**
  - Modificar un producto existente.
  - Desactivar un producto (se conserva en el historial).
  - Consultar productos con filtros de búsqueda por categoría o nombre.
- **Postcondición:** Producto disponible para la venta.
- **Reglas de negocio:** RN09, RN11.

---

## RF04 — Gestión de cigarrillos

### UC06. Registrar cigarrillo
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; se está registrando un producto de categoría cigarrillo (continuación de UC05).
- **Flujo principal:**
  1. El administrador ingresa: precio de la cajetilla, unidades por cajetilla, y porcentaje de incremento (o usa el valor por defecto).
  2. El sistema calcula el precio de venta por unidad.
  3. El sistema guarda el producto con ambas modalidades de venta habilitadas.
- **Postcondición:** Cigarrillo disponible para la venta, tanto por cajetilla como por unidad.
- **Reglas de negocio:** RN01, RN02, RN03, RN09.

### UC07. Registrar apertura de cajetilla
- **Actor:** Trabajador, Administrador
- **Precondición:** Sesión activa; existe stock de cajetillas cerradas del producto.
- **Contexto:** Puede iniciarse de forma independiente (gestión de inventario) o **en línea, dentro de una venta en curso** (ver UC09, flujo 2b), cuando faltan unidades sueltas de un producto específico.
- **Flujo principal:**
  1. El usuario indica qué cigarrillo y cuántas cajetillas desea abrir.
  2. El sistema descuenta la cantidad indicada del stock de cajetillas cerradas.
  3. El sistema incrementa el stock de unidades sueltas en la cantidad correspondiente.
- **Flujo alternativo:** 2a. No hay cajetillas suficientes para abrir → el sistema rechaza la operación.
- **Postcondición:** El stock de unidades sueltas queda disponible para la venta por unidad.
- **Reglas de negocio:** RN13, RN17.

---

## RF05 — Gestión de combos

### UC08. Gestionar combo
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; existen productos de las categorías gaseosa y trago, y stock de hielo.
- **Flujo principal:**
  1. El administrador selecciona los productos que componen el combo (gaseosa + hielo + trago).
  2. Define el precio del combo y el descuento asociado (valor por defecto: 0).
  3. El sistema guarda el combo como producto vendible.
- **Flujos alternativos:**
  - Modificar componentes o descuento de un combo existente.
  - Desactivar un combo.
- **Postcondición:** Combo disponible para la venta.
- **Reglas de negocio:** RN04, RN09.

---

## RF06 — Registro de ventas

### UC09. Registrar venta
- **Actor:** Trabajador
- **Precondición:** Sesión activa; existen productos con stock disponible.
- **Flujo principal:**
  1. El trabajador selecciona uno o varios productos y sus cantidades (indicando, para cigarrillos, si es por cajetilla o por unidad).
  2. El sistema verifica el stock disponible de cada producto seleccionado.
  3. El sistema calcula el subtotal por ítem (aplicando descuento si corresponde) y el total de la venta.
  4. El trabajador selecciona el método de pago → continúa en UC11 (Efectivo) o UC12 (QR).
  5. Al confirmarse el pago, el sistema registra la venta con: trabajador, fecha y hora, productos, cantidades, precio aplicado y total.
  6. El sistema actualiza el inventario descontando lo vendido.
- **Flujos alternativos:**
  - 2a. Stock insuficiente en algún ítem → el sistema impide agregarlo.
  - 2b. Si el ítem es cigarrillo por unidad y no hay unidades sueltas suficientes pero sí cajetillas cerradas del mismo producto → el sistema ofrece abrir una cajetilla (ver UC07) dentro del mismo flujo, sin salir de la venta.
  - 3a. El trabajador puede, opcionalmente, identificar al cliente (nuevo o ya registrado) — ver UC20. La venta se completa igual si no se identifica.
  - 4a. El trabajador cancela la venta antes de confirmar el pago → ver UC10.
- **Postcondición:** Venta registrada, inventario actualizado.
- **Reglas de negocio:** RN04, RN05, RN13, RN17, RN19.

---

## RF07 — Cancelación de ventas

### UC10. Cancelar venta
- **Actor:** Trabajador
- **Precondición:** Existe una venta en curso o recién confirmada.
- **Flujo principal:**
  1. El usuario solicita cancelar la venta.
  2. Si el pago aún no fue confirmado, el sistema descarta la venta sin afectar el inventario.
  3. Si el pago ya fue confirmado, el sistema restituye el stock correspondiente y marca la venta como cancelada.
- **Postcondición:** Venta anulada; el inventario queda como antes de la venta si ya se había descontado. La venta permanece en el historial.
- **Reglas de negocio:** RN07, RN08, RN13, RN16.

---

## RF08 — Gestión de pagos

### UC11. Pagar en efectivo
- **Actor:** Trabajador
- **Precondición:** Venta en proceso con total calculado (continuación de UC09).
- **Flujo principal:**
  1. El trabajador selecciona "Efectivo".
  2. El trabajador ingresa el monto recibido.
  3. El sistema calcula el cambio a entregar.
  4. El sistema confirma el pago y continúa el paso 5 de UC09.
- **Flujo alternativo:** 2a. Monto recibido insuficiente → el sistema no permite confirmar el pago.
- **Postcondición:** Venta cerrada con pago en efectivo.
- **Reglas de negocio:** RN15.

### UC12. Pagar con QR
- **Actor:** Trabajador, Pasarela QR
- **Precondición:** Venta en proceso con total calculado (continuación de UC09).
- **Flujo principal:**
  1. El trabajador selecciona "QR".
  2. El sistema genera un código QR asociado a la venta y su monto.
  3. El cliente paga escaneando el código.
  4. La pasarela QR notifica al sistema la confirmación del pago.
  5. El sistema confirma la transacción y continúa el paso 5 de UC09.
- **Flujos alternativos:**
  - 4a. La pasarela no confirma dentro de un tiempo determinado → el sistema no cierra la venta.
  - 4b. La pasarela reporta un pago rechazado → el sistema notifica al trabajador y no cierra la venta.
- **Postcondición:** Venta cerrada solo si el pago fue confirmado.
- **Reglas de negocio:** RN06.

---

## RF09 — Gestión de inventario

### UC13. Consultar inventario
- **Actor:** Trabajador, Administrador
- **Precondición:** Sesión activa.
- **Flujo principal:**
  1. El usuario accede al módulo de inventario.
  2. El sistema muestra la cantidad disponible de cada producto, con filtros de búsqueda.
- **Flujo alternativo:** Filtrar por productos con bajo stock (umbral configurable, ver RF13).
- **Postcondición:** El usuario visualiza el estado actual del inventario.

### UC14. Consultar historial de movimientos de inventario
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador.
- **Flujo principal:**
  1. El administrador accede al historial de movimientos.
  2. El sistema lista entradas y salidas, con fecha, cantidad, producto y responsable.
- **Postcondición:** El administrador puede revisar todo movimiento de stock.
- **Reglas de negocio:** RN13.

---

## RF10 — Gestión de compras y abastecimiento

### UC15. Registrar compra
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; proveedor registrado.
- **Flujo principal:**
  1. El administrador consulta qué proveedores suministran un producto (útil ante bajo stock o stock en cero), comparando el precio y la cantidad ofertada por cada uno.
  2. Selecciona un proveedor y registra los productos y cantidades recibidas.
  3. El sistema actualiza automáticamente el inventario.
- **Postcondición:** Inventario actualizado con la nueva mercadería.
- **Reglas de negocio:** RN12, RN13.

---

## RF11 — Gestión de proveedores

### UC16. Gestionar proveedores
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador.
- **Flujo principal:**
  1. El administrador registra un proveedor (nombre, contacto).
  2. Asocia uno o varios productos que dicho proveedor puede suministrar.
- **Flujos alternativos:**
  - Modificar o desactivar un proveedor existente.
  - Consultar proveedores con filtros de búsqueda.
- **Postcondición:** Proveedor disponible para ser seleccionado al registrar una compra (UC15).
- **Reglas de negocio:** RN09, RN11.

---

## RF12 — Reportes

### UC17. Generar reporte
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; existen ventas registradas.
- **Flujo principal:**
  1. El administrador selecciona el tipo de reporte: por producto, por trabajador, o por fecha.
  2. El sistema procesa la información y genera el reporte.
  3. El sistema muestra el reporte (y opcionalmente permite exportarlo).
- **Postcondición:** El administrador obtiene información consolidada para la toma de decisiones.

---

## RF13 — Configuración del sistema

### UC18. Configurar parámetros del sistema
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador.
- **Flujo principal:**
  1. El administrador accede al módulo de configuración.
  2. Edita parámetros: porcentaje por defecto de incremento de cigarrillos, umbral de bajo stock, datos del negocio.
  3. El sistema guarda los cambios.
- **Postcondición:** Los nuevos valores se usan como valor por defecto en registros posteriores, sin afectar retroactivamente los existentes.
- **Reglas de negocio:** RN03.

---

## RF14 — Auditoría

### UC19. Consultar auditoría
- **Actor:** Administrador
- **Precondición:** Sesión activa como Administrador; existen operaciones críticas registradas.
- **Flujo principal:**
  1. El administrador accede al módulo de auditoría.
  2. Filtra por tipo de operación, trabajador y/o rango de fechas.
  3. El sistema muestra qué ocurrió, cuándo y quién fue responsable.
- **Postcondición:** El administrador obtiene trazabilidad completa de operaciones sensibles.
- **Reglas de negocio:** RN16.

---

## RF15 — Gestión de clientes (opcional)

### UC20. Identificar cliente en la venta
- **Actor:** Trabajador
- **Precondición:** Venta en curso (continuación de UC09, flujo 3a).
- **Flujo principal:**
  1. El trabajador busca al cliente por nombre o contacto.
  2. Si existe, lo asocia a la venta. Si no existe, lo registra con nombre y/o contacto y lo asocia.
- **Flujo alternativo:** El trabajador omite este paso — la venta continúa sin cliente asociado.
- **Postcondición:** La venta queda (opcionalmente) vinculada a un cliente.
- **Reglas de negocio:** RN19, RN20.

### UC21. Consultar historial de compras de un cliente
- **Actor:** Trabajador, Administrador
- **Precondición:** Sesión activa; el cliente fue identificado en ventas anteriores.
- **Flujo principal:**
  1. El usuario busca un cliente.
  2. El sistema muestra las ventas previamente asociadas a ese cliente.
- **Postcondición:** El usuario visualiza el historial de compras del cliente.
- **Reglas de negocio:** RN20.

---

## Trazabilidad

La matriz completa RF ↔ UC ↔ RN ↔ Agregado vive en el **Documento E — Matrices de Trazabilidad**, para evitar mantener la misma información en dos archivos. Resumen rápido: los 14 RF del Documento A tienen al menos un caso de uso en este documento; ningún caso de uso existe sin un RF que lo respalde.
