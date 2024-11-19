package br.edu.ibmec.cartao_credito.service;

import br.edu.ibmec.projeto_cloud.exception.CartaoException;
import br.edu.ibmec.projeto_cloud.exception.ClienteException;
import br.edu.ibmec.projeto_cloud.exception.TransacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.projeto_cloud.repository.TransacaoRepository;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.repository.NotificacaoRepository;
import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Notificacao;
import br.edu.ibmec.projeto_cloud.model.Cliente;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Transacao createTransacao(int id, Double valor, String comerciante, LocalDateTime dataTransacao) throws Exception {
        Transacao tryTransacao = new Transacao();
        tryTransacao.setComerciante(comerciante);
        tryTransacao.setValor(valor);
        tryTransacao.setDataTransacao(dataTransacao);
        
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(id);

        if (!cartaoOptional.isPresent())
            return null;
        
        Cartao cartao = cartaoOptional.get();

        Optional<Cliente> clienteOptional = clienteRepository.findByCartoes(cartao);

        if (!clienteOptional.isPresent())
            throw new ClienteException("Erro ao achar o cliente");

        Cliente cliente = clienteOptional.get();

        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Tentativa de transação");
        notificacao.setDataNotificacao(tryTransacao.getDataTransacao());

        String mensagemBase = "Tentativa de transação no cartão com final " + ultimosQuatroDigitos + ". Motivo da recusa: ";

        if (!cartao.getEstaAtivado()) {
            notificacao.setMensagem(mensagemBase + "Cartão desativado");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new CartaoException("Cartão desativado.");
        }

        if (cartao.getSaldo() < tryTransacao.getValor()) {
            // Envia notificação
            notificacao.setMensagem(mensagemBase + "Saldo insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Saldo insuficiente para a compra");
        }

        if (cartao.getLimite() < tryTransacao.getValor()) {
            notificacao.setMensagem(mensagemBase + "Limite insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Limite insuficiente para a compra");
        }

        List<Transacao> transacoesDuplicadas = transacaoRepository.findByValorAndComerciante(
            tryTransacao.getValor(), tryTransacao.getComerciante()
        );

        List<Transacao> transacoesCartao = cartao.getTransacoes();

        int transacoesNosUltimosDoisMinutos = 0;

        for (Transacao transacaoCartao : transacoesCartao) {
            if (verificaMinutagem(transacaoCartao.getDataTransacao(), tryTransacao.getDataTransacao())) {
                transacoesNosUltimosDoisMinutos++;
            }
        }

        if (transacoesNosUltimosDoisMinutos >= 3) {
            notificacao.setMensagem(mensagemBase + "Alta frequência de transações");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Limite de 3 transações em 2 minutos excedido.");
        }

        for (Transacao transacaoDuplicada : transacoesDuplicadas) {
            for (Transacao transacaoCartao : transacoesCartao) {
                if (transacaoCartao.getValor() == transacaoDuplicada.getValor() &&
                    transacaoCartao.getComerciante().equals(transacaoDuplicada.getComerciante())) {
                    if (verificaMinutagem(transacaoCartao.getDataTransacao(), tryTransacao.getDataTransacao())) {
                        notificacao.setMensagem(mensagemBase + "Transação duplicada");
                        cliente.associarNotificacao(notificacao);
                        notificacaoRepository.save(notificacao);
                        System.out.println(transacaoCartao.getDataTransacao());
                        System.out.println(transacaoDuplicada.getDataTransacao());
                        System.out.println(tryTransacao.getDataTransacao());
            
                        throw new TransacaoException("Transação duplicada encontrada.");
                    }
                }
            }
        }
        notificacao.setTipoNotificacao("Transação aprovada");
        notificacao.setMensagem("Transação aprovada no cartão com final " + ultimosQuatroDigitos + ". Valor de R$" + tryTransacao.getValor() + " em " + tryTransacao.getComerciante());

        cliente.associarNotificacao(notificacao);

        notificacaoRepository.save(notificacao);

        cartao.adicionarTransacao(tryTransacao);

        transacaoRepository.save(tryTransacao);

        cartao.setSaldo(cartao.getSaldo() - tryTransacao.getValor());

        cartaoRepository.save(cartao);
        
        return tryTransacao;
    }

    public List<Transacao> getExtratoByCartao(int id) throws Exception {
        Optional<Cartao> cartaoExistente = cartaoRepository.findById(id);

        if (!cartaoExistente.isPresent())
            return null;
        
        Cartao cartao = cartaoExistente.get();

        List<Transacao> transacoes = cartao.getTransacoes()
                                           .stream()
                                           .filter(x -> x.getDataTransacao().getMonth() == LocalDateTime.now().getMonth() && x.getDataTransacao().getYear() == LocalDateTime.now().getYear() )
                                           .toList();
        
        return transacoes;
    }

    private boolean verificaMinutagem(LocalDateTime dataTransacaoExistente, LocalDateTime dataTransacaoNova) {
        Duration duration = Duration.between(dataTransacaoExistente, dataTransacaoNova);

        return Math.abs(duration.toMinutes()) < 2;
    }

}
