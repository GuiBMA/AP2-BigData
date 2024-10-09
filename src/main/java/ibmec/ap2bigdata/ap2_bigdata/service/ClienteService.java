package main.java.ibmec.ap2bigdata.ap2_bigdata.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public void save(Cliente cliente) {
        cliente.setClienteId(UUID.randomUUID().toString());
        this.clienteRepository.save(cliente);
    }

    public Optional<Cliente> findById(String clienteId) {
        return this.clienteRepository.findById(clienteId);
    }

    public List<Cliente> findByNome(String nome) {
        return this.clienteRepository.findByNome(nome);
    }

    public List<Cliente> findByRegiao(String regiao) {
        return this.clienteRepository.findByRegiao(regiao);
    }

    public void delete(String clienteId) throws Exception {
        Optional<Cliente> optCliente = this.clienteRepository.findById(clienteId);

        if (!optCliente.isPresent()) {
            throw new Exception("Cliente não encontrado para exclusão");
        }

        this.clienteRepository.deleteById(clienteId, new PartitionKey(optCliente.get().getRegiao()));
    }

    public void update(String clienteId, Cliente clienteAtualizado) throws Exception {
        Optional<Cliente> optCliente = this.clienteRepository.findById(clienteId);

        if (!optCliente.isPresent()) {
            throw new Exception("Cliente não encontrado para atualização");
        }

        Cliente clienteExistente = optCliente.get();
        clienteAtualizado.setClienteId(clienteExistente.getClienteId());
        clienteAtualizado.setRegiao(clienteExistente.getRegiao());
        this.clienteRepository.save(clienteAtualizado);
    }
}
