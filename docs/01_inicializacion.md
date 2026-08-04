# Entorno de Desarrollo Fullstack (Java 21 + SolidJS + PostgreSQL)

Este directorio contiene la configuración necesaria para levantar un entorno de desarrollo completamente contenedorizado, unificado y listo para trabajar, garantizando que todo el equipo (independientemente de su sistema operativo o IDE) tenga exactamente las mismas dependencias.

## Estructura

- `base/`: Contiene el `Containerfile` base y los scripts para compilar la imagen compartida que tiene pre-instalado Java 21, Node.js, y las herramientas esenciales.
- `.devcontainer/`: Contiene la configuración del entorno en sí (`devcontainer.json` para VS Code) y 
- `compose.yml` para iniciar todo el stack junto a la base de datos PostgreSQL.

---

## 🚀 Pasos para Iniciar

### 1. Construir la Imagen Base (Se hace una sola vez)

Antes de iniciar el entorno por primera vez, cada desarrollador necesita construir la imagen localmente. Abre tu terminal, navega hasta la carpeta `base` y ejecuta el script correspondiente:

**En Linux o macOS:**
```bash
cd base
./build-java-base.sh 1.0.1
```

**En Windows:**
```cmd
cd base
build-java-base.cmd 1.0.1
```

> **Nota:** Este proceso tomará unos minutos mientras descarga Java y Node.js. Esto solo debe ejecutarse la primera vez, o cuando se agreguen dependencias globales nuevas al `Containerfile.java`.

### ✅ 1.5. Verificar la Imagen (Opcional)

Para asegurarte de que la imagen se construyó correctamente, puedes listar tus imágenes locales ejecutando:

```bash
# Si usas Podman
podman images

# Si usas Docker
docker images
```

Deberías ver en la lista la imagen `ing.sw/java21-solid-dev` con la etiqueta `1.0.1` y `latest`. 
**Dato importante:** Ese nombre exacto de la imagen es el que está configurado en el archivo `compose.yml` (y a su vez ligado al `.devcontainer.json`), asegurando que todo el equipo trabaje sobre exactamente la misma base.

### 2. Levantar el Entorno

Dependiendo del IDE que prefiera cada desarrollador, debe seguir **solo una** de las siguientes opciones:

#### Opción A: Usando VS Code + Devcontainers (Recomendado)
Esta opción te conectará directamente dentro del entorno aislado en Linux e instalará todas las extensiones de autocompletado para Spring Boot y Tailwind automáticamente.

1. Abre la carpeta del proyecto en **VS Code**.
2. Debería aparecer una notificación en la parte inferior derecha preguntando si deseas reabrir la carpeta en un contenedor (*"Reopen in Container"*). Haz clic ahí.
   - *Si no aparece, presiona `F1`, escribe "Dev Containers: Reopen in Container" y presiona Enter.*
3. VS Code lanzará automáticamente la base de datos y tu entorno, conectándote al interior.

#### Opción B: Usando cualquier otro IDE (IntelliJ, Neovim, WebStorm)
Si un miembro del equipo no usa Devcontainers, puede simplemente ejecutar los servicios y editar el código desde su propia máquina (Windows, Mac o Linux):

1. Navega en tu terminal a la carpeta del proyecto:
   ```bash
   cd psico-deli-coreria
   ```
2. Levanta los contenedores en segundo plano:
   ```bash
   podman compose up -d
   # Si usan Docker Desktop: docker compose up -d
   ```
3. Abre tu IDE favorito directamente en esa carpeta `psico-deli-coreria/`. Podrás editar los archivos libremente y conectarte a los puertos expuestos.

---

## 📦 Datos de Conexión a la Base de Datos

El entorno levanta automáticamente un contenedor con **PostgreSQL 16**. Los datos se guardan en un volumen local para no perderse cuando apagues el contenedor.

- **Host (desde la máquina local):** `localhost`
- **Host (desde Spring Boot / dentro del contenedor):** `db`
- **Puerto:** `5432`
- **Usuario:** `dev`
- **Contraseña:** `password`
- **Base de Datos:** `appdb`
