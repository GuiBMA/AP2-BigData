package main.java.ibmec.ap2bigdata.ap2_bigdata.controler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.azure.cosmos.models.PartitionKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        this.service.save(cliente);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable("id") String id) {
        Optional<Cliente> cliente = this.service.findById(id);

        if (cliente.isPresent()) {
            return new ResponseEntity<>(cliente.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/nome")
    public ResponseEntity<List<Cliente>> getByNome(@RequestParam String nome) {
        List<Cliente> clientes = this.service.findByNome(nome);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/regiao/{regiao}")
    public ResponseEntity<List<Cliente>> getByRegiao(@PathVariable("regiao") String regiao) {
        List<Cliente> clientes = this.service.findByRegiao(regiao);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") String id, @RequestBody Cliente cliente) {
        try {
            this.service.update(id, cliente);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") String id) {
        try {
            this.service.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
