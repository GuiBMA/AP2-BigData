package ibmec.ap2bigdata.ap2_bigdata.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.azure.cosmos.models.PartitionKey;

import ibmec.ap2bigdata.ap2_bigdata.entity.Cliente;
import ibmec.ap2bigdata.ap2_bigdata.entity.Compra;
import ibmec.ap2bigdata.ap2_bigdata.entity.Product;
import ibmec.ap2bigdata.ap2_bigdata.repository.ClienteRepository;
import ibmec.ap2bigdata.ap2_bigdata.repository.CompraRepository;
import ibmec.ap2bigdata.ap2_bigdata.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductRepository productRepository;

    public void processarCompra(String clienteId, List<String> produtosIds) throws Exception {
        // Verificar se o cliente existe
        Optional<Cliente> optCliente = clienteRepository.findById(clienteId);
        if (!optCliente.isPresent()) {
            throw new Exception("Cliente não encontrado");
        }
        Cliente cliente = optCliente.get();

        // Verificar se os produtos existem e calcular o valor total
        double valorTotal = 0.0;
        for (String produtoId : produtosIds) {
            Optional<Product> optProduto = productRepository.findById(produtoId);
            if (!optProduto.isPresent()) {
                throw new Exception("Produto com ID " + produtoId + " não encontrado");
            }
            valorTotal += optProduto.get().getPrice();
        }

        // Criar a compra
        Compra compra = new Compra();
        compra.setCompraId(UUID.randomUUID().toString());
        compra.setClienteId(clienteId);
        compra.setProdutosIds(produtosIds);
        compra.setValorTotal(valorTotal);
        compra.setDataCompra(LocalDateTime.now());
        compra.setRegiao(cliente.getRegiao());

        // Salvar a compra no CosmosDB
        compraRepository.save(compra);

        // Aqui você pode adicionar lógica para processar o pagamento usando o cartão de crédito
        // Certifique-se de tratar informações sensíveis de forma segura
    }

    public List<Compra> obterComprasPorCliente(String clienteId) {
        return compraRepository.findByClienteId(clienteId);
    }

    public List<Compra> obterComprasPorRegiao(String regiao) {
        return compraRepository.findByRegiao(regiao);
    }

    public List<Compra> obterComprasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return compraRepository.findByDataCompraBetween(inicio, fim);
    }
}
