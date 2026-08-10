# Documento F — Especificación de la API Funcional del Dominio

**Proyecto:** Sistema para Tienda de Licorería  
**Etapa:** 02 — Especificación del dominio  
**Versión:** F-1.1  
**Fecha:** 2026-08-09  
**Estado:** Baseline para diseño funcional y TDD

**Cambios en esta versión (F-1.1):** sección 7 (Combos) reestructurada — composición genérica vía `ComponenteCombo[]` en vez de campos fijos (`gaseosaId`/`tragoId`/`hieloId`); se agregan `calcularStockDisponibleCombo` y `consumoDeComponentes` (stock derivado de los componentes, sin stock físico propio del combo); se documenta RN-COMBO-01 (= RN22 en el Documento B); se agrega `desactivarCombo` y la extensión futura descartada de preparación física de combos.

## 0. Propósito y alcance

Este documento especifica la **API funcional del dominio** del sistema. Define los tipos de datos del dominio, entidades, agregados, funciones, contratos, resultados y errores necesarios para implementar las capacidades identificadas en los documentos de la Etapa 1.

No define endpoints HTTP, controladores Spring Boot, repositorios JPA, tablas, DTOs de infraestructura ni detalles de despliegue.

La API descrita aquí debe poder implementarse y probarse independientemente de Spring Boot y de la infraestructura.

La especificación se deriva de los Documentos A–E y del `docB_etapa02_overview.md` proporcionado como panorama previo de Etapa 2. Cuando una decisión depende de una regla provisional, se marca explícitamente.

---

# 1. Principios de diseño funcional

## 1.1. Prioridad de funciones puras

Siempre que una operación pueda expresarse como transformación determinista de datos, se especificará como función pura.

```text
calcularTotal(venta) -> Result<Dinero, ErrorDominio>
```

No debe consultar base de datos, reloj, sesión HTTP ni servicios externos.

## 1.2. Inmutabilidad

Los Value Objects son inmutables. Las entidades y agregados se tratarán preferentemente como valores que producen una nueva versión mediante funciones de transformación.

## 1.3. Separación entre dominio y efectos

Las funciones de dominio no realizan directamente persistencia, llamadas HTTP, acceso a Spring, cifrado concreto, generación gráfica de QR ni lectura del reloj del sistema.

## 1.4. Errores como resultados

Los errores previsibles del negocio deben formar parte del contrato:

```text
Result<T, ErrorDominio>
```

No se prescribe una biblioteca concreta para `Result`.

## 1.5. Identificadores

Las entidades poseen identificadores propios. Las referencias entre agregados se expresan mediante identificadores.

## 1.6. Funciones y métodos

Este documento especifica comportamiento, no obliga a que cada función termine como método de una entidad. Podrá implementarse como función pura, método de agregado, servicio de dominio puro, transformación o consulta.

---

# 2. Convenciones de contratos

Cada operación se describe mediante entrada, salida, precondiciones, postcondiciones, reglas, pureza, errores y tests.

Convención:

```text
Result<T, E>
    Ok(T)
    Error(E)
```

---

# 3. Tipos abstractos del dominio

## 3.1. Identificadores

```text
ProductoId
VentaId
TrabajadorId
ProveedorId
CompraId
ClienteId
SesionId
MovimientoInventarioId
AuditoriaId
```

Un identificador válido no es vacío. Construcción pura.

## 3.2. Dinero

```text
crearDinero(valor) -> Result<Dinero, ErrorDominio>
sumar(a, b) -> Dinero
restar(a, b) -> Result<Dinero, ErrorDominio>
multiplicar(dinero, cantidad) -> Result<Dinero, ErrorDominio>
aplicarPorcentaje(dinero, porcentaje) -> Dinero
esMayor(a, b) -> Boolean
esMayorOIgual(a, b) -> Boolean
esIgual(a, b) -> Boolean
```

Debe definirse en implementación la política de precisión y redondeo monetario y cubrirla con tests.

## 3.3. Cantidad

```text
crearCantidad(valor) -> Result<Cantidad, ErrorDominio>
sumar(a, b) -> Cantidad
restar(a, b) -> Result<Cantidad, ErrorDominio>
esSuficiente(disponible, requerida) -> Boolean
esCero(cantidad) -> Boolean
```

Invariante: `cantidad >= 0`.

## 3.4. Porcentaje

