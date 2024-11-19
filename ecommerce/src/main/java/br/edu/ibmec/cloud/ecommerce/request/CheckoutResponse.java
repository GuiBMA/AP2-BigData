package br.edu.ibmec.cloud.ecommerce.request;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckoutResponse {
    private String idProduto;
    private String status;
    private LocalDateTime dataTransacao;
    private String erro;
    private String idOrder;
}
