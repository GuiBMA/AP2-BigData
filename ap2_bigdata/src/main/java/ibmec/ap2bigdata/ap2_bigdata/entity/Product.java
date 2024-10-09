package ibmec.ap2bigdata.ap2_bigdata.entity;
import org.springframework.data.annotation.Id;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Data;

@Data
@Container(containerName = "products")
public class Product {

    @Id    
    private String productId;
    
    private String productName;
    
    @PartitionKey
    private String productCategory;
    
    private double price;
}
