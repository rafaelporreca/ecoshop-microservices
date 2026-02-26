package com.ecoshop.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Lista de rotas que são PÚBLICAS (não exigem token)
    public static final List<String> openApiEndpoints = List.of(
            "/api/users/register",
            "/api/users/login",
            "/eureka"
    );

    // Lógica que verifica se a rota atual NÃO está na lista pública
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
