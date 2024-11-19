package br.edu.ibmec.cloud.ecommerce.repository;
import br.edu.ibmec.cloud.ecommerce.entity.Produto;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends CosmosRepository<Produto, String> {
    List<Produto> findByNomeProduto(String nomeProduto);
    List<Produto> findByCategoriaProduto(String categoriaProduto);
}
