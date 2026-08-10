Panorama general de etapas

1. Documentación del análisis
   • ERF
   • ERNF
   • RN
   • UC
   • Modelo de Dominio
   • Matrices de trazabilidad
↓
2. Especificación del dominio
   • Tipos abstractos (ADTs)
   • Value Objects
   • Entidades
   • Firmas de funciones
   • Contratos (entradas, salidas, precondiciones, postcondiciones)
   • Errores del dominio
   • Dependencias entre funciones
↓
3. Planificación FDD
   • Feature 0: Núcleo/Core del dominio (Domain First)
   • Feature 1..N: Capacidades del sistema (verticales)
↓
4. TDD por micro-feature
   • Escribir los tests de la siguiente función
   • Implementar la función mínima
   • Refactorizar
   • Integrar con el resto del dominio
↓
5. Integración por features
   • Capa de aplicación
   • Persistencia
   • Adaptadores (Spring Boot)
   • Frontend
