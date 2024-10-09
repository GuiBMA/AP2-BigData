package ibmec.ap2bigdata.ap2_bigdata.service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.azure.cosmos.models.PartitionKey;
import ibmec.ap2bigdata.ap2_bigdata.entity.Product;
import ibmec.ap2bigdata.ap2_bigdata.repository.ProductRepository;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findProductByName(String productName) {
        return this.productRepository.findByProductName(productName);
    }

    public Optional<Product> findById(String productId, String category) {
        return this.productRepository.findById(productId, new PartitionKey(category));
    }

    public void save(Product product) {
        product.setProductId(UUID.randomUUID().toString());
        this.productRepository.save(product);
    }

    public void delete(String productId) throws Exception {

        Optional<Product> optProduct = this.productRepository.findById(productId);

        if (optProduct.isPresent() == false)
            throw new Exception("Não encontrei o produto a ser excluido");

        this.productRepository.deleteById(productId, new PartitionKey(optProduct.get().getProductCategory()));
    }

    public void update(String productId, Product product) throws Exception {
        Optional<Product> optProduct = this.productRepository.findById(productId);
    
        if (!optProduct.isPresent()) {
            throw new Exception("Não encontrei o produto a ser atualizado");
        }
    
        product.setProductId(optProduct.get().getProductId());
        product.setProductCategory(optProduct.get().getProductCategory());
        this.productRepository.save(product);
    }
    

    public List<Product> findByCategory(String category) {
        return this.productRepository.findByProductCategory(category);
    }

}