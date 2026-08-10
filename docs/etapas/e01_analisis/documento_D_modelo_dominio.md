# Documento D — Modelo de Dominio

> **Criterios aplicados:** este documento describe entidades, Value Objects, agregados y eventos — el "sustantivo" del sistema. **No contiene reglas de negocio** (viven en el Documento B) **ni planificación de features** (vivirá en el documento de Etapa 3, FDD). Complementa el Documento A (RF/RNF), el Documento B (RN) y el Documento C (UC), de los cuales se derivan estas entidades.
>
> Reemplaza a `dominio_licoreria.md`. Alcance decidido: DDD táctico sobre un **monolito modular con Clean Architecture**. Se descarta explícitamente CQRS, bus de mensajería, microservicios y otros patrones — ver sección 7, decisiones descartadas.

---

## 1. Glosario del Lenguaje Ubicuo

| Término | Definición en el negocio |
|---|---|
| **Producto** | Cualquier artículo que la licorería vende: cerveza, gaseosa, cigarrillo, refresco, trago o combo. |
| **Categoría** | Clasificación de un producto (Cerveza, Gaseosa, Cigarrillo, Refresco, Trago, Combo). |
| **Cajetilla** | Unidad de venta cerrada de cigarrillos, compuesta por varias unidades individuales. |
| **Combo** | Producto compuesto por Gaseosa + Hielo + Trago, con un descuento propio configurable. |
| **Stock** | Cantidad disponible de un producto para la venta. |
| **Venta** | Transacción comercial donde un Trabajador entrega uno o más Productos a un cliente a cambio de un Pago. |
| **Detalle de Venta** | Línea dentro de una Venta: producto, cantidad, precio unitario, subtotal. |
| **Pago** | Confirmación del cobro de una Venta, realizado en Efectivo o mediante QR. |
| **Trabajador** | Persona que opera el sistema; puede tener rol Administrador o Vendedor. Incluye sus credenciales de acceso. |
| **Proveedor** | Persona o empresa que suministra productos a la licorería. |
| **Compra** | Transacción de abastecimiento: ingreso de productos desde un Proveedor hacia el inventario. |
| **Movimiento de Inventario** | Registro histórico de cualquier cambio de stock (por Venta, Cancelación o Compra). |
| **Descuento** | Reducción aplicada sobre el precio de un Producto. Atributo opcional disponible para cualquier categoría (por defecto 0); hoy, como regla de negocio vigente, solo se configura para Combos. |
| **Incremento** | Porcentaje aplicado al precio base de un cigarrillo vendido por unidad, respecto al precio de la cajetilla. |
| **Horario asignado** | Plan de días/horas en que se espera que un Trabajador trabaje. Es informativo: el sistema no restringe accesos ni ventas en función de él. |
| **Sesión** | Periodo entre el inicio y cierre de sesión de un Trabajador. Su historial permite hacer seguimiento de cuándo trabajó cada empleado. |
| **Auditoría** | Registro histórico de quién hizo qué y cuándo, sobre operaciones críticas. |
| **Cliente** | Persona a quien se le realiza una venta. Su identificación es **opcional** — la venta no depende de conocerlo. |
| **Parámetro** | Valor configurable por el Administrador que puede cambiar libremente sin alterar la lógica del sistema. Distinto de una regla de negocio: el parámetro es el "cuánto", la regla es el "qué política existe". |

---

## 2. Mapa del dominio

```
Negocio
│
├── Ventas        (core domain — la mayor complejidad de reglas)
├── Catálogo       (productos, categorías, combos, precios — incluye Stock)
├── Compras        (abastecimiento, proveedores)
├── Usuarios       (autenticación + trabajadores, fusionados — incluye horario y sesiones)
├── Reportes       (solo lectura sobre Ventas/Inventario)
├── Configuración  (parámetros del negocio — de solo lectura para el resto de módulos)
├── Auditoría      (registro transversal de operaciones críticas, alimentado por eventos de dominio)
└── Integraciones  (Pasarela QR — simulada/mock)
```

**Nota de diseño:** *Inventario* no es un subdominio independiente. El stock vive dentro del agregado `Producto`, y `MovimientoInventario` es un registro de auditoría (no un agregado con reglas propias). Esto simplifica la consistencia: todo cambio de stock ocurre dentro de la misma transacción que la Venta o la Compra que lo origina. Ver sección 7 para el detalle de esta decisión y sus alternativas.

---

## 3. Entidades

