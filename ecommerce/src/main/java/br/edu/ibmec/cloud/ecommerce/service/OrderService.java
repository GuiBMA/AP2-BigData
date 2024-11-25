package br.edu.ibmec.cloud.ecommerce.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ibmec.cloud.ecommerce.entity.Order;
import br.edu.ibmec.cloud.ecommerce.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findOrderByIdCliente(String IdCliente) {
        return this.orderRepository.findOrderByIdCliente(IdCliente);
    }

    public List<Order> findOrderById(String idOrder) {
        return this.orderRepository.findOrderById(idOrder);
    }

    public java.util.Optional<Order> findById(String id) {
        return this.orderRepository.findById(id);
    }

    public List<Order> findOrderByIdProduto() {
        return (List<Order>) orderRepository.findAll();
    }

    public void save(Order order) {
        order.setIdOrder(UUID.randomUUID().toString());
        this.orderRepository.save(order);
    }

    // public void delete(String produtoId) throws Exception {

    //     Optional<Order> optProduct = this.orderRepository.findById(produtoId);

    //     if (optProduct.isPresent() == false)
    //         throw new Exception("Não encontrei o produto a ser excluido");

    //     this.orderRepository.deleteById(produtoId, new PartitionKey(optProduct.get().getCategoriaProduto()));
    // }
}
