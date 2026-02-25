package com.oceanodosdados.infra;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Lista de caminhos que NÃO serão limitados (Ex: H2 Console, Swagger, etc.)
    private final List<String> excludedPaths = Arrays.asList(
            "/h2-console/**",
            "/favicon.ico",
            "/error"
    );

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    private Bucket createAuthBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }

    private Bucket createGeneralBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(15, Refill.greedy(15, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Verifica se o caminho atual está na lista de exclusões
        boolean isExcluded = excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (isExcluded) {
            // Se estiver excluído, ignora o Rate Limiting e segue para o próximo filtro
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        // 2. Lógica de proteção diferenciada para os demais caminhos
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            Bucket authBucket = authBuckets.computeIfAbsent(clientIp, k -> createAuthBucket());
            if (!authBucket.tryConsume(1)) {
                System.out.println(">>> BLOQUEIO AUTH: IP " + clientIp + " em " + path);
                sendErrorResponse(response, "Limite de tentativas de autenticação excedido (3/min).");
                return;
            }
        } else {
            Bucket generalBucket = generalBuckets.computeIfAbsent(clientIp, k -> createGeneralBucket());
            if (!generalBucket.tryConsume(1)) {
                System.out.println(">>> BLOQUEIO AUTH: IP " + clientIp + " em " + path);
                sendErrorResponse(response, "Limite de requisições gerais excedido (20/min).");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"error\": \"Security Block\", \"message\": \"%s\", \"status\": 429}", message);
        response.getWriter().write(jsonResponse);
    }
}