```text
crearPorcentaje(valor) -> Result<Porcentaje, ErrorDominio>
aplicarSobre(porcentaje, dinero) -> Dinero
```

Invariante: `0 <= porcentaje <= 100`.

## 3.5. Descuento

```text
crearDescuento(porcentaje) -> Result<Descuento, ErrorDominio>
aplicarSobre(descuento, precio) -> Dinero
```

Semánticamente distinto de un porcentaje genérico.

## 3.6. Enumeraciones y tipos suma

```text
CategoriaProducto = CERVEZA | GASEOSA | CIGARRILLO | REFRESCO | TRAGO | COMBO
Rol = VENDEDOR | ADMINISTRADOR
EstadoProducto = ACTIVO | INACTIVO
EstadoVenta = EN_PROCESO | CONFIRMADA | CANCELADA
MetodoPago = EFECTIVO | QR
EstadoPago = PENDIENTE | CONFIRMADO | RECHAZADO | EXPIRADO
TipoMovimientoInventario = COMPRA | VENTA | CANCELACION | APERTURA_CAJETILLA
```

Transiciones válidas de venta:

```text
EN_PROCESO -> CONFIRMADA
EN_PROCESO -> CANCELADA
CONFIRMADA -> CANCELADA
```

No se permite reactivar una venta cancelada.

## 3.7. HorarioAsignado

Representa el horario previsto de un trabajador. Es informativo: no bloquea autenticación ni ventas.

## 3.8. DatosContacto

Objeto reutilizable con nombre y contacto, opcionales según el agregado.

## 3.9. Credencial

Representa `usuario + passwordHash`. La contraseña en texto plano no forma parte del estado persistente. La generación del hash concreto pertenece a infraestructura.

```text
verificarCredencial(intentoPlano, passwordHash) -> Boolean
```

---

# 4. ErrorDominio

Familia extensible de errores:

```text
ValorInvalido
CantidadInvalida
DineroInvalido
PorcentajeInvalido
ProductoNoEncontrado
ProductoInactivo
ProductoSinStock
StockInsuficiente
CigarrilloInvalido
CajetillaInsuficiente
PrecioInvalido
ComboInvalido
ComponenteComboInvalido
ComboSinStockSuficiente
VentaNoEncontrada
VentaNoCancelable
VentaYaConfirmada
VentaYaCancelada
VentaSinDetalles
PagoNoConfirmado
MontoRecibidoInsuficiente
TrabajadorNoEncontrado
TrabajadorInactivo
CredencialesInvalidas
OperacionNoAutorizada
ProveedorNoEncontrado
CompraInvalida
ClienteNoEncontrado
EstadoInvalido
ConfiguracionInvalida
```

La lista podrá ampliarse conforme se especifiquen funciones concretas.

---

# 5. Producto y catálogo

## 5.1. Producto genérico

Datos conceptuales:

```text
Producto
    id
    nombre
    categoria
    precio
    estado
    stock
    proveedores[]
    descuento?
```

Operaciones:

```text
crearProducto(datos) -> Result<Producto, ErrorDominio>
modificarProducto(producto, cambios) -> Result<Producto, ErrorDominio>
activarProducto(producto) -> Result<Producto, ErrorDominio>
desactivarProducto(producto) -> Result<Producto, ErrorDominio>
cambiarPrecio(producto, nuevoPrecio) -> Result<Producto, ErrorDominio>
configurarDescuento(producto, descuento) -> Result<Producto, ErrorDominio>
obtenerPrecioVenta(producto) -> Result<Dinero, ErrorDominio>
```

RN04 y RN09. Las transformaciones de estado son puras.

---

# 6. Cigarrillos

El cigarrillo mantiene dos existencias diferenciadas:

```text
stockCajetillas
stockUnidadesSueltas
```

Además:

```text
precioCajetilla
unidadesPorCajetilla
porcentajeIncremento
```

## 6.1. Crear cigarrillo

```text
crearCigarrillo(
    datosBase,
    precioCajetilla,
    unidadesPorCajetilla,
    porcentajeIncremento
) -> Result<Cigarrillo, ErrorDominio>
```

Precondiciones: precio positivo, unidades positivas e incremento válido.

## 6.2. Calcular precio unitario

```text
calcularPrecioUnitarioCigarrillo(
    precioCajetilla,
    unidadesPorCajetilla,
    incremento
) -> Result<Dinero, ErrorDominio>
```

