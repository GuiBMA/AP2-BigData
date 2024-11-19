package br.edu.ibmec.cloud.ecommerce.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Container(containerName = "produtos")
public class Produto {
    @Id
    private String idProduto;

    @PartitionKey
    @NotBlank(message = "Categoria do produto deve ser preenchida.")
    private String categoriaProduto;

    @NotBlank(message = "Nome do produto deve ser preenchido.")
    private String nomeProduto;

    private double preco;

    private String urlFoto;

    private String descricaoProduto;
}
