package com.psicodeli.core.dominio.compartido;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

/**
 * Utilidad de dominio para generar UUIDv7 secuenciales utilizando la biblioteca estándar
 * y probada 'java-uuid-generator' de FasterXML (RFC 9562).
 */
public final class UuidV7 {

    private static final TimeBasedEpochRandomGenerator GENERATOR = Generators.timeBasedEpochRandomGenerator();

    private UuidV7() {}

    public static UUID generar() {
        return GENERATOR.generate();
    }
}
