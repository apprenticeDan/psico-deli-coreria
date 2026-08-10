# Creación del Proyecto Backend (Spring Boot + SQLite)

Este documento registra cómo se inicializó el proyecto backend para soportar la arquitectura "Domain First" con generación automática de la base de datos (SQLite) para fines académicos y de desarrollo inicial.

## Requisitos
- Java 21+
- Gradle

## Estructura Inicial
Se creó manualmente la estructura básica de Spring Boot en la carpeta `/backend` usando Gradle como gestor de dependencias.

1. Se crearon los archivos `build.gradle` y `settings.gradle`.
2. Se agregaron las dependencias de `spring-boot-starter-data-jpa`, el driver `sqlite-jdbc` y el dialecto `hibernate-community-dialects` (necesario para SQLite en Hibernate 6).
3. Se configuró `application.properties` para usar `jdbc:sqlite:psicodeli.sqlite` y `spring.jpa.hibernate.ddl-auto=update` para que las tablas se autogeneren en base a los modelos de dominio.

## ¿Por qué SQLite y autogeneración?
Para cumplir con los entregables académicos donde se requiere ver las tablas, hemos adelantado la creación de las entidades JPA. Al correr el proyecto, Spring Boot lee estas clases y crea el archivo SQLite con las tablas. Esto no interfiere con nuestro dominio puro (que usa Records de Java 21), ya que la lógica persistirá separada en la capa de infraestructura.

## Estado de Implementación del Modelo (Doc F vs Código)

Para mantener trazabilidad con el **Documento F** y no perder el control de lo que falta para tener el modelo completo en la base de datos (SQLite), aquí detallamos el estado actual de las entidades y repositorios.

### ✅ Implementado (Feature 0.1 - Base)
- **Producto:** `Producto` (Record), `ProductoJpaEntity`, `ProductoRepository`
- **Enums Base:** `CategoriaProducto`, `EstadoProducto`

### ⏳ Pendiente por Implementar

**Productos Específicos (Documento F - Secciones 6 y 7):**
- **Cigarrillo:** Requiere mapear la lógica de `stockCajetillas` y `stockUnidadesSueltas`.
- **Combo & ComponenteCombo:** Requiere mapear la relación de composición.

**Actores (Documento F - Secciones 8, 9 y 10):**
- **Trabajador:** Incluyendo enum `Rol` (VENDEDOR, ADMINISTRADOR) y Credenciales.
- **Cliente:** Datos de contacto e historial.
- **Proveedor:** Asociación M:N con Productos.

**Ventas y Transacciones (Documento F - Secciones 11 y 13):**
- **Venta & DetalleVenta:** Entidad principal de salidas.
- **Pago (Efectivo/QR):** Modelado de la transacción financiera.

**Inventario y Compras (Documento F - Secciones 15 y 16):**
- **Compra & DetalleCompra:** Ingreso de mercadería.
- **MovimientoInventario:** Registro append-only (Kardex/Auditoría de stock).

**Transversales:**
- **RegistroAuditoria:** Log de operaciones críticas.
- **ConfiguracionSistema:** Parámetros globales.
- **Sesiones:** Control de turnos.
