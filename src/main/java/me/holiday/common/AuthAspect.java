package me.holiday.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.holiday.sub.exception.AuthException;
import me.holiday.sub.exception.ServerException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    @Pointcut("@annotation(RequireAuth) || @within(RequireAuth)")
    public void requireAuth() {}

    private final HttpServletRequest request;

    @Before("requireAuth()")
    public void checkAuthorization() {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(token)
                || !token.startsWith("Bearer ")) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "토큰 없음", null);
        }
        token = token.substring("Bearer ".length());
        String authServer = "http://localhost:8001/api/v1/validation/access-token?token=" + token;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authServer))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("[Auth] 인증 서버 Not 200");
                throw new ServerException();
            }
            if (response.statusCode() == 403) {
                log.error("[Auth] 토큰 저장 X");
            }
        } catch (IOException | InterruptedException e) {
            log.error("[Auth] 인증 서버 요청 에러");
            throw new ServerException();
        }
    }
}
