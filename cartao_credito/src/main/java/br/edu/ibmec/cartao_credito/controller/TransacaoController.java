package br.edu.ibmec.cartao_credito.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import br.edu.ibmec.cartao_credito.exception.ClienteException;
import br.edu.ibmec.cartao_credito.model.Cartao;
import br.edu.ibmec.cartao_credito.model.Cliente;
import br.edu.ibmec.cartao_credito.model.Transacao;
import br.edu.ibmec.cartao_credito.repository.CartaoRepository;
import br.edu.ibmec.cartao_credito.repository.ClienteRepository;
import br.edu.ibmec.cartao_credito.repository.TransacaoRepository;
import br.edu.ibmec.cartao_credito.request.TransacaoRequest;
import br.edu.ibmec.cartao_credito.request.TransacaoResponse;
import br.edu.ibmec.cartao_credito.service.TransacaoService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CartaoRepository cartaoRepository;


    @GetMapping("{id}")
    public ResponseEntity<Transacao> getTransacaoById(@PathVariable("id") int id) {
        Optional<Transacao> tryResponse = transacaoRepository.findById(id);

        if (!tryResponse.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Transacao response = tryResponse.get();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/buscar-id-cartao/{numeroCartao}")
    public ResponseEntity<Integer> getIdCartaoByNumeroCartao(@PathVariable("numeroCartao") String numeroCartao) {
        Optional<Cartao> cartaoOptional = Optional.ofNullable(cartaoRepository.findByNumeroCartao(numeroCartao));

        if (cartaoOptional == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Retorna o ID do cartão
        return new ResponseEntity<>(cartaoOptional.get().getId(), HttpStatus.OK);
    }

    @GetMapping("extrato-cartao/{id}")
    public ResponseEntity<List<Transacao>> getTransacoesByCartao(@PathVariable("id") int id) throws Exception {
        List<Transacao> transacoes = transacaoService.getExtratoByCartao(id);

        if (transacoes == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(transacoes, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<TransacaoResponse> saveTransacao(@RequestBody TransacaoRequest request) throws Exception {

        Optional<Cliente> optCliente = clienteRepository.findById(request.getIdCliente());

        if (!optCliente.isPresent())
            throw new ClienteException("Usuario não encontrado, verifique o identificador.");
        
        Cliente cliente = optCliente.get();

        Optional<Cartao> optCartao = cliente.getCartoes()
                .stream()
                .filter(x -> x.getId() == (request.getIdCartao()))
                .findFirst();

        if (!optCartao.isPresent())
            throw new ClienteException("Cartão de id " + request.getIdCartao() + " não está associado ao cliente de id " + request.getIdCliente() + " ou não existe.");

        Cartao cartao = optCartao.get();
        TransacaoResponse response = new TransacaoResponse();

        try {
            Transacao transacao = transacaoService.createTransacao(cartao.getId(), request.getValor(), request.getComerciante(), request.getDataTransacao());

            response.setDataTransacao(transacao.getDataTransacao());
            response.setStatus("APROVADO");
            response.setValor(transacao.getValor());
            response.setCodigoAutorizacao(UUID.randomUUID());

        } catch (Exception e) {
            response.setStatus("REPROVADO:" + e.getMessage());
            response.setDataTransacao(request.getDataTransacao());
            response.setValor(request.getValor());
            response.setErro(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
