package main.java.ibmec.ap2bigdata.ap2_bigdata.entity;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Container(containerName = "clientes")
public class Cliente {

    @Id
    private String clienteId;

    private String nome;
    private String email;
    private String cartaoDeCredito;

    @PartitionKey
    private String regiao;
}
