package ibmec.ap2bigdata.ap2_bigdata.repository;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import ibmec.ap2bigdata.ap2_bigdata.entity.Product;

@Repository
public interface ProductRepository extends CosmosRepository<Product, String> {
    List<Product> findByProductName(String productName);
    List<Product> findByProductCategory(String productCategory);
}
