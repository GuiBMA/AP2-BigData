package br.edu.ibmec.cartao_credito.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.edu.ibmec.projeto_cloud.model.Transacao;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByValorAndComerciante(double valor, String comerciante);

}