| Entidad | Descripción | Agregado raíz |
|---|---|---|
| **Producto** | Representa cualquier artículo vendible. Contiene subtipo/categoría, precio, stock, `estado` (ACTIVO/INACTIVO) y un `descuento` opcional (por defecto 0/null, disponible para cualquier producto aunque hoy solo se configure en Combos). Los cigarrillos añaden `precioCajetilla`, `unidadesPorCajetilla`, `porcentajeIncremento`, y mantienen **dos stocks diferenciados**: cajetillas cerradas y unidades sueltas, con una operación de transformación entre ambos (`abrirCajetilla`). Los combos además añaden `componentes[]`. | Sí |
| **Venta** | Encabezado de una transacción de venta. | Sí |
| **DetalleVenta** | Línea de producto dentro de una Venta. | No (parte del agregado Venta) |
| **Trabajador** | Persona que opera el sistema; incluye credenciales, rol, `estado` (ACTIVO/INACTIVO) y `horarioAsignado` (informativo). El rol Administrador es un **superconjunto** de permisos del rol Vendedor — no son roles excluyentes ni requieren sesiones distintas. | Sí |
| **RegistroSesion** | Registro append-only de un inicio/cierre de sesión de un Trabajador. Alimenta el historial de seguimiento. | No es agregado — se genera como efecto de eventos de sesión. |
| **Proveedor** | Empresa/persona proveedora, con productos que puede suministrar. | Sí |
| **Compra** | Encabezado de una transacción de abastecimiento, con `estado` (`REGISTRADA`). | Sí |
| **DetalleCompra** | Línea de producto dentro de una Compra. | No (parte del agregado Compra) |
| **MovimientoInventario** | Registro de auditoría de un cambio de stock (tipo: venta, cancelación, compra, apertura de cajetilla), con fecha, cantidad, producto y responsable. | No es agregado — registro append-only generado como efecto de otros agregados. |
| **RegistroAuditoria** | Registro append-only de una operación crítica, con fecha, tipo, responsable y detalle. Consolida `MovimientoInventario` y `RegistroSesion` bajo un mismo esquema de consulta transversal. | No es agregado — se genera como efecto de los eventos de dominio. |
| **ConfiguracionSistema** | Contiene los parámetros globales editables por el Administrador. | Sí (agregado simple, un único registro vigente) |
| **Cliente** | Persona identificada opcionalmente en una venta. Contiene solo `nombre` y `contacto` (ambos opcionales). No incluye datos de facturación, crédito ni fidelización. | Sí (agregado liviano) |

---

## 4. Value Objects

Inmutables, sin identidad propia, con comportamiento (no getters/setters sueltos).

| Value Object | Descripción | Comportamiento típico |
|---|---|---|
| **Dinero / Precio** | Monto monetario. | `sumar()`, `multiplicar(cantidad)`, `aplicarPorcentaje(pct)` |
| **Cantidad** | Cantidad de unidades de un producto. | Validación: nunca negativa |
| **Porcentaje** | Valor entre 0 y 100 (incremento de cigarrillo, descuento de producto). | `aplicarSobre(Dinero)` |
| **Descuento** | Reducción opcional sobre el precio de un Producto. Por defecto 0/ausente. | `aplicarSobre(Dinero)` |
| **Credencial** | Usuario + contraseña cifrada, nunca en texto plano. | — |
| **CodigoQR** | Código generado para un pago QR, con estado (pendiente/confirmado/expirado). Generado con un formato/estándar real; la confirmación es simulada (mock de pasarela). | `confirmar()`, `expirar()` |
| **EstadoVenta** | Enum: `EN_PROCESO`, `CONFIRMADA`, `CANCELADA`. | Transiciones controladas por el agregado Venta |
| **EstadoProducto** | Enum: `ACTIVO`, `INACTIVO`. | Transición controlada por el agregado Producto |
| **HorarioAsignado** | Días y rango horario esperado para un Trabajador. Puramente informativo. | — |
| **Cambio (vuelto)** | Resultado de `montoRecibido − total` en un pago en efectivo. | `calcular(montoRecibido, total)` — función pura |

*(Se elimina "Usuario" como Value Object de propuestas anteriores — era una entidad con identidad, no un VO; queda fusionado dentro de `Trabajador`.)*

---

## 5. Agregados (raíz y límites de consistencia)

