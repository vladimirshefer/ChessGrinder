package com.chessgrinder.chessgrinder.security.principal;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public final class CustomOidcUser implements OidcUser, AuthorizedUserEntityProvider {

    @Nonnull
    private final OidcUser delegate;
    @Nonnull
    private final UserEntity user;

    @Override
    public UserEntity getUserEntity() {
        return user;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return SecurityUtil.getGrantedAuthorities(user);
    }

    @Override
    public String getEmail() {
        return getUser().getUsername();
    }

    @Override
    public String getName() {
        return getUser().getUsername();
    }

    /*
    =======================================
              DELEDATED METHODS
    =======================================
     */

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public <A> A getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public String getAccessTokenHash() {
        return delegate.getAccessTokenHash();
    }

    @Override
    public List<String> getAudience() {
        return delegate.getAudience();
    }

    @Override
    public Instant getAuthenticatedAt() {
        return delegate.getAuthenticatedAt();
    }

    @Override
    public String getAuthenticationContextClass() {
        return delegate.getAuthenticationContextClass();
    }

    @Override
    public List<String> getAuthenticationMethods() {
        return delegate.getAuthenticationMethods();
    }

    @Override
    public String getAuthorizationCodeHash() {
        return delegate.getAuthorizationCodeHash();
    }

    @Override
    public String getAuthorizedParty() {
        return delegate.getAuthorizedParty();
    }

    @Override
    public Instant getExpiresAt() {
        return delegate.getExpiresAt();
    }

    @Override
    public Instant getIssuedAt() {
        return delegate.getIssuedAt();
    }

    @Override
    public URL getIssuer() {
        return delegate.getIssuer();
    }

    @Override
    public String getNonce() {
        return delegate.getNonce();
    }

    @Override
    public String getSubject() {
        return delegate.getSubject();
    }

    @Override
    public AddressStandardClaim getAddress() {
        return delegate.getAddress();
    }

    @Override
    public String getBirthdate() {
        return delegate.getBirthdate();
    }

    @Override
    public Boolean getEmailVerified() {
        return delegate.getEmailVerified();
    }

    @Override
    public String getFamilyName() {
        return delegate.getFamilyName();
    }

    @Override
    public String getFullName() {
        return delegate.getFullName();
    }

    @Override
    public String getGender() {
        return delegate.getGender();
    }

    @Override
    public String getGivenName() {
        return delegate.getGivenName();
    }

    @Override
    public String getLocale() {
        return delegate.getLocale();
    }

    @Override
    public String getMiddleName() {
        return delegate.getMiddleName();
    }

    @Override
    public String getNickName() {
        return delegate.getNickName();
    }

    @Override
    public String getPhoneNumber() {
        return delegate.getPhoneNumber();
    }

    @Override
    public Boolean getPhoneNumberVerified() {
        return delegate.getPhoneNumberVerified();
    }

    @Override
    public String getPicture() {
        return delegate.getPicture();
    }

    @Override
    public String getPreferredUsername() {
        return delegate.getPreferredUsername();
    }

    @Override
    public String getProfile() {
        return delegate.getProfile();
    }

    @Override
    public Instant getUpdatedAt() {
        return delegate.getUpdatedAt();
    }

    @Override
    public String getWebsite() {
        return delegate.getWebsite();
    }

    @Override
    public String getZoneInfo() {
        return delegate.getZoneInfo();
    }

    @Override
    public <T> T getClaim(String claim) {
        return delegate.getClaim(claim);
    }

    @Override
    public Boolean getClaimAsBoolean(String claim) {
        return delegate.getClaimAsBoolean(claim);
    }

    @Override
    public Instant getClaimAsInstant(String claim) {
        return delegate.getClaimAsInstant(claim);
    }

    @Override
    public Map<String, Object> getClaimAsMap(String claim) {
        return delegate.getClaimAsMap(claim);
    }

    @Override
    public String getClaimAsString(String claim) {
        return delegate.getClaimAsString(claim);
    }

    @Override
    public List<String> getClaimAsStringList(String claim) {
        return delegate.getClaimAsStringList(claim);
    }

    @Override
    public URL getClaimAsURL(String claim) {
        return delegate.getClaimAsURL(claim);
    }

    @Override
    public boolean hasClaim(String claim) {
        return delegate.hasClaim(claim);
    }
}
