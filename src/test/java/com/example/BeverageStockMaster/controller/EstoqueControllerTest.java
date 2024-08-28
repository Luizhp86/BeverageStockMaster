package com.example.BeverageStockMaster.controller;

import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import com.example.BeverageStockMaster.service.EstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EstoqueControllerTest {

    @InjectMocks
    private EstoqueController estoqueController;

    @Mock
    private EstoqueService estoqueService;

    @Mock
    private TipoBebidaRepository tipoBebidaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarEntradaTest() {
        Bebida bebida = new Bebida();
        bebida.setTipoBebida(new TipoBebida());
        bebida.getTipoBebida().setId(1L);

        when(tipoBebidaRepository.findById(1L)).thenReturn(Optional.of(bebida.getTipoBebida()));
        doNothing().when(estoqueService).registrarEntradaBebida(bebida, 1L, "Responsável");

        ResponseEntity<String> response = estoqueController.registrarEntrada(bebida, 1L, "Responsável");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Entrada registrada com sucesso.", response.getBody());
    }

    @Test
    void registrarEntradaTipoBebidaNaoEncontradoTest() {
        Bebida bebida = new Bebida();
        bebida.setTipoBebida(new TipoBebida());
        bebida.getTipoBebida().setId(1L);

        when(tipoBebidaRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = estoqueController.registrarEntrada(bebida, 1L, "Responsável");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Tipo de Bebida não encontrado", response.getBody());
    }

    @Test
    void registrarSaidaTest() {
        Bebida bebida = new Bebida();
        bebida.setVolume(10.0);

        doNothing().when(estoqueService).registrarSaidaBebida(bebida, 1L, "Responsável");

        ResponseEntity<?> response = estoqueController.registrarSaida(bebida, 1L, "Responsável");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bebida removida com sucesso da seção 1", response.getBody());
    }

    @Test
    void registrarSaidaVolumeInvalidoTest() {
        Bebida bebida = new Bebida();
        bebida.setVolume(0.0);

        ResponseEntity<?> response = estoqueController.registrarSaida(bebida, 1L, "Responsável");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O volume deve ser maior que zero.", response.getBody());
    }

    @Test
    void consultarVolumeTotalTest() {
        when(estoqueService.consultarVolumeTotal()).thenReturn(100.0);

        ResponseEntity<Double> response = estoqueController.consultarVolumeTotal();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100.0, response.getBody());
    }

    @Test
    void consultarBebidasPorSecaoTest() {
        List<Bebida> bebidas = Arrays.asList(new Bebida(), new Bebida());

        when(estoqueService.consultarBebidasPorSecao(1L)).thenReturn(bebidas);

        ResponseEntity<List<Bebida>> response = estoqueController.consultarBebidasPorSecao(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bebidas, response.getBody());
    }

    @Test
    void consultarSecoesDisponiveisTest() {
        List<Secao> secoes = Arrays.asList(new Secao(), new Secao());

        when(estoqueService.consultarSecoesDisponiveis(1L, 10.0)).thenReturn(secoes);

        ResponseEntity<List<Secao>> response = estoqueController.consultarSecoesDisponiveis(1L, 10.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(secoes, response.getBody());
    }

    @Test
    void consultarTodasBebidasTest() {
        List<Bebida> bebidas = Arrays.asList(new Bebida(), new Bebida());

        when(estoqueService.consultarTodasBebidas()).thenReturn(bebidas);

        ResponseEntity<List<Bebida>> response = estoqueController.consultarTodasBebidas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bebidas, response.getBody());
    }

    @Test
    void consultarHistoricoMovimentacoesTest() {
        List<Object[]> historico = Arrays.asList(new Object[]{}, new Object[]{});

        when(estoqueService.consultarHistoricoMovimentacoes()).thenReturn(historico);

        ResponseEntity<List<Object[]>> response = estoqueController.consultarHistoricoMovimentacoes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(historico, response.getBody());
    }

    @Test
    void consultarLocaisDisponiveisTest() {
        List<Secao> secoes = Arrays.asList(new Secao(), new Secao());

        when(estoqueService.consultarLocaisDisponiveisParaVolume(10.0)).thenReturn(secoes);

        ResponseEntity<List<Secao>> response = estoqueController.consultarLocaisDisponiveis(10.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(secoes, response.getBody());
    }

    @Test
    void consultarSecoesDisponiveisParaVendaTest() {
        List<Secao> secoes = Arrays.asList(new Secao(), new Secao());

        when(estoqueService.consultarSecoesDisponiveisParaVenda(1L)).thenReturn(secoes);

        ResponseEntity<List<Secao>> response = estoqueController.consultarSecoesDisponiveisParaVenda(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(secoes, response.getBody());
    }
}