RN01–RN03. Debe garantizar RN02:

```text
precioUnitario * unidadesPorCajetilla > precioCajetilla
```

## 6.3. Abrir cajetilla

```text
abrirCajetilla(
    cigarrillo,
    cantidadCajetillas
) -> Result<Cigarrillo, ErrorDominio>
```

Transforma:

```text
stockCajetillas -= cantidadCajetillas
stockUnidadesSueltas += cantidadCajetillas * unidadesPorCajetilla
```

Ejemplo: `35 cajetillas, 0 sueltos -> 34 cajetillas, 20 sueltos` al abrir una cajetilla de 20.

RN17. La operación no crea ni destruye unidades.

## 6.4. Disponibilidad

```text
puedeVenderCajetillas(cigarrillo, cantidad) -> Boolean
puedeVenderUnidades(cigarrillo, cantidad) -> Boolean
```

Si faltan unidades sueltas pero existen cajetillas cerradas, el flujo de venta puede solicitar `abrirCajetilla` dentro del mismo proceso.

---

# 7. Combos

Un Combo es un Producto compuesto destinado a una oferta comercial. Está formado por uno o más productos componentes y posee un precio de venta propio, independiente de la suma de sus componentes.

Ante la Venta, el Combo se comporta como un Producto vendible: aparece como **una única línea** en el `DetalleVenta` — el trabajador/cliente no ve los componentes como líneas separadas.

**El Combo no posee stock físico independiente en el alcance actual.** Su disponibilidad se determina a partir del stock disponible de sus productos componentes (ver 7.4). No se introducen todavía los conceptos de stock comprometido, stock reservado para promociones, ni operaciones de armar/desarmar combo — no son necesarios para el alcance actual (ver 7.6 para la extensión futura deliberadamente descartada).

Datos conceptuales:

```text
Combo
    productoId
    nombre
    componentes[]      -- lista de ComponenteCombo
    precio              -- independiente del precio de los componentes
    descuento
    estado               -- ACTIVO | INACTIVO
```

## 7.0. ComponenteCombo

Representa la relación entre un Combo y uno de los productos que lo componen. Reemplaza la estructura fija anterior (`gaseosaId`, `tragoId`, `hieloId`), que no permitía variar la composición de un combo a otro.

```text
ComponenteCombo
    producto   -- ProductoId
    cantidad   -- Cantidad
```

```text
Combo Carnavalero
 ├── Pepsi 2L  × 1
 ├── Lupita 1L × 1
 └── Hielo 1Kg × 1
```

Distintos combos pueden tener distintas composiciones y distintos precios comerciales de forma completamente independiente entre sí (ej. "Combo Carnavalero" a Bs 90 vs. "Combo Fiesta" con otras cantidades a Bs 125) — cada uno es una oferta independiente, y la oferta pertenece exclusivamente al Combo, nunca a sus productos componentes. Formar parte de un combo no modifica el precio individual de Pepsi, Lupita o Hielo.

## 7.1. Crear combo

```text
crearCombo(
    datosBase,
    componentes: List<ComponenteCombo>,
    precio,
    descuento
) -> Result<Combo, ErrorDominio>
```

Precondición: `componentes` no vacío; cada `ComponenteCombo.cantidad > 0`; no se exige una composición fija (ej. "debe tener exactamente gaseosa+trago+hielo") — cualquier combinación de productos existentes y activos es válida.

## 7.2. Precio final

```text
calcularPrecioFinalCombo(precioBase, descuento) -> Result<Dinero, ErrorDominio>
```

El precio del combo **nunca se recalcula sumando los componentes** — es un dato propio del Combo (RN04). Stock y precio son conceptos independientes: uno se deriva de los componentes, el otro no.

## 7.3. Stock derivado del combo

```text
calcularStockDisponibleCombo(combo, inventario)
    -> Result<Cantidad, ErrorDominio>
```

Función pura, determinista: para cada componente, calcula `stockDisponibleDelComponente / cantidadRequeridaPorCombo`, y el resultado es el **mínimo** de esos valores (el componente más escaso limita cuántos combos se pueden vender).

