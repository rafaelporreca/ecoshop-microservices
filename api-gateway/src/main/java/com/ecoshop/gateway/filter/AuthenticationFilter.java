package com.ecoshop.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // Verifica se a rota exige segurança
            if (validator.isSecured.test(exchange.getRequest())) {

                // 1. Verifica se o cabeçalho Authorization existe
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // 2. Extrai o token
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7); // Remove a palavra "Bearer "
                }

                // 3. Valida o token com a nossa chave
                try {
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    System.out.println("Acesso Negado: Token inválido ou expirado.");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401
                    return exchange.getResponse().setComplete();
                }
            }

            // Se tudo estiver correto, permite que o pedido continue o seu caminho para o microsserviço
            return chain.filter(exchange);
        });
    }

    public static class Config {
        // Podemos colocar propriedades de configuração aqui no futuro, se necessário
    }
}
