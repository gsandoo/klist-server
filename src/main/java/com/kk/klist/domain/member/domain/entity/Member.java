package com.kk.klist.domain.member.domain.entity;

import com.kk.klist.global.security.auth.Role;
import com.kk.klist.global.util.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members", uniqueConstraints = @UniqueConstraint(columnNames = {"oauthProvider", "oauthId"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oauthProvider;

    @Column(nullable = false)
    private String oauthId;

    @Column
    private String nationality;

    @Column
    private String preferredLanguage;

    @Column
    private String profileImageUrl;

    @Builder
    private Member(String nickname, OAuthProvider oauthProvider, String oauthId) {
        this.nickname = nickname;
        this.role = Role.USER;
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
    }

    public static Member create(String nickname, OAuthProvider oauthProvider, String oauthId) {
        return Member.builder()
                .nickname(nickname)
                .oauthProvider(oauthProvider)
                .oauthId(oauthId)
                .build();
    }
}
