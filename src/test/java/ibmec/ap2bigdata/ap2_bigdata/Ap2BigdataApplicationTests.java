/*package ibmec.ap2bigdata.ap2_bigdata;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ibmec.ap2bigdata.ap2_bigdata.controler.ProductController;
import ibmec.ap2bigdata.ap2_bigdata.entity.Product;
import ibmec.ap2bigdata.ap2_bigdata.service.ProductService;

@SpringBootTest
class Ap2BigdataApplicationTests {

	@Test
	void contextLoads() {
	}
    @Test
    public void test_update_product_success() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        Product product = new Product("1", "Test Product", 100);
    
        doNothing().when(productService).update("1", product);
    
        ResponseEntity response = productController.update("1", product);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
	@Test
	public void test_update_non_existent_product() {
		ProductService productService = mock(ProductService.class);
		ProductController productController = new ProductController(productService);
		Product product = new Product("2", "Non-existent Product", 200);
	
		doThrow(new Exception()).when(productService).update("2", product);
	
		ResponseEntity response = productController.update("2", product);
	
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

}
	*/
