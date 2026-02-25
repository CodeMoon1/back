package com.oceanodosdados.domain;
import jakarta.persistence.*;
import com.oceanodosdados.enums.StatusEfi;
@Entity
@Table(name = "pix_charge")
public class PixCharge {
    @Id
    private String txid;
    @Column(precision = 19, scale = 2)
    private String valorOriginal;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEfi status;
    private String pixCopiaECola;
    private Integer locId;
    private String criacao;
    private String cpf;

    public PixCharge (){
        
    }

    public PixCharge(String txid, String valorOriginal, StatusEfi status, String pixCopiaECola, Integer locId, String criacao, String cpf) {
        this.txid = txid;
        this.valorOriginal = valorOriginal;
        this.status = status;
        this.pixCopiaECola = pixCopiaECola;
        this.locId = locId;
        this.criacao = criacao;
        this.cpf = cpf;
    }
    

    public Integer getLocId() {
        return locId;
    }

    public void setLocId(Integer locId) {
        this.locId = locId;
    }

    public String getTxid() {
        return txid;
    }
    public String getValorOriginal() {
        return valorOriginal;
    }
    public void setValorOriginal(String valorOriginal) {
        this.valorOriginal = valorOriginal;
    }
    public StatusEfi getStatus() {
        return status;
    }
    public void setStatus(StatusEfi status) {
        this.status = status;
    }
    public String getPixCopiaECola() {
        return pixCopiaECola;
    }
    public void setPixCopiaECola(String pixCopiaECola) {
        this.pixCopiaECola = pixCopiaECola;
    }
    public String getCriacao() {
        return criacao;
    }
    public void setCriacao(String criacao) {
        this.criacao = criacao;
    }
    public String getCpf() {
        return cpf;
    }


    
}
