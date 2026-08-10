# Documento E — Matrices de Trazabilidad

> Consolida las relaciones entre los Documentos A (RF), B (RN), C (UC) y D (Modelo de Dominio). Cierra la Etapa 1 (Documentación del análisis).

---

## Matriz 1 — RF ↔ Casos de Uso

| RF | Casos de uso |
|---|---|
| RF01. Autenticación | UC01, UC02 |
| RF02. Trabajadores | UC03, UC04 |
| RF03. Productos | UC05 |
| RF04. Cigarrillos | UC06, UC07 |
| RF05. Combos | UC08 |
| RF06. Registro de ventas | UC09 |
| RF07. Cancelación de ventas | UC10 |
| RF08. Pagos | UC11, UC12 |
| RF09. Inventario | UC13, UC14 |
| RF10. Compras | UC15 |
| RF11. Proveedores | UC16 |
| RF12. Reportes | UC17 |
| RF13. Configuración | UC18 |
| RF14. Auditoría | UC19 |
| RF15. Clientes (opcional) | UC20, UC21 |

Los 15 RF tienen al menos un caso de uso; ningún caso de uso existe sin un RF que lo respalde.

---

## Matriz 2 — Caso de Uso ↔ Reglas de Negocio

| UC | Reglas de negocio aplicables |
|---|---|
| UC01. Iniciar sesión | RN10 |
| UC02. Cerrar sesión | — |
| UC03. Gestionar trabajadores | RN09, RN10, RN14 |
| UC04. Consultar historial de sesiones | RN14 |
| UC05. Gestionar producto | RN09, RN11 |
| UC06. Registrar cigarrillo | RN01, RN02, RN03, RN09 |
| UC07. Registrar apertura de cajetilla | RN13, RN17 |
| UC08. Gestionar combo | RN04, RN09 |
| UC09. Registrar venta | RN04, RN05, RN13, RN17, RN19 |
| UC10. Cancelar venta | RN07, RN08, RN13, RN16 |
| UC11. Pagar en efectivo | RN15 |
| UC12. Pagar con QR | RN06 |
| UC13. Consultar inventario | — |
| UC14. Consultar historial de movimientos | RN13 |
| UC15. Registrar compra | RN12, RN13 |
| UC16. Gestionar proveedores | RN09, RN11 |
| UC17. Generar reporte | — |
| UC18. Configurar parámetros | RN03 |
| UC19. Consultar auditoría | RN16 |
| UC20. Identificar cliente en la venta | RN19, RN20 |
| UC21. Consultar historial de compras de un cliente | RN20 |

**Nota:** UC02, UC13 y UC17 no tienen RN propia — son operaciones de lectura/cierre de sesión sin restricción de negocio adicional más allá de la autenticación general (RN10) y autorización (RN09), ya cubiertas por el flujo de login.

---

## Matriz 3 — RF ↔ Reglas de Negocio (derivada de la Matriz 1 + 2)

| RF | RN aplicables |
|---|---|
| RF01 | RN10 |
| RF02 | RN09, RN10, RN14 |
| RF03 | RN09, RN11 |
| RF04 | RN01, RN02, RN03, RN09, RN13, RN17 |
| RF05 | RN04, RN09 |
| RF06 | RN04, RN05, RN13, RN17 |
| RF07 | RN07, RN08, RN13, RN16 |
| RF08 | RN06, RN15 |
| RF09 | RN13 |
| RF10 | RN12, RN13 |
| RF11 | RN09, RN11 |
| RF12 | — |
| RF13 | RN03 |
| RF14 | RN16 |
| RF15 | RN19, RN20 |

Toda RN vigente (RN01–RN18) queda cubierta por al menos un RF, con excepción de RN18 (rol Administrador como superconjunto), que es una regla transversal de autorización aplicable a **todos** los RF marcados con "solo Administrador" (RF02, RF03, RF04, RF05, RF10, RF11, RF13) — no se restringe a un único RF porque describe una relación entre roles, no una capacidad puntual.

---

## Matriz 4 — Reglas de Negocio ↔ Agregado responsable

> Esta es la tabla que originalmente vivía dentro del Documento B. Se movió aquí para que B se mantenga independiente del modelo de dominio (ver criterio 2 del Documento B) — el contenido de cada regla no cambia, solo se le agrega esta referencia cruzada una vez que el Documento D ya existe.

| RN | Agregado(s) responsable(s) |
|---|---|
| RN01 | Producto |
| RN02 | Producto |
| RN03 | Producto (valor por defecto sourced de ConfiguracionSistema) |
| RN04 | Producto |
| RN05 | Producto / Venta |
| RN06 | Venta |
| RN07 | Venta / Producto |
| RN08 | Venta |
| RN09 | Trabajador (autorización — transversal) |
| RN10 | Trabajador |
| RN11 | Producto / Proveedor |
| RN12 | Compra / Producto |
| RN13 | Producto / MovimientoInventario |
| RN14 | Trabajador / RegistroSesion |
| RN15 | Venta |
| RN16 | RegistroAuditoria (transversal) |
| RN17 | Producto |
| RN18 | Trabajador (autorización — transversal) |
| RN19 | Venta / Cliente |
| RN20 | Cliente |

---

## Matriz 5 — Caso de Uso ↔ Agregado principal

| UC | Agregado principal |
|---|---|
| UC01, UC02 | Trabajador |
| UC03 | Trabajador |
| UC04 | RegistroSesion |
| UC05 | Producto |
| UC06, UC07 | Producto |
| UC08 | Producto |
| UC09, UC10, UC11, UC12 | Venta |
| UC13 | Producto |
| UC14 | MovimientoInventario |
| UC15 | Compra |
| UC16 | Proveedor |
| UC17 | Venta (lectura) |
| UC18 | ConfiguracionSistema |
| UC19 | RegistroAuditoria |
| UC20, UC21 | Cliente |

---

## Cobertura verificada

- **15/15 RF** tienen al menos un UC (Matriz 1).
- **20/20 RN** están vinculadas a al menos un RF, directa o transversalmente (Matriz 3).
- **13/13 agregados/entidades** del Documento D son referenciados por al menos un UC (Matriz 5) — no hay entidades "huérfanas" sin caso de uso que las justifique.

Con esto queda cerrada la Etapa 1 (Documentación del análisis). El siguiente paso, según el plan de 5 etapas, es la **Etapa 2 — Especificación del dominio**: ADTs, firmas de función, contratos (pre/postcondiciones) y errores del dominio, a partir de las entidades y VO ya definidos en el Documento D.
