# Enfoque arquitectura compartida

- Compartir el modelo conceptual

                 DOCUMENTO F
                     │
              contratos funcionales
                     │
          ┌──────────┴──────────┐
          │                     │
       Backend               Frontend
       Java                  TypeScript
          │                     │
   implementación          validaciones
   de dominio              ligeras / UI
          │                     │
       Spring               SolidJS


- Fuente Única de Verdad (Backend)

  El frontend consumirá los modelos (ADTs) y contratos establecidos en la Especificación de Dominio (Doc F) para crear una experiencia fluida (por ejemplo, validar formularios o mostrar cálculos en vivo), pero **la lógica pura de dominio centralizada y la autoridad final vivirán exclusivamente en el backend.**
  
  Con esto, evitamos duplicar lógica compleja de negocio entre Java y TypeScript, reduciendo drásticamente el costo de mantenimiento. Solo duplicaremos lógica en el frontend cuando sea estrictamente necesaria para la UX.

Hay cosas que NO debemos duplicar
El frontend puede preparar una venta,
Subtotal: Bs. 35
Descuento: Bs. 5
Total: Bs. 30
pero la autoridad final debe estar en el backend.
Frontend
   │
   │ POST /ventas
   ↓
Backend
   │
   ├── verifica stock
   ├── verifica precios
   ├── calcula total
   ├── confirma pago
   ├── modifica inventario
   ├── registra auditoría
   └── registra venta

Tendríamos Funciones de dominio puras(en ambos ends cuando tengan sentido) y Operaciones de dominio/aplicación (fundamentalmente backend)

- los contratos HTTP
Podemos tener otra capa de contratos:
                 DOMINIO
                    │
              API funcional
                    │
          ┌─────────┴─────────┐
          │                   │
      Backend              Frontend
          │                   │
      casos de uso          UI
          │                   │
          └────── HTTP ───────┘
                    │
              API REST

 Nos lleva a una arquitectura bastante limpia

                   ┌─────────────────────┐
                  │     FRONTEND        │
                  │      SolidJS        │
                  │                     │
                  │ UI                  │
                  │ estado              │
                  │ funciones puras     │
                  │ API client          │
                  └──────────┬──────────┘
                             │
                           HTTP
                             │
                  ┌──────────▼──────────┐
                  │       BACKEND       │
                  │      SpringBoot     │
                  │                     │
                  │ Controllers         │
                  │ Application         │
                  │ Domain              │
                  │ Infrastructure      │
                  └──────────┬──────────┘
                             │
                         Persistence
                             │
                       ┌─────▼─────┐
                       │ Database  │
                       └───────────┘

 Es decir algo así:

  Documento A-E
Análisis y Requisitos del Negocio (Casos de uso de negocio, dominio conceptual, reglas)

  Documento F
API funcional del dominio (Especificación de tipos y contratos)

  Documento G
Contrato API HTTP (Swagger/OpenAPI temprano para paralelizar Frontend/Backend)

  Documento H
Plan FDD + TDD (Capa de aplicación y despliegue iterativo)


- Resumen

REQUERIMIENTOS
       ↓
CASOS DE USO
       ↓
MODELO DE DOMINIO
       ↓
API FUNCIONAL DEL DOMINIO
       ↓
       ├───────────────┐
       ↓               ↓
  IMPLEMENTACIÓN   CONTRATOS
    BACKEND          FRONTEND
       ↓               ↓
     Java          TypeScript
       ↓               ↓
 Spring Boot       SolidJS
       └───────┬───────┘
               ↓
          API HTTP

   Para el proyecto, parece que sí es recomendable. No es sobreingeniería mientras mantengamos la regla de oro: compartir contratos y lógica pura cuando aporta valor, pero no intentar construir una plataforma de dominio multiplataforma innecesariamente compleja.

   Queda la pregunta, si esto encaja muy bien con la intención original de FP + Clean Light + TDD + FDD? el Documento F define el comportamiento; TDD demuestra ese comportamiento; FDD organiza su implementación; Clean Light mantiene las dependencias bajo control; y frontend/backend consumen el mismo modelo conceptual.
