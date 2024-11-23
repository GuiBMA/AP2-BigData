package br.edu.ibmec.cartao_credito.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Cliente;
import br.edu.ibmec.cartao_credito.repository.ClienteRepository;
import br.edu.ibmec.cartao_credito.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService service;

    @GetMapping
    public ResponseEntity<List<Cliente>> getCliente() {
        List<Cliente> Clientes = clienteRepository.findAll();
        return new ResponseEntity<>(Clientes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") int id) {
        Optional<Cliente> tryResponse = clienteRepository.findById(id);

        if (!tryResponse.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cliente response = tryResponse.get();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/buscar-id/{cpf}")
    public ResponseEntity<Integer> getClienteIdByCpf(@PathVariable("cpf") String cpf) {
        Optional<Cliente> clienteOptional = clienteRepository.findByCpf(cpf);

        if (!clienteOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cliente cliente = clienteOptional.get();
        return new ResponseEntity<>(cliente.getId(), HttpStatus.OK);
    }

    
    @PostMapping
    public ResponseEntity<Cliente> saveCliente(@Valid @RequestBody Cliente cliente) throws Exception {
        Cliente response = service.createCliente(cliente);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{id}/associar-cartao")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") int id, @Valid @RequestBody Cartao cartao) throws Exception {
        Cliente response = service.associarCartao(cartao, id);

        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{id}/cartao-status/{idCartao}")
    public ResponseEntity<Cartao> updateCartaoStatus(@PathVariable("id") int id, @PathVariable("idCartao") int idCartao) throws Exception {
        Cartao response = service.cartaoStatus(id, idCartao);

        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