```text
Ejemplo 1 — cantidades iguales:
    componentes: Pepsi×1, Lupita×1, Hielo×1
    inventario:  Pepsi=20, Lupita=10, Hielo=15
    resultado = min(20/1, 10/1, 15/1) = 10

Ejemplo 2 — cantidades distintas:
    componentes: Pepsi×2, Lupita×1, Hielo×1
    inventario:  Pepsi=20, Lupita=10, Hielo=15
    resultado = min(20/2, 10/1, 15/1) = min(10, 10, 15) = 10

Ejemplo 3 — un componente agotado:
    componentes: Pepsi×1, Lupita×1, Hielo×1
    inventario:  Pepsi=10, Lupita=0, Hielo=15
    resultado = min(10/1, 0/1, 15/1) = 0
```

```text
consumoDeComponentes(combo, cantidadVendida)
    -> Result<List<ProductoCantidad>, ErrorDominio>
```

Calcula **qué debe consumirse** de cada componente al vender `cantidadVendida` combos — no persiste nada, solo produce el dato. La aplicación se encarga después de convertir ese resultado en movimientos de inventario persistidos.

```text
ProductoCantidad
    producto   -- ProductoId
    cantidad   -- Cantidad
```

```text
Combo Carnavalero × 2
    consumoDeComponentes(...) ->
        [ Pepsi×2, Lupita×2, Hielo×2 ]
```

## 7.4. Regla de disponibilidad — RN-COMBO-01

Un Combo puede venderse únicamente cuando existe stock suficiente de **todos** sus componentes para satisfacer la cantidad solicitada. Se materializa comprobando `calcularStockDisponibleCombo(combo, inventario) >= cantidadSolicitada` antes de confirmar cualquier venta que incluya el combo — un combo nunca aparece como "disponible" si alguno de sus componentes está agotado, aunque los demás tengan existencia (ver Ejemplo 3 arriba). Se documenta también en el Documento B como RN22.

## 7.5. Desactivación de un combo

```text
desactivarCombo(combo) -> Result<Combo, ErrorDominio>
```

El combo pasa a `estado = INACTIVO` y deja de estar disponible para nuevas ventas. **Los productos componentes no se modifican ni desaparecen** — siguen siendo productos normales, vendibles individualmente fuera del combo.

## 7.6. Extensión futura deliberadamente descartada — preparación física de combos

El modelo actual no contempla stock físico independiente de combos. Si el negocio requiriera preparar y almacenar combos con anticipación (ej. armar 20 "Combo Carnavalero" antes de un evento), podría incorporarse una operación de transformación de inventario que convierta cantidades de productos componentes en unidades preparadas de un Combo — simétrica a `abrirCajetilla` en Cigarrillos:

```text
prepararCombo(combo, cantidad, inventario)
    componentes -> combos preparados
```

**Esta función no forma parte de la API funcional actual.** Se deja registrada como posibilidad de evolución, no como funcionalidad especulativa agregada al alcance — mismo criterio que las decisiones descartadas del Documento D (CQRS, bus de eventos, etc.): evaluada y explícitamente no implementada, no ignorada por desconocimiento.

---

# 8. Trabajadores, roles y sesión

## 8.1. Trabajador

```text
crearTrabajador(datosContacto, credencial, rol, horario)
    -> Result<Trabajador, ErrorDominio>

modificarTrabajador(trabajador, cambios)
    -> Result<Trabajador, ErrorDominio>

activarTrabajador(trabajador)
    -> Result<Trabajador, ErrorDominio>

desactivarTrabajador(trabajador)
    -> Result<Trabajador, ErrorDominio>
```

## 8.2. Autorización

```text
puedeEjecutar(rol, accion) -> Boolean
```

RN18: `ADMINISTRADOR` incluye los permisos de `VENDEDOR`.

Para reglas que puedan cambiar, la autorización puede recibirse como política en lugar de quedar fijada dentro de una función central.

## 8.3. Autenticación

```text
autenticar(usuario, intentoPlano, trabajadores)
    -> Result<Trabajador, ErrorDominio>
```

Debe rechazar credenciales inválidas y trabajadores inactivos. La obtención de trabajadores pertenece a aplicación/infraestructura.

## 8.4. Sesiones

```text
iniciarSesion(trabajador, fechaHora)
    -> Result<SesionIniciada, ErrorDominio>

cerrarSesion(sesion, fechaHora)
    -> Result<SesionFinalizada, ErrorDominio>

compararConHorario(horarioAsignado, sesiones)
    -> ResultadoSeguimientoHorario
```

RN14: el horario no bloquea operaciones.

---

# 9. Cliente

## 9.1. Crear y modificar

