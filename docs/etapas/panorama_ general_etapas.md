# Panorama general de etapas y artefactos documentales

1. Documentación del análisis (Etapa 01)
   • Documento A: Requisitos Funcionales (ERF) y No Funcionales (ERNF)
   • Documento B: Reglas de Negocio (RN)
   • Documento C: Casos de Uso del Negocio (Requisitos de usuario)
   • Documento D: Modelo de Dominio Conceptual
   • Documento E: Matrices de trazabilidad
↓
2. Especificación (Etapa 02)
   • Documento F: Especificación de la API Funcional del Dominio (Firmas, contratos, ADTs, funciones puras y errores)
   • Documento G: Contrato API HTTP (Definición temprana para permitir trabajo en paralelo de Frontend y Backend)
↓
3. Planificación FDD (Etapa 03)
   • Documento H: Plan de Features
     - Feature 0: Núcleo/Core del dominio (Domain First)
     - Feature 1..N: Capacidades del sistema (verticales)
↓
4. TDD por micro-feature (Etapa 04)
   • TDD iterativo sobre lógica pura de dominio (Backend)
     - Escribir test, implementar función mínima, refactorizar.
↓
5. Integración por features (Etapa 05)
   • Capa de aplicación (Servicios de Aplicación / Orquestación en Backend)
   • Persistencia y Adaptadores (Spring Boot)
   • Frontend (Consumo de API HTTP y UI)
