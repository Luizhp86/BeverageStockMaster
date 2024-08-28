package com.example.BeverageStockMaster.service;
import com.example.BeverageStockMaster.domain.Bebida;
import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.example.BeverageStockMaster.domain.Secao;
import com.example.BeverageStockMaster.domain.TipoBebida;
import com.example.BeverageStockMaster.repository.BebidaRepository;
import com.example.BeverageStockMaster.repository.HistoricoMovimentacaoRepository;
import com.example.BeverageStockMaster.repository.SecaoRepository;
import com.example.BeverageStockMaster.repository.TipoBebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    @Autowired
    BebidaRepository bebidaRepository;

    @Autowired
    SecaoRepository secaoRepository;

    @Autowired
    HistoricoMovimentacaoRepository historicoRepository;

    @Autowired
    TipoBebidaRepository tipoBebidaRepository;

    public void registrarEntradaBebida(Bebida bebida, Long secaoId, String responsavel) {
        Secao secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));

        TipoBebida tipoBebida = tipoBebidaRepository.findById(bebida.getTipoBebida().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));

        List<Bebida> bebidasNaSecao = bebidaRepository.findBySecao(secao);
        if (!bebidasNaSecao.isEmpty()) {
            TipoBebida tipoBebidaNaSecao = bebidasNaSecao.get(0).getTipoBebida();
            if (!tipoBebidaNaSecao.equals(tipoBebida)) {
                throw new IllegalArgumentException("Não é permitido cadastrar uma bebida de tipo diferente na mesma seção.");
            }

        } else {

            LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
            LocalDateTime fimDoDia = LocalDate.now().plusDays(1).atStartOfDay();

            List<HistoricoMovimentacao> historicoMovimentacaos = historicoRepository.findBySecaoAndTipoMovimentacaoNoMesmoDia(
                    secao.getNome(), "SAÍDA", inicioDoDia, fimDoDia
            );

            boolean restricaoHoje = false; // Inicialmente, assumimos que não há restrição

            for (HistoricoMovimentacao movimentacao : historicoMovimentacaos) {
                Optional<TipoBebida> verificacaoBebida = tipoBebidaRepository.findByDescricaoAndRestricaoQuarentena(movimentacao.getTipoBebidaNome(), true);

                if (verificacaoBebida.isPresent() && verificacaoBebida.get().isRestricaoQuarentena() && !verificacaoBebida.get().equals(tipoBebida)) {
                    restricaoHoje = true;
                    break;
                }
            }

            if (restricaoHoje) {
                throw new IllegalArgumentException("Cadastro bloqueado devido à restrição de quarentena em saídas de outras bebidas hoje. Só será possível cadastrar outro tipo de bebida nesta seção no próximo dia.");
            }
        }

        double novaCapacidadeAtual = secao.getUtilizacaoTotal() + bebida.getVolume();
        if (novaCapacidadeAtual > tipoBebida.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Capacidade máxima para esse tipo de bebida excedida.");
        }

        secao.setUtilizacaoTotal(novaCapacidadeAtual);
        bebida.setSecao(secao);
        bebida.setDataEntrada(LocalDate.now());
        bebida.setResponsavel(responsavel);

        secaoRepository.save(secao);
        bebidaRepository.save(bebida);
        registrarHistorico("ENTRADA", bebida.getVolume(), secao.getNome(), responsavel, tipoBebida.getDescricao());
    }

    public void registrarSaidaBebida(Bebida bebida, Long secaoId, String responsavel) {
        if (secaoId <= 0) {
            throw new IllegalArgumentException("O número da seção deve ser positivo.");
        }

        Secao secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));

        if (!secao.equals(bebida.getSecao())) {
            throw new IllegalArgumentException("A bebida não pertence a essa seção.");
        }

        double novaCapacidadeAtual = secao.getUtilizacaoTotal() - bebida.getVolume();
        secao.setUtilizacaoTotal(novaCapacidadeAtual);
        bebidaRepository.delete(bebida);
        secaoRepository.save(secao);

        registrarHistorico("SAIDA", bebida.getVolume(), secao.getNome(), responsavel, bebida.getTipoBebida().getDescricao());
    }

    private void registrarHistorico(String tipo, double volume, String secaoNome, String responsavel, String tipoBebidaNome) {
        HistoricoMovimentacao historico = new HistoricoMovimentacao(
                LocalDateTime.now(),
                tipo,
                volume,
                secaoNome,
                responsavel,
                tipoBebidaNome
        );
        historicoRepository.save(historico);
    }


    public List<Bebida> consultarBebidasPorSecao(Long secaoId) {
        Secao secao = secaoRepository.findById(secaoId)
                .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));
        return bebidaRepository.findBySecao(secao);
    }

    public List<Secao> consultarSecoesDisponiveis(Long tipoBebidaId, double volume) {
        TipoBebida tipoBebida = tipoBebidaRepository.findById(tipoBebidaId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));

        return secaoRepository.findAll().stream()
                .filter(secao -> (secao.getUtilizacaoTotal() + volume) <= tipoBebida.getCapacidadeMaxima())
                .collect(Collectors.toList());
    }

    public double consultarVolumeTotal() {
        return bebidaRepository.findAll().stream()
                .mapToDouble(Bebida::getVolume)
                .sum();
    }

    public List<Object[]> consultarQuantidadePorSecao() {
        return bebidaRepository.findQuantidadePorSecaoETipo();
    }

    public List<Bebida> consultarTodasBebidas() {
        return bebidaRepository.findAll();
    }

    public void saidaBebida(Long bebidaId, String responsavel) {
        Bebida bebida = bebidaRepository.findById(bebidaId)
                .orElseThrow(() -> new IllegalArgumentException("Bebida não encontrada"));

        Secao secao = bebida.getSecao();
        if (secao != null) {
            double novaCapacidadeAtual = secao.getUtilizacaoTotal() - bebida.getVolume();
            secao.setUtilizacaoTotal(novaCapacidadeAtual);
            secaoRepository.save(secao);
        }

        registrarHistorico("SAÍDA", bebida.getVolume(), secao.getNome(), responsavel, bebida.getTipoBebida().getDescricao());

        bebidaRepository.delete(bebida);
    }

    public List<Object[]> consultarHistoricoMovimentacoes() {
        return historicoRepository.findHistoricoAgrupadoPorDataETipo();
    }

    public List<Secao> consultarTodasSecoes() {
        return secaoRepository.findAll();
    }

    public List<Secao> consultarLocaisDisponiveisParaVolume(double volume) {
        return secaoRepository.findAll().stream()
                .filter(secao -> {
                    List<Bebida> bebidas = bebidaRepository.findBySecaoId(secao.getId());

                    if (bebidas.isEmpty()) {
                        TipoBebida maiorCapacidadeTipo = tipoBebidaRepository.findAll().stream()
                                .max(Comparator.comparingDouble(TipoBebida::getCapacidadeMaxima))
                                .orElseThrow(() -> new IllegalArgumentException("Nenhum tipo de bebida disponível."));
                        secao.setCapacidadeDisponivel(maiorCapacidadeTipo.getCapacidadeMaxima());
                        secao.setTipoBebidaUtilizada(maiorCapacidadeTipo.getDescricao());
                        return volume <= maiorCapacidadeTipo.getCapacidadeMaxima();
                    } else {
                        TipoBebida tipoBebidaNaSecao = bebidas.get(0).getTipoBebida();
                        double capacidadeAtualTotal = bebidas.stream()
                                .mapToDouble(Bebida::getVolume)
                                .sum();
                        double capacidadeDisponivel = tipoBebidaNaSecao.getCapacidadeMaxima() - capacidadeAtualTotal;
                        secao.setCapacidadeDisponivel(capacidadeDisponivel);
                        secao.setTipoBebidaUtilizada(tipoBebidaNaSecao.getDescricao());
                        return capacidadeDisponivel >= volume;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Secao> consultarSecoesDisponiveisParaVenda(Long tipoBebidaId) {
        return secaoRepository.findAll().stream()
                .filter(secao -> {

                    List<Bebida> bebidasDoTipo = bebidaRepository.findBySecao(secao).stream()
                            .filter(bebida -> bebida.getTipoBebida().getId().equals(tipoBebidaId))
                            .collect(Collectors.toList());

                    if (bebidasDoTipo.isEmpty()) {
                        return false;
                    }

                    double capacidadeAtualTotal = bebidasDoTipo.stream()
                            .mapToDouble(Bebida::getVolume)
                            .sum();

                    TipoBebida tipoBebida = bebidasDoTipo.get(0).getTipoBebida();
                    double capacidadeDisponivel = tipoBebida.getCapacidadeMaxima() - capacidadeAtualTotal;

                    secao.setCapacidadeDisponivel(capacidadeDisponivel);

                    return capacidadeDisponivel > 0;
                })
                .collect(Collectors.toList());
    }


}
