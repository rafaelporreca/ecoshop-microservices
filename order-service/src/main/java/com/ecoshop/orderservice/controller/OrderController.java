package com.ecoshop.orderservice.controller;

import com.ecoshop.orderservice.dto.OrderRequest;
import com.ecoshop.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para o gerenciamento de pedidos na Ecoshop") // <-- NOME DA SESSÃO
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Cria um novo pedido", description = "Inicia a Saga coreografada: cria o pedido, reserva o estoque e processa o pagamento.") // <-- DESCRIÇÃO DO MÉTODO
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        Long orderId = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Pedido " + orderId + " criado com sucesso e enviado para processamento.");
    }
}
