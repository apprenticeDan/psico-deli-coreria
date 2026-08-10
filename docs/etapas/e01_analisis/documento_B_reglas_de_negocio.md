# Documento B — Reglas de Negocio (RN)

> **Criterios aplicados a cada RN:**
> 1. Expresa una **política, restricción o cálculo** del negocio — no una capacidad (eso es el Documento A) ni una interacción (eso es el Documento C).
> 2. **No** menciona nombres de clases, entidades o agregados (eso es el Documento D — Modelo de Dominio). Los términos de negocio (venta, cigarrillo, cajetilla, compra) sí se usan, porque son lenguaje ubicuo, no nombres de clase.
> 3. **No** contiene decisiones técnicas de implementación (algoritmos de hash, nombres de tablas, formatos de dato).
> 4. Es independiente del modelo de dominio — la relación "Regla → Agregado responsable" vive en el **Documento E — Matrices de Trazabilidad** (Matriz 4), no en este documento.
>
> Este documento reemplaza a la sección 7 de `dominio_licoreria.md` (que queda obsoleta en cuanto a las RN — el resto de esa sección, sobre entidades/VO/agregados, se mantiene vigente en el Documento D).

---

## Reglas vigentes

| # | Regla |
|---|---|
| RN01 | El precio de un cigarrillo vendido por unidad se calcula a partir del precio de la cajetilla dividido entre las unidades que la componen, más un porcentaje de incremento. |
| RN02 | El precio total de vender una cajetilla por unidades individuales debe ser siempre mayor al precio de la cajetilla cerrada. |
| RN03 | Todo cigarrillo posee un porcentaje de incremento configurable respecto al precio de la cajetilla, con un valor por defecto. |
| RN04 | El descuento solo se aplica a los productos configurados explícitamente para ello. Actualmente, únicamente los combos tienen esa configuración habilitada; el resto de productos se crea sin descuento, sin importar la cantidad comprada. |
| RN05 | No se puede vender un producto sin stock disponible. La disponibilidad debe verificarse tanto al agregar el producto a la venta como al confirmarla, para evitar inconsistencias entre operaciones simultáneas. |
| RN06 | Un pago mediante código QR debe confirmarse explícitamente antes de cerrar la venta; sin confirmación, la venta no puede darse por completada. |
| RN07 | Una venta cancelada antes de confirmar el pago no afecta el inventario. Una venta cancelada después de confirmado el pago debe restituir el stock correspondiente. |
| RN08 | Toda venta queda registrada de forma permanente, incluso si posteriormente se cancela; nunca se elimina del historial. |
| RN09 | Solo un trabajador con rol de Administrador puede crear, modificar o desactivar productos, sus precios (incluyendo el porcentaje de incremento de cigarrillos y el descuento de combos) y proveedores. |
| RN10 | Las credenciales de acceso de un trabajador deben almacenarse de forma segura, sin exponer la contraseña en texto plano. |
| RN11 | Un producto puede estar asociado a uno o más proveedores. |
| RN12 | Toda compra debe identificar el proveedor del que proviene y actualizar automáticamente el stock de los productos recibidos. |
| RN13 | Todo cambio de stock (por venta, cancelación o compra) debe quedar registrado indicando fecha, responsable, tipo de movimiento y cantidad. |
| RN14 | El horario asignado a un trabajador es de carácter informativo: el sistema no restringe el inicio de sesión ni el registro de ventas en función de él. El sistema debe permitir comparar el horario asignado contra las sesiones efectivamente realizadas, con fines de seguimiento. |
| RN15 | En un pago en efectivo, el monto entregado debe cubrir el importe total de la venta. El cambio a entregar es una consecuencia de esa diferencia. |
| RN16 | Toda operación crítica (venta, cancelación, cambio de producto o precio, compra, inicio o cierre de sesión, cambio de configuración) debe quedar registrada indicando qué ocurrió, cuándo y quién fue responsable. |
| RN17 | Si al vender un cigarrillo por unidad no hay suficientes unidades sueltas disponibles pero sí cajetillas cerradas del mismo producto, el sistema debe permitir abrir una cajetilla como parte del mismo proceso de venta, sin requerir una operación separada. |
| RN18 | El rol Administrador incluye todos los permisos del rol Trabajador. Una persona con rol Administrador puede realizar cualquier operación de Trabajador (incluyendo ventas) con su misma sesión, sin necesidad de cambiar de vista o volver a autenticarse. |
| RN19 | La identificación del cliente en una venta es opcional: el sistema debe permitir completar la venta sin asociar un cliente. |
| RN20 | Si se asocia un cliente a una venta, sus compras quedan disponibles para consulta en futuras visitas, sin que esto implique facturación, crédito ni beneficios automáticos. |
| RN21 ⚠️ *provisional* | Cancelar una venta ya confirmada requiere rol Administrador. **Esta regla no está confirmada con el negocio** — surge de la revisión v1.1 del Documento A (RF07: "según los permisos establecidos") sin que se precisara cuáles son esos permisos. Se adopta como valor por defecto conservador, fácil de relajar después. |
| RN22 (RN-COMBO-01) | Un combo puede venderse únicamente cuando existe stock suficiente de todos sus productos componentes para la cantidad solicitada. La disponibilidad del combo se deriva del stock de sus componentes; el combo no tiene un stock propio independiente. |

---

## Reglas eliminadas (con justificación)

| # (anterior) | Motivo de eliminación |
|---|---|
| RN05 (antigua) | Redundante — ya implícita en RN04: si el descuento solo aplica a productos configurados para ello, "los demás productos no tienen descuento" no aporta información nueva. |
| RN18 (antigua) | Duplicaba el criterio de verificación ya definido en el Documento A, RF13 ("el nuevo valor se usa como valor por defecto... sin afectar retroactivamente los ya existentes"). No hace falta repetirlo como regla de negocio independiente. |

---

## Trazabilidad con la numeración anterior

| Anterior | Nueva | Cambio |
|---|---|---|
| RN01, RN02 | RN01, RN02 | Sin cambios |
| RN03 | RN03 | Se removió la cláusula de autorización (ahora unificada en RN09) |
| RN04 | RN04 | Sin cambios |
| RN05 | — | Eliminada (redundante con RN04) |
| RN06, RN07, RN08, RN09 | RN05, RN06, RN07, RN08 | Sin cambios de contenido, solo renumeradas |
| RN10 | RN09 | Se fusionó con la cláusula de autorización de RN03 |
| RN11 | RN10 | Se removió el término técnico `PasswordHash` |
| RN12, RN13 | RN11, RN12 | Se removieron nombres de entidad (`Producto`, `Compra`, `DetalleCompra`) |
| RN14 | RN13 | Se removió `MovimientoInventario` |
| RN15 | RN14 | Se removieron `HorarioAsignado` y `RegistroSesion` |
| RN16 | RN15 | Redactada de forma más abstracta, según sugerencia recibida |
| RN17 | RN16 | Se removió `RegistroAuditoria` |
| RN18 | — | Eliminada (duplicaba criterio de verificación de RF13) |
