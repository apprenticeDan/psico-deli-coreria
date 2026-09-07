package com.psicodeli.core.aplicacion.producto;

import com.psicodeli.core.dominio.compartido.Result;
import com.psicodeli.core.dominio.producto.*;
import com.psicodeli.core.infraestructura.persistencia.entidades.ProductoJpaEntity;
import com.psicodeli.core.infraestructura.persistencia.repositorios.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GestionProductoServiceTest {

    private ProductoRepository productoRepository;
    private GestionProductoService gestionProductoService;

    @BeforeEach
    void setUp() {
        productoRepository = Mockito.mock(ProductoRepository.class);
        gestionProductoService = new GestionProductoService(productoRepository);
    }

    @Test
    void generarSiguienteCodigo_sinCodigosPrevios_retorna0001() {
        when(productoRepository.findCodigosByPrefijo("CER-")).thenReturn(List.of());

        String codigo = gestionProductoService.generarSiguienteCodigo(CategoriaProducto.CERVEZA);

        assertEquals("CER-0001", codigo);
    }

    @Test
    void generarSiguienteCodigo_conCodigosExistentes_incrementaMaximoCorrelativo() {
        when(productoRepository.findCodigosByPrefijo("CER-"))
                .thenReturn(List.of("CER-0001", "CER-0005", "CER-0003"));

        String codigo = gestionProductoService.generarSiguienteCodigo(CategoriaProducto.CERVEZA);

        assertEquals("CER-0006", codigo);
    }

    @Test
    void generarSiguienteCodigo_conCodigosNoEstandar_ignoraFormatoInvalido() {
        when(productoRepository.findCodigosByPrefijo("CIG-"))
                .thenReturn(List.of("CIG-0002", "CIG-CAMEL", "CIG-0004"));

        String codigo = gestionProductoService.generarSiguienteCodigo(CategoriaProducto.CIGARRILLO);

        assertEquals("CIG-0005", codigo);
    }

    @Test
    void registrarProducto_conCodigoNulo_autogeneraSiguienteCorrelativoYPersiste() {
        when(productoRepository.findCodigosByPrefijo("CER-")).thenReturn(List.of("CER-0001"));
        when(productoRepository.save(any(ProductoJpaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Presentacion pres = new Presentacion("Botella", new BigDecimal("620"), UnidadMedida.ML);

        Result<Producto, ?> resultado = gestionProductoService.registrarProducto(
                null,
                "Huari Tradicional",
                "CBN",
                CategoriaProducto.CERVEZA,
                pres,
                new BigDecimal("18.00"),
                30,
                Optional.empty()
        );

        assertTrue(resultado.isOk());
        Producto p = resultado.getValue();
        assertEquals("CER-0002", p.codigo());
        assertNotNull(p.id());

        ArgumentCaptor<ProductoJpaEntity> captor = ArgumentCaptor.forClass(ProductoJpaEntity.class);
        verify(productoRepository).save(captor.capture());
        assertEquals("CER-0002", captor.getValue().getCodigo());
        assertEquals(p.id(), captor.getValue().getId());
    }
}
