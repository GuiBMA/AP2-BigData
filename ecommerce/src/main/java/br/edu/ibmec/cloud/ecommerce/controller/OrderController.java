package br.edu.ibmec.cloud.ecommerce.controller;

import br.edu.ibmec.cloud.ecommerce.entity.Order;
import br.edu.ibmec.cloud.ecommerce.entity.Pedido;
import br.edu.ibmec.cloud.ecommerce.entity.Produto;
import br.edu.ibmec.cloud.ecommerce.service.ProdutoService;
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
    private ProdutoService service;

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody Order order) {
        this.service.save(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping(value = "/idOrder/{id}")
    public ResponseEntity<List<Produto>> getByidProduto(@PathVariable("idProduto") String id) {
        List<Produto> produtos = this.service.findOrderById(id);

        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @GetMapping(value = "/idProduto/{id}")
    public ResponseEntity<List<Produto>> getOrderByidProduto(@PathVariable("idProduto") String id) {
        List<Produto> produtos = this.service.findByidProduto(id);

        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    // @GetMapping
    // public ResponseEntity<List<Order>> getByProductName(@RequestParam String nomeProduto) {
    //     List<Order> produtos = this.service.findProductByName(nomeProduto);
    //     return new ResponseEntity<>(produtos, HttpStatus.OK);
    // }

    // @GetMapping("/todos")
    // public ResponseEntity<List<Order>> getAllProducts() {
    //     List<Produto> produtos = this.service.buscarTodosProdutos(); // Busca todos os produtos do serviço
    //     return new ResponseEntity<>(produtos, HttpStatus.OK);
    // }

    // @DeleteMapping("{id}")
    // public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception{
    //     this.service.delete(id);
    //     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }
}
