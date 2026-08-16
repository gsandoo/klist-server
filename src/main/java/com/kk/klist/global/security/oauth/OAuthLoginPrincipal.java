package com.kk.klist.global.security.oauth;

import com.kk.klist.global.security.auth.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * OAuth2 로그인 성공 시 SecurityContext에 채워지는 Authentication principal.
 * {@link com.kk.klist.global.security.auth.CustomUserDetails}와 마찬가지로 Member 엔티티를 모르는
 * 최소 값 타입이며, 이후 SuccessHandler는 이 타입만 알면 동작한다.
 */
@Getter
public class OAuthLoginPrincipal implements OAuth2User {

    private final Long memberId;
    private final Role role;
    private final boolean isNewMember;

    public OAuthLoginPrincipal(Long memberId, Role role, boolean isNewMember) {
        this.memberId = memberId;
        this.role = role;
        this.isNewMember = isNewMember;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
