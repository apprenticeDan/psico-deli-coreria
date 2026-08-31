# Reglas de Validación de Entrada y Restricciones Legales (Input Validation & QA Test Suite)

Este documento centraliza las reglas generales y reutilizables de validación de entradas de usuario, unicidad de datos y restricciones de ley en el sistema Psico-Deli Licorería.

---

## 🛡️ 1. Reglas Reutilizables de Validación de Entrada (`ValidadorInput`)

El paquete `com.psicodeli.core.dominio.validador.ValidadorInput` proporciona funciones puras de dominio para validar las entradas de la aplicación antes de ser procesadas o persistidas:

| Campo | Regla de Validación | Formato / Expresión Regular | Error Retornado |
|---|---|---|---|
| **Nombre Persona (`nombreCompleto`)** | Solo letras (incluidas tildes, ñ/Ñ), espacios, guiones y apóstrofes. **Rechaza estrictamente números y símbolos especiales**. | `^[a-zA-ZáéíóúÁÉÍÓÚñÑäëïöüÄËÏÖÜ\s'-]{2,100}$` | `FormatoInvalido("nombreCompleto", ...)` |
| **Nombre Usuario (`usuario`)** | Alfanumérico, puntos y guiones bajos. Sin espacios ni caracteres especiales. Longitud entre 3 y 30 caracteres. | `^[a-zA-Z0-9._]{3,30}$` | `FormatoInvalido("usuario", ...)` |
| **Contraseña (`password`)** | Longitud mínima de 8 caracteres. No vacía. Sin caracteres nulos o de control. | Longitud >= 8 | `FormatoInvalido("password", ...)` |
| **Cédula de Identidad (`cedulaIdentidad`)** | Entre 5 y 15 caracteres alfanuméricos con guion opcional y extensión departamental opcional. | `^[a-zA-Z0-9-]{5,15}(\s[a-zA-Z]{2,3})?$` | `FormatoInvalido("cedulaIdentidad", ...)` |
| **Teléfono (`telefono`)** | **Exclusivamente numérico** (7 a 15 dígitos) con prefijo '+' opcional. **Rechaza letras y símbolos especiales**. | `^\+?[0-9]{7,15}$` | `FormatoInvalido("telefono", ...)` |

---

## 🆔 2. Restricciones de Unicidad (`GestionTrabajadorService`)

Para preservar la integridad de los datos entre los trabajadores:
- **Cédula de Identidad Única**: Ningún trabajador puede registrarse con una Cédula de Identidad que ya pertenezca a otro trabajador activo en la base de datos (`ErrorDominio.CedulaIdentidadYaExiste`).
- **Nombre de Usuario Único**: El identificador de inicio de sesión debe ser único globalmente (`ErrorDominio.UsuarioYaExiste`).

---

## 🔞 3. Restricción Legal de Venta a Menores de Edad (`ValidadorVentaEdad`)

Conforme a la normativa legal vigente para la venta de productos regulados:
- **Categorías Restringidas**: `CIGARRILLO`, `CERVEZA`, `TRAGO`.
- **Regla de Edad**: La fecha de nacimiento del cliente se valida contra la fecha de la venta. Si la persona tiene menos de 18 años cumplidos al momento de la transacción, la venta es rechazada automáticamente con `ErrorDominio.VentaNoPermitidaMenorDeEdad`.
- **Categorías Permitidas para Menores**: `GASEOSA`, `REFRESCO`, `COMBO` (sin alcohol).

---

## 🧪 4. Suite de Pruebas Automáticas

La suite de pruebas en `backend/src/test/` incluye:
1. `ValidadorInputTest.java`: Verifica nombres válidos/inválidos con números/símbolos, usuarios, contraseñas, CIs y teléfonos numéricos.
2. `ValidadorVentaEdadTest.java`: Verifica la restricción de 18 años cumplidos exactamente para alcohol y cigarrillos, además de casos borde.
3. `UsuarioValidacionTest.java`: Pruebas de integración de dominio al crear trabajadores con inputs malformados.
4. `GestionTrabajadorServiceTest.java`: Pruebas de servicio verificando unicidad de CI y usuario.
5. `UsuarioFuncionesTest.java`: Pruebas de autenticación y permisos por rol.

### Ejecución de Pruebas
Dentro del contenedor de desarrollo:
```bash
podman exec psicodelicoreria_dev-env_1 bash -c "cd /workspace/backend && ./gradlew test"
```
