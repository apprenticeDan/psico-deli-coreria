package com.psicodeli.core.dominio.producto;

public enum CategoriaProducto {
    CERVEZA("CER"),
    GASEOSA("GAS"),
    CIGARRILLO("CIG"),
    REFRESCO("REF"),
    TRAGO("TRA"),
    COMBO("COM");

    private final String prefijo;

    CategoriaProducto(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}

