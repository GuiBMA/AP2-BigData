package br.edu.ibmec.cloud.ecommerce.repository;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import br.edu.ibmec.cloud.ecommerce.entity.Order;

@Repository
public interface OrderRepository extends CosmosRepository<Order, String> {
    List<Order> findOrderByIdCliente(String idCliente);
    List<Order> findOrderById(String id);
    List<Order> findByidProduto(String id);
    List<Order> findByIdCliente(String idCliente);
    List<Order> findByIdOrder(String idOrder);
}