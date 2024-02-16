package com.chessgrinder.chessgrinder.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class WithRefererOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    @Autowired
    public WithRefererOAuth2AuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    public WithRefererOAuth2AuthorizationRequestResolver(DefaultOAuth2AuthorizationRequestResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = delegate.resolve(request);
        return patchState(oAuth2AuthorizationRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = delegate.resolve(request, clientRegistrationId);
        return patchState(oAuth2AuthorizationRequest, request);
    }

    private OAuth2AuthorizationRequest patchState(OAuth2AuthorizationRequest auth2AuthorizationRequest, HttpServletRequest request) {
        if (auth2AuthorizationRequest == null) {
            return null;
        }
        String referer = getRefererOrEmpty(request);
        String state = auth2AuthorizationRequest.getState();
        if (StringUtils.isNotBlank(referer)) {
            state = state + WebSecurityConfig.OAUTH2_STATE_SEPARATOR + referer;
        }
        return OAuth2AuthorizationRequest
                .from(auth2AuthorizationRequest)
                .state(state)
                .build();
    }

    private String getRefererOrEmpty(HttpServletRequest request) {
        if (request != null) {
            String referer = "";
            // Do not use Referer header
            //noinspection ConstantValue
            if (false) {
                String refererHeader = request.getHeader("Referer");
                if (StringUtils.isNotBlank(refererHeader)) {
                    referer = refererHeader;
                }
            }
            String refererParam = request.getParameter("referer");
            if (StringUtils.isNotBlank(refererParam)) {
                referer = refererParam;
            }
            if (!referer.startsWith("/")) {
                referer = "/" + referer;
            }
            return referer;
        }
        return "";
    }

}
