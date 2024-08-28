package com.example.BeverageStockMaster.service;

import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.BebidaRepository;
import com.example.BeverageStockMaster.repository.HistoricoMovimentacaoRepository;
import com.example.BeverageStockMaster.repository.SecaoRepository;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private BebidaRepository bebidaRepository;

    @Mock
    private SecaoRepository secaoRepository;

    @Mock
    private HistoricoMovimentacaoRepository historicoRepository;

    @Mock
    private TipoBebidaRepository tipoBebidaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarEntradaBebidaTest() {
        Bebida bebida = new Bebida();
        bebida.setVolume(50);
        TipoBebida tipoBebida = new TipoBebida();
        tipoBebida.setId(1L);
        tipoBebida.setCapacidadeMaxima(100);
        bebida.setTipoBebida(tipoBebida);

        Secao secao = new Secao();
        secao.setId(1L);
        secao.setUtilizacaoTotal(30);

        when(secaoRepository.findById(1L)).thenReturn(Optional.of(secao));
        when(tipoBebidaRepository.findById(1L)).thenReturn(Optional.of(tipoBebida));
        when(bebidaRepository.findBySecao(secao)).thenReturn(Collections.emptyList());

        estoqueService.registrarEntradaBebida(bebida, 1L, "Responsável");

        verify(secaoRepository, times(1)).save(secao);
        verify(bebidaRepository, times(1)).save(bebida);
        verify(historicoRepository, times(1)).save(any(HistoricoMovimentacao.class));
    }

    @Test
    void registrarEntradaBebidaSecaoNaoEncontradaTest() {
        Bebida bebida = new Bebida();
        when(secaoRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estoqueService.registrarEntradaBebida(bebida, 1L, "Responsável");
        });

        assertEquals("Seção não encontrada", exception.getMessage());
    }

    @Test
    void registrarEntradaBebidaTipoNaoEncontradoTest() {
        Bebida bebida = new Bebida();
        bebida.setTipoBebida(new TipoBebida());
        bebida.getTipoBebida().setId(1L);

        Secao secao = new Secao();
        secao.setId(1L);

        when(secaoRepository.findById(1L)).thenReturn(Optional.of(secao));
        when(tipoBebidaRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estoqueService.registrarEntradaBebida(bebida, 1L, "Responsável");
        });

        assertEquals("Tipo de Bebida não encontrado", exception.getMessage());
    }



    @Test
    void registrarSaidaBebidaSecaoNaoEncontradaTest() {
        Bebida bebida = new Bebida();
        when(secaoRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estoqueService.registrarSaidaBebida(bebida, 1L, "Responsável");
        });

        assertEquals("Seção não encontrada", exception.getMessage());
    }

    @Test
    void registrarSaidaBebidaSecaoInvalidaTest() {
        Bebida bebida = new Bebida();
        Secao secao = new Secao();
        secao.setId(1L);
        bebida.setSecao(secao);

        Secao secaoErrada = new Secao();
        secaoErrada.setId(2L);

        when(secaoRepository.findById(2L)).thenReturn(Optional.of(secaoErrada));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estoqueService.registrarSaidaBebida(bebida, 2L, "Responsável");
        });

        assertEquals("A bebida não pertence a essa seção.", exception.getMessage());
    }

    @Test
    void consultarBebidasPorSecaoTest() {
        Secao secao = new Secao();
        secao.setId(1L);

        List<Bebida> bebidas = Arrays.asList(new Bebida(), new Bebida());

        when(secaoRepository.findById(1L)).thenReturn(Optional.of(secao));
        when(bebidaRepository.findBySecao(secao)).thenReturn(bebidas);

        List<Bebida> resultado = estoqueService.consultarBebidasPorSecao(1L);

        assertEquals(2, resultado.size());
        verify(bebidaRepository, times(1)).findBySecao(secao);
    }

    @Test
    void consultarSecoesDisponiveisTest() {
        TipoBebida tipoBebida = new TipoBebida();
        tipoBebida.setId(1L);
        tipoBebida.setCapacidadeMaxima(100);

        Secao secao1 = new Secao();
        secao1.setUtilizacaoTotal(30);
        Secao secao2 = new Secao();
        secao2.setUtilizacaoTotal(50);

        List<Secao> secoes = Arrays.asList(secao1, secao2);

        when(tipoBebidaRepository.findById(1L)).thenReturn(Optional.of(tipoBebida));
        when(secaoRepository.findAll()).thenReturn(secoes);

        List<Secao> resultado = estoqueService.consultarSecoesDisponiveis(1L, 20.0);

        assertEquals(2, resultado.size());
    }

    @Test
    void consultarVolumeTotalTest() {
        when(bebidaRepository.findAll()).thenReturn(Arrays.asList(
                new Bebida() {{ setVolume(50.0); }},
                new Bebida() {{ setVolume(30.0); }}
        ));

        double volumeTotal = estoqueService.consultarVolumeTotal();

        assertEquals(80.0, volumeTotal);
    }

    @Test
    void consultarQuantidadePorSecaoTest() {
        List<Object[]> quantidades = Arrays.asList(
                new Object[]{"Seção 1", "Tipo 1", 50.0},
                new Object[]{"Seção 2", "Tipo 2", 30.0}
        );

        when(bebidaRepository.findQuantidadePorSecaoETipo()).thenReturn(quantidades);

        List<Object[]> resultado = estoqueService.consultarQuantidadePorSecao();

        assertEquals(2, resultado.size());
        assertEquals("Seção 1", resultado.get(0)[0]);
        assertEquals("Seção 2", resultado.get(1)[0]);
    }

    @Test
    void consultarTodasBebidasTest() {
        List<Bebida> bebidas = Arrays.asList(new Bebida(), new Bebida());

        when(bebidaRepository.findAll()).thenReturn(bebidas);

        List<Bebida> resultado = estoqueService.consultarTodasBebidas();

        assertEquals(2, resultado.size());
        verify(bebidaRepository, times(1)).findAll();
    }




    @Test
    void consultarSecoesDisponiveisParaVendaTest() {
        TipoBebida tipoBebida = new TipoBebida();
        tipoBebida.setId(1L);
        tipoBebida.setCapacidadeMaxima(100.0);

        Secao secao = new Secao();
        secao.setId(1L);
        secao.setUtilizacaoTotal(50.0);

        Bebida bebida = new Bebida();
        bebida.setVolume(50.0);
        bebida.setTipoBebida(tipoBebida);
        bebida.setSecao(secao);

        when(secaoRepository.findAll()).thenReturn(Arrays.asList(secao));
        when(bebidaRepository.findBySecao(secao)).thenReturn(Arrays.asList(bebida));

        List<Secao> secoesDisponiveis = estoqueService.consultarSecoesDisponiveisParaVenda(1L);

        assertFalse(secoesDisponiveis.isEmpty());
        assertEquals(50.0, secoesDisponiveis.get(0).getCapacidadeDisponivel());
    }
}
