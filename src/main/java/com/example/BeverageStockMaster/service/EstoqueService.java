    package com.example.BeverageStockMaster.service;
    import com.example.BeverageStockMaster.domain.Bebida;
    import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
    import com.example.BeverageStockMaster.domain.Secao;
    import com.example.BeverageStockMaster.domain.TipoBebida;
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

        @Autowired
        BebidaRepository bebidaRepository;

        @Autowired
        SecaoRepository secaoRepository;

        @Autowired
        HistoricoMovimentacaoRepository historicoRepository;



        public void registrarEntradaBebida(Bebida bebida, Long secaoId, String responsavel) {
            if (secaoId <= 0) {
                throw new IllegalArgumentException("O número da seção deve ser positivo.");
            }

            Secao secao = secaoRepository.findById(secaoId)
                    .orElseThrow(() -> new IllegalArgumentException("Seção não encontrada"));

            // Verificar se a seção já contém bebidas de um tipo diferente
            if (secao.getTipoBebida() != null && !secao.getTipoBebida().getId().equals(bebida.getTipoBebida().getId())) {
                throw new IllegalArgumentException("A seção só pode armazenar bebidas do tipo: " + secao.getTipoBebida().getDescricao());
            }

            // Calcular a nova capacidade
            double novaCapacidadeAtual = secao.getCapacidadeAtual() + bebida.getVolume();
            if (novaCapacidadeAtual > secao.getCapacidadeMaxima()) {
                throw new IllegalArgumentException("Capacidade máxima da seção excedida.");
            }

            // Atualizar a capacidade e o tipo de bebida na seção
            secao.setCapacidadeAtual(novaCapacidadeAtual);
            secao.setTipoBebida(bebida.getTipoBebida());
            secaoRepository.save(secao);

            // Associar a bebida à seção e salvar
            bebida.setSecao(secao);
            bebidaRepository.save(bebida);

            // Registrar a movimentação no histórico
            registrarHistorico("ENTRADA", bebida.getVolume(), secao, responsavel);
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

            registrarHistorico("SAIDA", bebida.getVolume(), secao, responsavel);
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

        public List<Secao> consultarSecoesDisponiveis(double volume) {
            return secaoRepository.findAll().stream()
                    .filter(secao -> secao.getCapacidadeMaxima() - secao.getCapacidadeAtual() >= volume)
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

        public void deletarBebida(Long bebidaId, String responsavel) {
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
            registrarHistorico("EXCLUSAO", bebida.getVolume(), secao, responsavel);

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
