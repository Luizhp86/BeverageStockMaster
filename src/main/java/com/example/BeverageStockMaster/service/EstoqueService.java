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
    import java.util.List;
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

            // Verifica se o tipo de bebida respeita a capacidade máxima para este tipo
            double novaCapacidadeAtual = secao.getCapacidadeAtual() + bebida.getVolume();
            if (novaCapacidadeAtual > tipoBebida.getCapacidadeMaxima()) {
                throw new IllegalArgumentException("Capacidade máxima para esse tipo de bebida excedida.");
            }

            secao.setCapacidadeAtual(novaCapacidadeAtual);
            bebida.setSecao(secao);
            bebida.setDataEntrada(LocalDate.now());
            bebida.setResponsavel(responsavel);

            secaoRepository.save(secao);
            bebidaRepository.save(bebida);
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

            // Atualizar a capacidade atual da seção
            double novaCapacidadeAtual = secao.getCapacidadeAtual() - bebida.getVolume();
            secao.setCapacidadeAtual(novaCapacidadeAtual);
            bebidaRepository.delete(bebida);
            secaoRepository.save(secao);

            registrarHistorico("SAIDA", bebida.getVolume(), secao.getNome(), responsavel);
        }

        private void registrarHistorico(String tipo, double volume, String secaoNome, String responsavel) {
            HistoricoMovimentacao historico = new HistoricoMovimentacao(
                    LocalDateTime.now(),
                    tipo,
                    volume,
                    secaoNome,
                    responsavel
            );
            historicoRepository.save(historico);
        }

        public double consultarVolumeTotalPorTipo(TipoBebida tipoBebida) {
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

        public List<Secao> consultarSecoesDisponiveis(Long tipoBebidaId, double volume) {
            TipoBebida tipoBebida = tipoBebidaRepository.findById(tipoBebidaId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));

            return secaoRepository.findAll().stream()
                    .filter(secao -> (secao.getCapacidadeAtual() + volume) <= tipoBebida.getCapacidadeMaxima())
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
                // Atualizar a capacidade atual da seção
                double novaCapacidadeAtual = secao.getCapacidadeAtual() - bebida.getVolume();
                secao.setCapacidadeAtual(novaCapacidadeAtual);
                secaoRepository.save(secao);
            }

            // Registrar a exclusão no histórico de movimentações
            registrarHistorico("SAÍDA", bebida.getVolume(), secao.getNome(), responsavel);

            // Excluir a bebida
            bebidaRepository.delete(bebida);
        }

        public List<Object[]> consultarHistoricoMovimentacoes() {
            return historicoRepository.findHistoricoAgrupadoPorDataETipo();
        }

        public List<Secao> consultarTodasSecoes() {
            return secaoRepository.findAll();
        }

        public List<Secao> consultarLocaisDisponiveisParaVolume(Long tipoBebidaId, double volume) {
            TipoBebida tipoBebida = tipoBebidaRepository.findById(tipoBebidaId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de Bebida não encontrado"));

            return secaoRepository.findAll().stream()
                    .filter(secao -> (tipoBebida.getCapacidadeMaxima() - secao.getCapacidadeAtual()) >= volume)
                    .collect(Collectors.toList());
        }

        public List<Secao> consultarSecoesDisponiveisParaVenda(Long tipoBebidaId) {
            // Encontre todas as seções que contêm bebidas do tipo especificado e que têm volume disponível
            return secaoRepository.findAll().stream()
                    .filter(secao -> bebidaRepository.findBySecao(secao).stream()
                            .anyMatch(bebida -> bebida.getTipoBebida().getId().equals(tipoBebidaId))
                    )
                    .collect(Collectors.toList());
        }

    }
