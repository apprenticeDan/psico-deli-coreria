# Enfoque: Especificación de la API funcional del dominio (Documento F — Especificación)

Una API conceptual/programática del dominio, independiente de la "tecnología" (Modelos y contratos compartidos, pero con la lógica pura centralizada en el Backend como única fuente de verdad).

---

                 ┌──────────────┐
                 │     Datos    │
                 └──────┬───────┘
                        │
                        ▼
┌─────────────┐   ┌──────────────┐
│   Entidades │──▶│   Funciones  │
└─────────────┘   └──────┬───────┘
                         │
                         ▼
                  Reglas de negocio
                         │
                         ▼
                       Tests

- Tipos abstractos del dominio
  Aquí se define qué datos existen.

- entidades y agregados
  Identidad
  Datos
  Invariantes
  Operaciones
  Dependencias
  Relaciones
  Eventos/resultados
Pero aquí hay una distinción importante ya que no necesariamente todas las operaciones deben ser métodos de la entidad.

En un diseño funcional puede resultar mejor tener:
Producto → datos
calcularPrecioCigarrillo(...) → función pura
aplicarDescuento(...) → función pura
abrirCajetilla(...) → función pura
registrarVenta(...) → función de dominio

- catálogo completo de funciones
  Ventas
  Inventario
  Compras y proveedores
  Trabajadores, horarios y sesiones
  Cliente
  Configuración
  Auditoría mínima

- contratos de tests


---

resumiendo

DOCUMENTACIÓN ETAPA 1
        ↓
API FUNCIONAL DEL DOMINIO
        ↓
CONTRATOS + TESTS DEL DOMINIO
        ↓
FEATURE
        ↓
implementación incremental

