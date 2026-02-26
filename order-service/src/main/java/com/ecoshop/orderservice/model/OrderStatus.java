package com.ecoshop.orderservice.model;

public enum OrderStatus {

    PENDING,   // Aguardando confirmação de estoque/pagamento
    APPROVED,  // Saga concluída com sucesso
    REJECTED   // Saga falhou (estoque insuficiente ou cartão negado)

}
