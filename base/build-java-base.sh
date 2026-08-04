#!/usr/bin/env bash

set -euo pipefail

VERSION="${1:?Uso: ./build-java-base.sh <version> [registro]}"

REGISTRY="${2:-ing.sw}"
IMAGE_NAME="java21-solid-dev"

FULL_IMAGE="${REGISTRY}/${IMAGE_NAME}"

echo "Construyendo ${FULL_IMAGE}:${VERSION}"

if command -v podman &> /dev/null; then
    DOCKER_CMD="podman"
else
    DOCKER_CMD="docker"
fi

$DOCKER_CMD build \
    -f Containerfile.java.base \
    --build-arg USERNAME=dev \
    -t "${FULL_IMAGE}:${VERSION}" \
    -t "${FULL_IMAGE}:latest" \
    .

echo
echo "Imagen creada:"
echo "  ${FULL_IMAGE}:${VERSION}"
echo "  ${FULL_IMAGE}:latest"
