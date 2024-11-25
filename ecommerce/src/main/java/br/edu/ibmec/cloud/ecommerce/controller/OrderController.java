package br.edu.ibmec.cloud.ecommerce.controller;
import br.edu.ibmec.cloud.ecommerce.entity.Order;
import br.edu.ibmec.cloud.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService; // Serviço correto para "Order"

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody Order order) {
        this.orderService.save(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping(value = "/idOrder/{id}")
    public ResponseEntity<List<Order>> getByidProduto(@PathVariable("idProduto") String id) {
        List<Order> orders = this.orderService.findOrderById(id);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // @GetMapping(value = "/idProduto/{id}")
    // public ResponseEntity<List<Order>> getOrderByidProduto(@PathVariable("idProduto") String id) {
    //     List<Order> orders = this.orderService.findByidProduto(id);

    //     return new ResponseEntity<>(orders, HttpStatus.OK);
    // }

}
