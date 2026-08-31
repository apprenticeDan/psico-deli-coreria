package com.psicodeli.core.dominio.compartido;

public sealed interface ErrorDominio {
    record CredencialesInvalidas() implements ErrorDominio {}
    record TrabajadorNoEncontrado() implements ErrorDominio {}
    record TrabajadorInactivo() implements ErrorDominio {}
    record OperacionNoAutorizada(String accion) implements ErrorDominio {}
    record ValorInvalido(String mensaje) implements ErrorDominio {}
    
    // Errores de Dominio para Productos y Cigarrillos (Documento F - Sección 4)
    record ProductoNoEncontrado() implements ErrorDominio {}
    record PrecioInvalido() implements ErrorDominio {}
    record CigarrilloInvalido() implements ErrorDominio {}
    record CajetillaInsuficiente() implements ErrorDominio {}

    // Errores de Dominio para Validación de Inputs, Unicidad y Restricción de Edad
    record CedulaIdentidadYaExiste(String ci) implements ErrorDominio {}
    record UsuarioYaExiste(String usuario) implements ErrorDominio {}
    record VentaNoPermitidaMenorDeEdad(int edadCalculada, String categoria) implements ErrorDominio {}
    record FormatoInvalido(String campo, String razon) implements ErrorDominio {}
}