```text
crearCliente(nombre?, contacto?) -> Result<Cliente, ErrorDominio>
modificarCliente(cliente, cambios) -> Result<Cliente, ErrorDominio>
```

## 9.2. Asociar a venta

```text
asociarCliente(venta, cliente?) -> Result<Venta, ErrorDominio>
```

La ausencia de cliente es válida. RN19.

## 9.3. Historial

```text
consultarHistorialCliente(clienteId, ventas) -> List<VentaResumen>
```

RN20. No se introducen crédito, cuentas por cobrar, puntos ni fidelización.

---

# 10. Proveedores

```text
crearProveedor(datosContacto) -> Result<Proveedor, ErrorDominio>
modificarProveedor(proveedor, cambios) -> Result<Proveedor, ErrorDominio>
activarProveedor(proveedor) -> Result<Proveedor, ErrorDominio>
desactivarProveedor(proveedor) -> Result<Proveedor, ErrorDominio>
asociarProducto(proveedor, productoId) -> Result<Proveedor, ErrorDominio>
desasociarProducto(proveedor, productoId) -> Result<Proveedor, ErrorDominio>
```

RN11.

---

# 11. Venta

Datos conceptuales:

```text
Venta
    id
    trabajadorId
    clienteId?
    detalles[]
    pago
    estado
    fechaHora
```

## 11.1. Iniciar

```text
iniciarVenta(ventaId, trabajadorId, fechaHora)
    -> Result<Venta, ErrorDominio>
```

Estado inicial: `EN_PROCESO`.

## 11.2. Detalles

```text
agregarDetalle(venta, producto, modalidadVenta, cantidad)
    -> Result<Venta, ErrorDominio>

quitarDetalle(venta, detalleId)
    -> Result<Venta, ErrorDominio>

cambiarCantidadDetalle(venta, detalleId, nuevaCantidad)
    -> Result<Venta, ErrorDominio>
```

Modalidades conceptuales:

```text
NORMAL | CAJETILLA | UNIDAD | COMBO
```

Para cigarrillos, `CAJETILLA` y `UNIDAD` consumen existencias diferentes.

## 11.3. Cálculos

```text
calcularSubtotal(detalle) -> Result<Dinero, ErrorDominio>
calcularDescuentoDetalle(detalle) -> Dinero
calcularTotal(venta) -> Result<Dinero, ErrorDominio>
```

Son funciones puras respecto de sus entradas.

---

# 12. Stock y confirmación de venta

## 12.1. Verificación

```text
verificarStock(items, inventario)
    -> Result<StockVerificado, ErrorDominio>
```

RN05.

La disponibilidad se comprueba durante la preparación y vuelve a verificarse al confirmar.

## 12.2. Plan de consumo

```text
prepararConsumoStock(venta, inventario)
    -> Result<PlanConsumoStock, ErrorDominio>
```

Debe describir producto, cantidad, modalidad y origen del stock. Para cigarrillos por unidad puede incluir apertura de cajetilla.

## 12.3. Confirmar venta

```text
confirmarVenta(
    venta,
    pago,
    planConsumoStock
) -> Result<ConfirmacionVenta, ErrorDominio>
```

Precondiciones:

- venta en proceso;
- detalles válidos;
- pago confirmado;
- stock suficiente al momento de confirmar;
- autorización válida.

Postcondiciones:

- estado `CONFIRMADA`;
- plan de consumo aplicable;
- eventos de dominio generables.

RN05, RN06, RN08.

---

# 13. Pagos

## 13.1. Efectivo

```text
crearPagoEfectivo(total, montoRecibido)
    -> Result<PagoEfectivo, ErrorDominio>

calcularCambio(montoRecibido, total)
    -> Result<Dinero, ErrorDominio>
```

RN15: `montoRecibido >= total`.

## 13.2. QR

```text
prepararPagoQR(ventaId, total)
    -> Result<PagoQR, ErrorDominio>

confirmarPagoQR(pagoQR, confirmacion)
    -> Result<PagoQR, ErrorDominio>

expirarPagoQR(pagoQR)
    -> Result<PagoQR, ErrorDominio>
```

La generación gráfica del QR, espera, timeout y comunicación con la pasarela pertenecen a aplicación/infraestructura.

RN06.

---

# 14. Cancelación de venta

```text
cancelarVenta(
    venta,
    trabajador,
    politicaAutorizacion
) -> Result<CancelacionVenta, ErrorDominio>
```

