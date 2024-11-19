package br.edu.ibmec.cartao_credito.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByCartoes(Cartao cartao);
}