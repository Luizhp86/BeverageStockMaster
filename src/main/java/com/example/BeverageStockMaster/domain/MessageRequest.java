package com.example.BeverageStockMaster.domain;

import com.example.BeverageStockMaster.domain.HistoricoMovimentacao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRequest {

    public String montarMessage(List<HistoricoMovimentacao> historicoMovimentacaoList) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<ObjectNode> historicoJsonList = historicoMovimentacaoList.stream()
                .map(movimentacao -> {
                    ObjectNode jsonNode = objectMapper.createObjectNode();
                    jsonNode.put("horario", movimentacao.getHorario().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    jsonNode.put("tipoMovimentacao", movimentacao.getTipoMovimentacao());
                    jsonNode.put("volume", movimentacao.getVolume());
                    jsonNode.put("secaoNome", movimentacao.getSecaoNome());
                    jsonNode.put("responsavel", movimentacao.getResponsavel());
                    jsonNode.put("tipoBebidaNome", movimentacao.getTipoBebidaNome());
                    return jsonNode;
                })
                .collect(Collectors.toList());

        String historicoJson;
        try {
            historicoJson = objectMapper.writeValueAsString(historicoJsonList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter o histórico de movimentação para JSON", e);
        }

        return String.format(
                "Faça uma análise da movimentação de estoque e me responda de forma sintética em texto com sugestões de compra com base no historico.  Histórico de movimentação: %s",
                historicoJson
        );
    }
}