Si `EN_PROCESO`, pasa a `CANCELADA` sin restitución de stock ya que todavía no se consumió.

Si `CONFIRMADA`, pasa a `CANCELADA` y genera restitución del stock correspondiente.

RN07 y RN08.

**RN21 es provisional:** actualmente se propone que cancelar una venta confirmada requiera Administrador. La función recibe la política para poder modificar esta decisión sin alterar el núcleo de cancelación.

---

# 15. Inventario

El stock no constituye un bounded context independiente. Se mantiene dentro del dominio de Producto y se coordina con Venta y Compra.

```text
aumentarStock(producto, cantidad) -> Result<Producto, ErrorDominio>
disminuirStock(producto, cantidad) -> Result<Producto, ErrorDominio>
restituirStock(producto, cantidad) -> Result<Producto, ErrorDominio>
```

Movimiento:

```text
crearMovimientoInventario(
    tipo,
    productoId,
    cantidad,
    responsableId,
    fechaHora,
    referencia
) -> MovimientoInventario
```

RN13. El registro es append-only.

Consultas:

```text
consultarInventario(productos) -> List<InventarioActual>
consultarBajoStock(productos, umbral) -> List<Producto>
consultarAgotados(productos) -> List<Producto>
consultarMovimientos(movimientos, filtro) -> List<MovimientoInventario>
```

Son funciones de lectura puras respecto de los datos recibidos.

---

# 16. Compras

```text
crearDetalleCompra(productoId, cantidad)
    -> Result<DetalleCompra, ErrorDominio>

registrarCompra(
    compraId,
    proveedorId,
    detalles,
    fechaHora,
    responsableId
) -> Result<CompraRegistrada, ErrorDominio>
```

RN12. La operación debe producir el plan de aumento de stock y los movimientos correspondientes.

---

# 17. Configuración

Datos mínimos:

```text
ConfiguracionSistema
    porcentajeIncrementoCigarrilloDefault
    umbralBajoStock
    datosDelNegocio
```

Funciones:

```text
crearConfiguracion(datos)
    -> Result<ConfiguracionSistema, ErrorDominio>

actualizarConfiguracion(configuracion, cambios)
    -> Result<ConfiguracionSistema, ErrorDominio>
```

Los valores son predeterminados. Cambiarlos no modifica retroactivamente valores explícitos ya almacenados en productos.

---

# 18. Auditoría

```text
crearRegistroAuditoria(
    fechaHora,
    tipoOperacion,
    responsableId,
    detalle,
    referencia?
) -> RegistroAuditoria

consultarAuditoria(registros, filtro)
    -> List<RegistroAuditoria>
```

Debe cubrir como mínimo operaciones críticas: venta, cancelación, cambio de producto/precio, compra, inicio/cierre de sesión y configuración.

El registro es append-only.

---

# 19. Reportes

Funciones de consulta:

```text
reporteVentasPorProducto(ventas, filtroPeriodo?)
    -> ReporteVentasPorProducto

reporteVentasPorTrabajador(ventas, filtroPeriodo?)
    -> ReporteVentasPorTrabajador

reporteVentasPorFecha(ventas, fecha)
    -> ReporteVentas

reporteVentasPorRango(ventas, rangoFecha)
    -> ReporteVentas

reporteProductosMasVendidos(ventas, filtroPeriodo?)
    -> List<ProductoVendido>

reporteVentasPorCategoria(ventas, filtroPeriodo?)
    -> ReporteVentasPorCategoria

reporteVentasPorMetodoPago(ventas, filtroPeriodo?)
    -> ReporteVentasPorMetodoPago
```

No introducen reglas nuevas de negocio.

---

# 20. Eventos de dominio

Eventos mínimos:

```text
ProductoCreado
ProductoActualizado
ProductoDesactivado
CajetillaAbierta
VentaIniciada
VentaConfirmada
VentaCancelada
PagoEfectivoConfirmado
PagoQRPreparado
PagoQRConfirmado
PagoQRRechazado
PagoQRExpirado
CompraRegistrada
SesionIniciada
SesionFinalizada
TrabajadorRegistrado
TrabajadorActualizado
TrabajadorDesactivado
ConfiguracionActualizada
```

Los eventos son datos que representan hechos ocurridos. Se manejan in-process; no se prescribe un bus externo.

---

# 21. Composición funcional de procesos principales

## Venta

