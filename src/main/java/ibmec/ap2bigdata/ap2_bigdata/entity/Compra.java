package main.java.ibmec.ap2bigdata.ap2_bigdata.entity;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Container(containerName = "compras")
public class Compra {

    @Id
    private String compraId;

    private String clienteId;
    private List<String> produtosIds;
    private double valorTotal;
    private LocalDateTime dataCompra;

    @PartitionKey
    private String regiao;
}
