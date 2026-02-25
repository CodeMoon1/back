package com.oceanodosdados.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubDoDevResponse(
        boolean status,
        @JsonProperty("return")
        String retorno,
        String consumed,
        String message,
        Result result
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String codigoPessoa,
            String nomeCompleto,
            String genero,
            String dataDeNascimento,
            String documento,
            String nomeDaMae,
            int anos,
            String zodiaco,
            List<Telefone> listaTelefones,
            List<Endereco> listaEnderecos,
            List<Email> listaEmails,
            String salarioEstimado,
            String statusCadastral,
            String dataStatusCadastral,
            String lastUpdate
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Telefone(
                String telefoneComDDD,
                String telemarketingBloqueado,
                String operadora,
                String tipoTelefone,
                String whatsApp
        ) {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Endereco(
                String logradouro,
                String numero,
                String complemento,
                String bairro,
                String cidade,
                String uf,
                String cep
        ) {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Email(
                String enderecoEmail
        ) {}
    }
}