```text
iniciarVenta
    -> agregarDetalle
    -> calcularTotal
    -> prepararPago
    -> confirmarPago
    -> verificarStock
    -> prepararConsumoStock
    -> confirmarVenta
    -> generar eventos
```

## Cigarrillo

```text
crearCigarrillo
    -> calcularPrecioUnitario
    -> verificar disponibilidad
    -> abrirCajetilla si es necesario
    -> consumir unidades
```

## Combo

```text
crearCombo (componentes[])
    -> calcularStockDisponibleCombo
    -> verificar RN-COMBO-01 (stock suficiente en todos los componentes)
    -> consumoDeComponentes
    -> aplicar consumo sobre el inventario de cada componente
```

## Compra

```text
crearDetalleCompra
    -> registrarCompra
    -> aumentarStock
    -> crearMovimientoInventario
```

---

# 22. Propiedades funcionales para TDD

## Cigarrillos

Para entradas válidas:

```text
precioUnitario > precioCajetilla / unidades
precioUnitario * unidades > precioCajetilla
```

## Cantidad

```text
restar(a,b)
```

solo es válido si `a >= b`.

## Venta

- Una venta cancelada no vuelve a confirmarse.
- Una venta QR sin pago confirmado no se confirma.
- Una venta confirmada tiene pago confirmado.

## Stock

Nunca debe producirse stock negativo.

## Horario

Cambiar el horario no cambia la posibilidad de iniciar sesión ni vender.

## Cliente

Una venta sin cliente sigue siendo válida.

---

# 23. Matriz RN → funciones

| RN | Funciones principales |
|---|---|
| RN01 | `calcularPrecioUnitarioCigarrillo` |
| RN02 | `calcularPrecioUnitarioCigarrillo` |
| RN03 | `crearCigarrillo`, `calcularPrecioUnitarioCigarrillo` |
| RN04 | `configurarDescuento`, `calcularDescuentoDetalle`, `calcularPrecioFinalCombo` |
| RN05 | `verificarStock`, `disminuirStock`, `confirmarVenta` |
| RN06 | `confirmarPagoQR`, `confirmarVenta` |
| RN07 | `cancelarVenta`, `restituirStock` |
| RN08 | `cancelarVenta` |
| RN09 | `puedeEjecutar` y funciones administrativas |
| RN10 | `verificarCredencial`, `autenticar` |
| RN11 | `asociarProducto`, `desasociarProducto` |
| RN12 | `registrarCompra` |
| RN13 | `crearMovimientoInventario` |
| RN14 | `compararConHorario` |
| RN15 | `crearPagoEfectivo`, `calcularCambio` |
| RN16 | `crearRegistroAuditoria` |
| RN17 | `abrirCajetilla`, `prepararConsumoStock` |
| RN18 | `puedeEjecutar` |
| RN19 | `crearCliente`, `asociarCliente` |
| RN20 | `consultarHistorialCliente` |
| RN21* | `cancelarVenta` mediante política de autorización |
| RN22 (RN-COMBO-01) | `calcularStockDisponibleCombo`, `consumoDeComponentes` |

`* RN21 es provisional.`

---

# 24. Matriz UC → funciones

| UC | Funciones principales |
|---|---|
| UC01 Iniciar sesión | `autenticar`, `iniciarSesion` |
| UC02 Cerrar sesión | `cerrarSesion` |
| UC03 Gestionar trabajadores | `crearTrabajador`, `modificarTrabajador`, `activarTrabajador`, `desactivarTrabajador` |
| UC04 Historial de sesiones | `compararConHorario` + consulta |
| UC05 Gestionar producto | `crearProducto`, `modificarProducto`, `activarProducto`, `desactivarProducto`, `cambiarPrecio` |
| UC06 Registrar cigarrillo | `crearCigarrillo`, `calcularPrecioUnitarioCigarrillo` |
| UC07 Abrir cajetilla | `abrirCajetilla` |
| UC08 Gestionar combo | `crearCombo`, `calcularPrecioFinalCombo`, `desactivarCombo` |
| UC09 Registrar venta | `iniciarVenta`, `agregarDetalle`, `calcularTotal`, `verificarStock`, `calcularStockDisponibleCombo`, `consumoDeComponentes`, `confirmarVenta` |
| UC10 Cancelar venta | `cancelarVenta`, `restituirStock` |
| UC11 Pago efectivo | `crearPagoEfectivo`, `calcularCambio`, `confirmarVenta` |
| UC12 Pago QR | `prepararPagoQR`, `confirmarPagoQR`, `confirmarVenta` |
| UC13 Consultar inventario | `consultarInventario`, `consultarBajoStock`, `consultarAgotados` |
| UC14 Historial inventario | `consultarMovimientos` |
| UC15 Registrar compra | `registrarCompra`, `aumentarStock`, `crearMovimientoInventario` |
| UC16 Gestionar proveedores | `crearProveedor`, `modificarProveedor`, `activarProveedor`, `desactivarProveedor`, `asociarProducto` |
| UC17 Reportes | funciones `reporte*` |
| UC18 Configuración | `crearConfiguracion`, `actualizarConfiguracion` |
| UC19 Auditoría | `crearRegistroAuditoria`, `consultarAuditoria` |
| UC20 Identificar cliente | `crearCliente`, `asociarCliente` |
| UC21 Historial cliente | `consultarHistorialCliente` |

