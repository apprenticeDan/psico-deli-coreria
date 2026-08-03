# Licoreria 🚀

Bienvenido a nuestra proyecto, una aplicación Fullstack desarrollada con **Spring Boot (Java 21)** y **SolidJS (TypeScript)**.

## 🛠️ Configuración del Entorno de Desarrollo

Este proyecto utiliza Devcontainers (Docker/Podman) para estandarizar el entorno de todos los desarrolladores. No necesitas instalar Java, Node.js ni PostgreSQL en tu máquina local.

👉 **[Ver Instrucciones para Levantar el Entorno](docs/01_readme_init.md)**

---

## 🏃‍♂️ Cómo Ejecutar el Proyecto (Una vez dentro del entorno)

Una vez que hayas levantado el contenedor (ya sea por VS Code o manualmente) y tengas la terminal abierta en la raíz de este proyecto, sigue estos pasos:

### 1. Iniciar la Base de Datos
La base de datos **PostgreSQL** ya se está ejecutando automáticamente en segundo plano gracias al archivo `compose.yml`. No necesitas hacer nada adicional.

### 2. Iniciar el Backend (Spring Boot)
Abre una terminal, navega a la carpeta del backend y ejecuta el proyecto con Gradle:
```bash
cd backend
./gradlew bootRun
```
*La API estará disponible en `http://localhost:8080`*

### 3. Iniciar el Frontend (SolidJS + Vite)
Abre otra pestaña de terminal, navega a la carpeta del frontend e inicia el servidor de desarrollo:
```bash
cd frontend
pnpm install  # (Solo si hay dependencias nuevas)
pnpm run dev
```
*La aplicación web estará disponible en `http://localhost:5173`*

---

## 📚 Documentación (FDD)
Puedes encontrar toda la documentación de requerimientos, reglas de negocio, dominio y características en la carpeta `/docs`:

- `01_readme_init.md`: Configuración de la infraestructura (Contenedores).
- `02_contexto_y_requerimientos.md`: *[Pendiente]*
- `03_reglas_de_negocio.md`: *[Pendiente]*
- `04_modelo_de_dominio.md`: *[Pendiente]*
- `06_plan_de_implementacion.md`: *[Pendiente]*

---

## 👥 Equipo y Roles

Cada miembro del equipo debe crear su archivo de perfil en `docs/roles/`, siguiendo el formato indicado en la [PLANTILLA](docs/roles/PLANTILLA.md).

| Archivo | Rol | Estado |
|---------|-----|--------|
| [rol_lider.md](docs/roles/rol_lider.md) | Líder del Proyecto | 🔴 Pendiente |
| [rol_analista01.md](docs/roles/rol_analista01.md) | Analista 01 | 🔴 Pendiente |
| [rol_analista02.md](docs/roles/rol_analista02.md) | Analista 02 | 🔴 Pendiente |
| [rol_dev01.md](docs/roles/rol_dev01.md) | Desarrollador 01 | 🔴 Pendiente |
| [rol_dev02.md](docs/roles/rol_dev02.md) | Desarrollador 02 | 🔴 Pendiente |
| [rol_QA.md](docs/roles/rol_QA.md) | QA / Tester | 🔴 Pendiente |

> **Nota:** GitHub Actions verificará automáticamente que estos archivos existan y cumplan con el formato esperado en cada Pull Request. Los enlaces de arriba funcionarán una vez que cada miembro suba su archivo.
