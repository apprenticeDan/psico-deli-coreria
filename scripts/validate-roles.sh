#!/usr/bin/env bash
# =================================================================
# Script de validación de archivos de roles del equipo.
# Se ejecuta en GitHub Actions para verificar que cada miembro
# haya subido su archivo con el formato correcto.
# =================================================================

set -euo pipefail

ROLES_DIR="docs/roles"
REQUIRED_FILES=(
  "rol_lider.md"
  "rol_analista01.md"
  "rol_analista02.md"
  "rol_dev01.md"
  "rol_dev02.md"
  "rol_QA.md"
)

REQUIRED_SECTIONS=(
  "## Datos del Miembro"
  "## Descripción del Rol"
  "## Responsabilidades"
)

ERRORS=0
WARNINGS=0

echo "========================================"
echo "  Validación de Archivos de Roles"
echo "========================================"
echo ""

# --- Paso 1: Verificar que todos los archivos existan ---
echo "--- Verificando existencia de archivos ---"
MISSING=0
for file in "${REQUIRED_FILES[@]}"; do
  filepath="${ROLES_DIR}/${file}"
  if [ ! -f "$filepath" ]; then
    echo "❌ FALTA: ${filepath}"
    MISSING=$((MISSING + 1))
    ERRORS=$((ERRORS + 1))
  else
    echo "✅ Existe: ${filepath}"
  fi
done
echo ""

if [ "$MISSING" -gt 0 ]; then
  echo "⚠️  Faltan ${MISSING} archivo(s). El equipo aún no ha completado todos los roles."
  echo ""
fi

# --- Paso 2: Validar formato de los archivos que SÍ existen ---
echo "--- Validando formato de archivos existentes ---"
for file in "${REQUIRED_FILES[@]}"; do
  filepath="${ROLES_DIR}/${file}"
  if [ ! -f "$filepath" ]; then
    continue
  fi

  echo ""
  echo "Revisando: ${filepath}"
  FILE_OK=true

  # Verificar que contenga las secciones requeridas
  for section in "${REQUIRED_SECTIONS[@]}"; do
    if ! grep -qF "$section" "$filepath"; then
      echo "  ❌ Falta la sección: '${section}'"
      ERRORS=$((ERRORS + 1))
      FILE_OK=false
    fi
  done

  # Verificar que no queden placeholders sin llenar [...]
  PLACEHOLDERS=$(grep -cE '\[.*\]' "$filepath" || true)
  # Descontar los que son enlaces markdown legítimos (ej: [texto](url))
  LEGIT_LINKS=$(grep -cE '\[.*\]\(.*\)' "$filepath" || true)
  UNFILLED=$((PLACEHOLDERS - LEGIT_LINKS))
  if [ "$UNFILLED" -gt 0 ]; then
    echo "  ⚠️  Posibles campos sin llenar (${UNFILLED} coincidencias con '[...]'). Revisar manualmente."
    WARNINGS=$((WARNINGS + 1))
  fi

  if $FILE_OK; then
    echo "  ✅ Formato correcto"
  fi
done

echo ""
echo "========================================"
echo "  Resumen"
echo "========================================"
echo "  Errores:      ${ERRORS}"
echo "  Advertencias: ${WARNINGS}"
echo "========================================"

if [ "$ERRORS" -gt 0 ]; then
  echo ""
  echo "❌ FALLÓ la validación. Corrige los errores antes de hacer merge."
  exit 1
else
  echo ""
  echo "✅ Validación completada exitosamente."
  exit 0
fi