---

# 25. Funciones puras prioritarias para TDD

```text
crearDinero
sumarDinero
restarDinero
multiplicarDinero
aplicarPorcentaje

crearCantidad
sumarCantidad
restarCantidad
esSuficiente

crearPorcentaje
aplicarPorcentaje

calcularPrecioUnitarioCigarrillo
abrirCajetilla

calcularPrecioFinalCombo
calcularStockDisponibleCombo
consumoDeComponentes

calcularSubtotal
calcularDescuentoDetalle
calcularTotal

calcularCambio

verificarStock
prepararConsumoStock

puedeEjecutar
compararConHorario

consultarBajoStock
consultarAgotados
```

Estas funciones no deben depender de Spring, base de datos ni HTTP.

---

# 26. Fronteras externas

Las siguientes operaciones pertenecen a aplicación/infraestructura:

```text
persistirProducto
persistirVenta
persistirCompra
persistirTrabajador
persistirMovimiento
persistirAuditoria

obtenerProductos
obtenerVentas
obtenerTrabajadores
obtenerProveedores

generarHash
verificarHash

generarRepresentacionQR
consultarPasarelaQR
obtenerFechaHoraActual
```

La API de dominio no prescribe su implementación.

---

# 27. Arquitectura conceptual

```text
Presentación (SolidJS)
        |
        v
Aplicación (casos de uso / coordinación)
        |
        v
DOMINIO (Documento F)
 tipos + entidades + funciones + reglas + resultados
        ^
        |
Infraestructura (persistencia / integraciones)
```

La dependencia debe apuntar hacia el dominio. El dominio no depende de Spring Boot, JPA, HTTP ni SolidJS.

---

# 28. Estrategia de evolución

El Documento F es una especificación versionable, no un contrato inmutable.

Si durante TDD aparece un descubrimiento:

```text
hallazgo
  -> contrato afectado
  -> tests
  -> implementación
```

Si cambia una regla:

```text
Documento B -> Documento F -> tests -> implementación
```

Si cambia un requerimiento:

```text
Documento A -> UC/RN -> Documento F -> tests -> implementación
```

No se debe rehacer automáticamente toda la documentación ante un cambio local.

---

# 29. Criterio de finalización

El Documento F se considerará suficientemente definido cuando:

- cada concepto relevante tenga un tipo o justificación;
- cada entidad tenga comportamiento o justificación como dato;
- cada RN tenga al menos una función responsable;
- cada UC tenga funciones suficientes para ejecutarse;
- cada función relevante tenga entrada y salida definidas;
- los errores de negocio estén identificados;
- las funciones puras estén identificadas;
- las fronteras con infraestructura estén identificadas;
- las propiedades importantes puedan convertirse en tests;
- las decisiones provisionales estén marcadas;
- no existan funciones introducidas únicamente “por si acaso” sin trazabilidad.

---

# 30. Relación con la siguiente etapa

Documento F define **qué comportamiento funcional debe existir en el dominio**.

No define todavía el backlog FDD ni la estructura definitiva de archivos Java.

La siguiente etapa transforma esta especificación en:

```text
Feature
  -> funciones incluidas
  -> tipos utilizados
  -> reglas cubiertas
  -> tests
  -> dependencias
  -> orden de implementación
  -> capas atravesadas
```

Eso permitirá iniciar TDD sobre una especificación concreta sin acoplar el dominio a Spring Boot.
