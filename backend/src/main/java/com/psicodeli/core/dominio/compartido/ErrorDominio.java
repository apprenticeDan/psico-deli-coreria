package com.psicodeli.core.dominio.compartido;

public sealed interface ErrorDominio {
    record CredencialesInvalidas() implements ErrorDominio {}
    record TrabajadorNoEncontrado() implements ErrorDominio {}
    record TrabajadorInactivo() implements ErrorDominio {}
    record OperacionNoAutorizada(String accion) implements ErrorDominio {}
    record ValorInvalido(String mensaje) implements ErrorDominio {}
}
