package com.ecoshop.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // Como o Gateway é reativo, usamos Mono para retornar a resposta de forma não-bloqueante
    @RequestMapping("/order")
    public Mono<ResponseEntity<String>> orderServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Ops! O serviço de pedidos está temporariamente indisponível. Por favor, tente novamente mais tarde."));
    }
}
