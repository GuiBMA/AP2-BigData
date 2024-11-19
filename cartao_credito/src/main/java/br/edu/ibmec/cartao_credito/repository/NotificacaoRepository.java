package br.edu.ibmec.cartao_credito.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ibmec.cartao_credito.model.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {
    
}