```
Venta (raíz)
 ├── DetalleVenta[]
 ├── Pago (método, estado)
 ├── EstadoVenta
 └── Cliente (referencia, opcional — puede ser nula)

Producto (raíz)
 ├── Precio
 ├── Stock (Cantidad) — o, si es Cigarrillo: stockCajetillas + stockUnidadesSueltas
 ├── Categoría
 ├── EstadoProducto (ACTIVO | INACTIVO)
 ├── Descuento (opcional, por defecto 0/null)
 ├── (si Cigarrillo) precioCajetilla, unidadesPorCajetilla, porcentajeIncremento
 └── (si Combo) componentes[]

Compra (raíz)
 ├── Proveedor (referencia)
 ├── estado (REGISTRADA)
 └── DetalleCompra[]

Trabajador (raíz)
 ├── credenciales (usuario, Credencial)
 ├── rol (ADMINISTRADOR | VENDEDOR) — ADMINISTRADOR incluye todos los permisos de VENDEDOR
 ├── estado (ACTIVO | INACTIVO)
 └── HorarioAsignado (informativo)

Proveedor (raíz)
 └── productos que suministra (referencias)

ConfiguracionSistema (raíz)
 ├── porcentajeIncrementoCigarrilloDefault
 ├── umbralBajoStock
 └── datosDelNegocio

Cliente (raíz)
 ├── nombre (opcional)
 └── contacto (opcional)
```

**Regla de consistencia clave:** cualquier cambio de stock (dentro de `Producto`, incluyendo la transferencia cajetilla → unidad suelta) ocurre **en la misma transacción** que la Venta, Compra o apertura de cajetilla que lo origina — no hay actualización asíncrona ni eventual consistency entre agregados en esta etapa.

---

## 6. Eventos de dominio (in-process, síncronos)

Se despachan **dentro de la misma transacción**, sin bus de mensajería externo. Sirven para desacoplar módulos dentro del monolito (ej. que el módulo de Auditoría escuche `VentaRegistrada` sin que el módulo de Ventas conozca su existencia).

```
VentaRegistrada          → dispara: reducir stock, registrar MovimientoInventario
VentaCancelada           → dispara: restituir stock (si ya se había reducido), registrar MovimientoInventario
PagoConfirmado           → dispara: cerrar la Venta
CajetillaAbierta         → dispara: transferir stock cajetilla → unidad suelta, registrar MovimientoInventario
CompraRegistrada         → dispara: aumentar stock, registrar MovimientoInventario
ProductoCreado
ProductoDesactivado
ComboCreado
TrabajadorRegistrado
SesionIniciada           → dispara: registrar RegistroSesion
SesionFinalizada         → dispara: registrar RegistroSesion
ConfiguracionActualizada
```

Todos los eventos anteriores además disparan `registrar RegistroAuditoria`, ya que la auditoría es transversal a toda operación crítica.

---

## 7. Decisiones de arquitectura descartadas (y por qué)

Documentadas explícitamente para dejar constancia de que fueron evaluadas, no ignoradas por desconocimiento.

| Patrón | Decisión | Justificación |
|---|---|---|
| **CQRS** | No se usa. | El volumen de lectura/escritura de una licorería de una sucursal no justifica separar modelos de lectura y escritura. Los reportes se resuelven con repositorios de solo lectura sobre el mismo modelo. |
| **Bus de eventos / mensajería (Kafka, RabbitMQ)** | No se usa. | Los eventos de dominio se manejan in-process, dentro de la misma transacción. No hay servicios distribuidos que desacoplar. |
| **Microservicios** | No se usa. | Se opta por monolito modular con Clean Architecture; los módulos están separados a nivel de paquete, no de despliegue. |
| **Inventario como bounded context independiente** | No se usa. | El stock se modela como parte del agregado `Producto` para mantener consistencia transaccional simple. |
| **Cierre de sesión automático por inactividad** | No se usa. | En un punto de venta con 2 trabajadores y clientes esporádicos, forzar reautenticación por inactividad genera fricción sin beneficio de seguridad claro. El cierre de sesión es siempre una acción explícita. |
| **Vista/rol conmutable en una misma sesión** | No se usa. | El Administrador ya tiene todos los permisos del Vendedor (superconjunto); no hace falta un mecanismo de "cambiar de vista". |
| **Venta a crédito / "fiado"** | No se implementa todavía. | Requiere manejo de saldo pendiente, límites de crédito y cobranza — complejidad real no solicitada. `Cliente` queda modelado de forma que agregarlo después es extender el agregado, no reestructurarlo. |
| **Programa de fidelización / puntos** | No se implementa todavía. | Requiere reglas de acumulación y redención no solicitadas. La existencia de `Cliente` con historial de compras ya deja la base lista para esto si se pide más adelante. |

Si el negocio creciera (más sucursales, mayor volumen, necesidad real de reportes en tiempo real sobre grandes volúmenes), estas decisiones deberían revisitarse — quedan documentadas como puntos de evolución, no como limitaciones permanentes.

---

## Referencias cruzadas

- Reglas de negocio que restringen estas entidades → Documento B.
- Casos de uso que las ejercitan → Documento C.
- Matrices de trazabilidad (RF↔RN↔UC↔Agregado) → Documento E.
- Planificación por features (FDD) → pendiente, corresponde a la Etapa 3.
