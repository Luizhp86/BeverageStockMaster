package com.example.BeverageStockMaster.service;

import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.repository.BebidaRepository;
import com.example.BeverageStockMaster.repository.HistoricoMovimentacaoRepository;
import com.example.BeverageStockMaster.repository.SecaoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    private final BebidaRepository bebidaRepository;
    private final SecaoRepository secaoRepository;
    private final HistoricoMovimentacaoRepository historicoRepository;

    @Autowired
    public EstoqueService(BebidaRepository bebidaRepository, SecaoRepository secaoRepository, HistoricoMovimentacaoRepository historicoRepository) {
        this.bebidaRepository = bebidaRepository;
        this.secaoRepository = secaoRepository;
        this.historicoRepository = historicoRepository;
    }

    public void registrarEntradaBebida(Bebida bebida, Long secaoId) {
        if (secaoId <= 0) {
            throw new IllegalArgumentException("O número da seção deve ser positivo.");
        }

        Secao secao = secaoRepository.findById(secaoId).orElse(null);

        if (secao == null) {
            // Se a seção não existir, crie uma nova seção com a capacidade padrão
            String nomeSecao = "Seção " + secaoId;
            secao = new Secao();
            secao.setNome(nomeSecao);
            secao.setCapacidadeAtual(0);
            secao.setCapacidadeMaxima(bebida.getTipoBebida() == Bebida.TipoBebida.ALCOOLICA ? 500 : 400);
            secaoRepository.save(secao);
        }

        // Atualizar a capacidade atual da seção
        double novaCapacidadeAtual = secao.getCapacidadeAtual() + bebida.getVolume();
        if (novaCapacidadeAtual > secao.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Capacidade máxima da seção excedida.");
        }

        secao.setCapacidadeAtual(novaCapacidadeAtual);
        bebida.setSecao(secao);
        bebidaRepository.save(bebida);
        secaoRepository.save(secao);

        registrarHistorico("ENTRADA", bebida.getVolume(), secao, "responsável");
    }

    public void registrarSaidaBebida(Bebida bebida, Long secaoId) {
        if (secaoId <= 0) {
            throw new IllegalArgumentException("O número da seção deve ser positivo.");
        }

        Secao secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));

        if (!secao.equals(bebida.getSecao())) {
            throw new IllegalArgumentException("A bebida não pertence a essa seção.");
        }

        // Atualizar a capacidade atual da seção
        double novaCapacidadeAtual = secao.getCapacidadeAtual() - bebida.getVolume();
        secao.setCapacidadeAtual(novaCapacidadeAtual);
        bebidaRepository.delete(bebida);
        secaoRepository.save(secao);

        registrarHistorico("SAIDA", bebida.getVolume(), secao, "responsável");
    }

    private void registrarHistorico(String tipo, double volume, Secao secao, String responsavel) {
        HistoricoMovimentacao historico = new HistoricoMovimentacao(
                LocalDateTime.now(),
                tipo,
                volume,
                secao,
                responsavel
        );
        historicoRepository.save(historico);
    }

    public double consultarVolumeTotalPorTipo(Bebida.TipoBebida tipoBebida) {
        return bebidaRepository.findAll().stream()
                .filter(bebida -> bebida.getTipoBebida().equals(tipoBebida))
                .mapToDouble(Bebida::getVolume)
                .sum();
    }

    public List<Bebida> consultarBebidasPorSecao(Long secaoId) {
        Secao secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));
        return bebidaRepository.findBySecao(secao);
    }

    public List<Secao> consultarSecoesDisponiveis(double volume) {
        return secaoRepository.findAll().stream()
                .filter(secao -> secao.getCapacidadeMaxima() - secao.getCapacidadeAtual() >= volume)
                .collect(Collectors.toList());
    }

    public double consultarVolumeTotalPorTipoESecao(Bebida.TipoBebida tipoBebida, Long secaoId) {
        return bebidaRepository.findAll().stream()
                .filter(bebida -> bebida.getTipoBebida().equals(tipoBebida) &&
                        (secaoId == null || (bebida.getSecao() != null && bebida.getSecao().getId().equals(secaoId))))
                .mapToDouble(Bebida::getVolume)
                .sum();
    }

    public List<Object[]> consultarQuantidadePorSecao() {
        return bebidaRepository.findQuantidadePorSecaoETipo();
    }

    public List<Bebida> consultarTodasBebidas() {
        return bebidaRepository.findAll();
    }

    public void deletarBebida(Long bebidaId) {
        Bebida bebida = bebidaRepository.findById(bebidaId)
                .orElseThrow(() -> new IllegalArgumentException("Bebida não encontrada"));

        Secao secao = bebida.getSecao();
        if (secao != null) {
            // Atualizar a capacidade atual da seção
            double novaCapacidadeAtual = secao.getCapacidadeAtual() - bebida.getVolume();
            secao.setCapacidadeAtual(novaCapacidadeAtual);
            secaoRepository.save(secao);
        }

        // Registrar a exclusão no histórico de movimentações
        registrarHistorico("EXCLUSAO", bebida.getVolume(), secao, "responsável");

        // Excluir a bebida
        bebidaRepository.delete(bebida);
    }

    public List<Object[]> consultarHistoricoMovimentacoes() {
        return historicoRepository.findHistoricoAgrupadoPorDataETipo();
    }

    public List<Secao> consultarTodasSecoes() {
        return secaoRepository.findAll();
    }

}
