package com.chessgrinder.chessgrinder.security.captcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class CaptchaRequiredAnnotationAspect {

    @Value(value = "${chessgrinder.captcha.secret:}")
    private String secret;

    @Value("${chessgrinder.feature.captcha.enabled:false}")
    private String enabled;

    private static final RestTemplate restTemplate = new RestTemplate();

    @Before("@annotation(com.chessgrinder.chessgrinder.security.captcha.CaptchaRequired)")
    public void beforeCaptchaRequired(JoinPoint joinPoint) {
        if (!enabled.equals("true")) {
            throw new CaptchaException("Captcha is disabled");
        }
        HttpServletRequest currentHttpRequest = Objects.requireNonNull(getCurrentHttpRequest(), "Not a valid HTTP request");
        String[] catchaToken = currentHttpRequest.getParameterMap().get("CAPTCHA_TOKEN");
        if (catchaToken == null || catchaToken.length == 0) {
            throw new IllegalArgumentException("Captcha is missing");
        }

        // Add logic to be executed before methods annotated with @CaptchaRequired
        CaptchaResponseDto captchaResponseDto = checkCaptchaChallenge(catchaToken[0]);
        log.info("Captcha verification {} \n {}", catchaToken[0], captchaResponseDto);
        if (!captchaResponseDto.success) {
            throw new CaptchaException("Illegal Captcha " + captchaResponseDto.errorCodes);
        }
    }

    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private CaptchaResponseDto checkCaptchaChallenge(String token) {
        final String url = "https://www.google.com/recaptcha/api/siteverify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", secret);
        requestBody.add("response", token);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CaptchaResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                CaptchaResponseDto.class
        );

        return responseEntity.getBody();
    }

    @Data
    private static class CaptchaResponseDto {
        private boolean success;

        @JsonProperty("challenge_ts")
        private String challengeTs;

        private String hostname;

        @JsonProperty("error-codes")
        private List<String> errorCodes;
    }

    @StandardException
    public static class CaptchaException extends RuntimeException {
    }

}

