package br.edu.ibmec.cloud.ecommerce.controller;

import br.edu.ibmec.cloud.ecommerce.entity.Produto;
import br.edu.ibmec.cloud.ecommerce.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @PostMapping
    public ResponseEntity<Produto> create(@Valid @RequestBody Produto produto) {
        this.service.save(produto);
        return new ResponseEntity<>(produto, HttpStatus.CREATED);
    }

    @GetMapping(value = "/categoria/{categoria}")
    public ResponseEntity<List<Produto>> getByCategory(@PathVariable("categoria") String categoria) {
        List<Produto> produtos = this.service.findByCategory(categoria);

        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> getByProductName(@RequestParam String nomeProduto) {
        List<Produto> produtos = this.service.findProductByName(nomeProduto);
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws Exception{
        this.service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
