package ibmec.ap2bigdata.ap2_bigdata.repository;
import com.azure.spring.data.cosmos.repository.CosmosRepository;

import ibmec.ap2bigdata.ap2_bigdata.entity.Cliente;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends CosmosRepository<Cliente, String> {
    List<Cliente> findByNome(String nome);
    List<Cliente> findByRegiao(String regiao);
    List<Cliente> findByEmail(String email);
}
