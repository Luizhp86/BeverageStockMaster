package com.example.BeverageStockMaster.controller;

import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.repository.SecaoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SecaoControllerTest {

    @InjectMocks
    private SecaoController secaoController;

    @Mock
    private SecaoRepository secaoRepository;

    @Mock
    private TipoBebidaRepository tipoBebidaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarSecaoTest() {
        Secao secao = new Secao();
        secao.setNome("Seção 1");

        when(secaoRepository.save(secao)).thenReturn(secao);

        ResponseEntity<String> response = secaoController.criarSecao(secao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Seção criada com sucesso.", response.getBody());
        verify(secaoRepository, times(1)).save(secao);
    }

    @Test
    void listarSecoesTest() {
        List<Secao> secoes = Arrays.asList(new Secao(), new Secao());

        when(secaoRepository.findAll()).thenReturn(secoes);

        ResponseEntity<List<Secao>> response = secaoController.listarSecoes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(secoes, response.getBody());
        verify(secaoRepository, times(1)).findAll();
    }

    @Test
    void deletarSecaoTest() {
        doNothing().when(secaoRepository).deleteById(1L);

        ResponseEntity<String> response = secaoController.deletarSecao(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Seção deletada com sucesso.", response.getBody());
        verify(secaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarSecaoComBebidasTest() {
        doThrow(new DataIntegrityViolationException("")).when(secaoRepository).deleteById(1L);

        ResponseEntity<String> response = secaoController.deletarSecao(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro: Não é possível deletar uma seção que contém bebidas cadastradas.", response.getBody());
        verify(secaoRepository, times(1)).deleteById(1L);
    }
}
