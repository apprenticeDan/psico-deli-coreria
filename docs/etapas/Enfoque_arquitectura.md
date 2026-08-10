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
   implementación          implementación
   de dominio              de dominio
          │                     │
       Spring               SolidJS


- No todo tiene que implementarse dos veces!!

  Hay funciones que son:
    deterministas;
    puras;
    pequeñas;
    sin acceso a base de datos;
    sin Spring;
    sin HTTP;
    sin navegador.
  Son candidatas para existir tanto en Java como en TypeScript.

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
  Documento F
API funcional del dominio

Documento G
Arquitectura de implementación

Documento H
API de aplicación / casos de uso

Documento I
Contrato API HTTP

Documento J
Plan FDD + TDD


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
