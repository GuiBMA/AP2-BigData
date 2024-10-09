package main.java.ibmec.ap2bigdata.ap2_bigdata.entity;
import lombok.Data;
import java.util.List;

@Data
public class CompraRequest {
    private String clienteId;
    private List<String> produtosIds;
}
