package com.example.BeverageStockMaster.controller;

import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TipoBebidaControllerTest {

    @InjectMocks
    private TipoBebidaController tipoBebidaController;

    @Mock
    private TipoBebidaRepository tipoBebidaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarTipoBebidaTest() {
        TipoBebida tipoBebida = new TipoBebida();
        tipoBebida.setDescricao("Vinho");

        when(tipoBebidaRepository.save(tipoBebida)).thenReturn(tipoBebida);

        ResponseEntity<String> response = tipoBebidaController.criarTipoBebida(tipoBebida);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tipo de Bebida criado com sucesso.", response.getBody());
        verify(tipoBebidaRepository, times(1)).save(tipoBebida);
    }

    @Test
    void listarTiposBebidasTest() {
        List<TipoBebida> tiposBebidas = Arrays.asList(new TipoBebida(), new TipoBebida());

        when(tipoBebidaRepository.findAll()).thenReturn(tiposBebidas);

        ResponseEntity<List<TipoBebida>> response = tipoBebidaController.listarTiposBebidas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tiposBebidas, response.getBody());
        verify(tipoBebidaRepository, times(1)).findAll();
    }

    @Test
    void deletarTipoBebidaTest() {
        doNothing().when(tipoBebidaRepository).deleteById(1L);

        ResponseEntity<String> response = tipoBebidaController.deletarTipoBebida(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tipo de bebida deletado com sucesso.", response.getBody());
        verify(tipoBebidaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarTipoBebidaComRelacionamentoTest() {
        doThrow(new DataIntegrityViolationException("")).when(tipoBebidaRepository).deleteById(1L);

        ResponseEntity<String> response = tipoBebidaController.deletarTipoBebida(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro: Este tipo de bebida está associado a uma ou mais seções e não pode ser deletado.", response.getBody());
        verify(tipoBebidaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarTipoBebidaComErroGenericoTest() {
        doThrow(new RuntimeException("Erro inesperado")).when(tipoBebidaRepository).deleteById(1L);

        ResponseEntity<String> response = tipoBebidaController.deletarTipoBebida(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro ao tentar deletar o tipo de bebida.", response.getBody());
        verify(tipoBebidaRepository, times(1)).deleteById(1L);
    }
}
