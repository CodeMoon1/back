package com.oceanodosdados.records.apiPix;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ResponsePixSuccessTxid(

        Calendario calendario,
        String txid,
        int revisao,
        Loc loc,
        String location,
        String status,
        Devedor devedor,
        Valor valor,
        String chave,
        String solicitacaoPagador,
        String pixCopiaECola,
        List<InfoAdicional> infoAdicionais

) {

    public record Calendario(
            Instant criacao,
            int expiracao
    ) {}

    public record Loc(
            Long id,
            String location,
            Instant criacao,
            String tipoCob
    ) {}

    public record Devedor(
            String cpf,
            String nome
    ) {}

    public record Valor(
            BigDecimal original
    ) {}

    public record InfoAdicional(
            String nome,
            String valor
    ) {}
}