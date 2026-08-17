package com.kk.klist.domain.chat.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

class ChatRedisConfigTest {

    @Test
    @DisplayName("Redis 접속 설정으로 RedisConnectionFactory와 StringRedisTemplate 빈이 생성된다")
    void redisBeans_whenPropertiesProvided_areCreated() {
        // given
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(
                new org.springframework.core.env.MapPropertySource(
                        "testRedisProperties",
                        java.util.Map.of(
                                "spring.data.redis.host", "localhost",
                                "spring.data.redis.port", "6379"
                        )
                )
        );
        context.register(ChatRedisConfig.class);

        try {
            // when
            context.refresh();

            // then
            assertThat(context.getBean(RedisConnectionFactory.class)).isNotNull();
            assertThat(context.getBean(StringRedisTemplate.class)).isNotNull();
        } finally {
            context.close();
        }
    }
}
