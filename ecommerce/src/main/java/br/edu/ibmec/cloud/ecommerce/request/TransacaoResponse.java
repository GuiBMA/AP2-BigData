package br.edu.ibmec.cloud.ecommerce.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransacaoResponse {
    private LocalDateTime dataTransacao;
    private double valor;
    private String status;
    private String erro;
    private UUID codigoAutorizacao;
}
