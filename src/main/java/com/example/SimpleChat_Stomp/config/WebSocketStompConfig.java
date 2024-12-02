package com.example.SimpleChat_Stomp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // 클라이언트가 서버로 메시지를 보내기 위한 URL 접두사(/app)지정
        // 즉, 클라이언트가 서버로 메시지를 보낼때 사용하는 URL 접두사 지정  (클라이언트가 /app로 시작하는 경로로 서버에 요청하면 메시지를 전달하는 것)
        registry.setApplicationDestinationPrefixes("/app");
        // /topic 으로 시작하는 요청 메시지는 브로커에서 처리하도록 설정
        // 메시지를 전달할 브로커를 설정하는 것으로 해당 경로로 시작하는 목적지(채널)를 브로커에서 처리하도록 설정
        registry.enableSimpleBroker("/topic", "/queue", "/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws/connect")
                .setAllowedOrigins("*")
                .withSockJS();
    }

}
