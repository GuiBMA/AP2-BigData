package br.edu.ibmec.cloud.ecommerce.request;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CheckoutRequest {
    private int idCliente;
    private String idProduto;
    private int idCartao;
    private LocalDateTime dataTransacao;
}