package br.edu.ibmec.cloud.ecommerce.controller;

import br.edu.ibmec.cloud.ecommerce.entity.Order;
import br.edu.ibmec.cloud.ecommerce.entity.Produto;
import br.edu.ibmec.cloud.ecommerce.errorHandler.CheckoutException;
import br.edu.ibmec.cloud.ecommerce.request.CheckoutRequest;
import br.edu.ibmec.cloud.ecommerce.request.CheckoutResponse;
import br.edu.ibmec.cloud.ecommerce.service.CheckoutService;
import br.edu.ibmec.cloud.ecommerce.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    @Autowired
    private CheckoutService service;

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) throws Exception{

        Optional<Produto> optProduto = this.produtoService.findById(request.getIdProduto());

        if (optProduto.isPresent() == false)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Produto produto = optProduto.get();
        CheckoutResponse response = new CheckoutResponse();

        try {
            Order order = this.service.checkout(request.getIdCliente(), produto, request.getIdCartao(), request.getDataTransacao());
            response.setDataTransacao(order.getDataTransacao());
            response.setIdProduto(order.getIdProduto());
            response.setStatus(order.getStatus());
            response.setIdOrder(order.getIdOrder());
            response.setErro(null); // Nenhum erro se a compra foi bem-sucedida
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (CheckoutException e) {
            response.setDataTransacao(request.getDataTransacao());
            response.setIdProduto(request.getIdProduto());
            response.setStatus("REPROVADO");
            response.setErro(e.getMessage()); // Mensagem de erro do cartão
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
