package com.example.SimpleChat_Stomp.config;

import com.example.SimpleChat_Stomp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


// 예기치 못한 세션 종료(브라우저 강제 종료, 네트워크 연결 끊김 등)가 발생할 때 Spring에서 자동으로 호출
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatService chatService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        // 세션 속성에서 사용자 정보 가져오기
        String roomNumber = (String) headerAccessor.getSessionAttributes().get("roomNumber");
        String sender = (String) headerAccessor.getSessionAttributes().get("sender");

        if (roomNumber != null && sender != null) {
            // chatService를 통해 채팅방 관리 Map에서 사용자 제거
            chatService.roomRemove(roomNumber, sender);
            // 세션에 roomNumber와 sender는 지울 필요가 없다.
            // 웹소켓 연결이 종료되면 세션도 종료가 되며, 추가로 저장했던 속성(roomNumber, Sender)도 자동으로 제거된다.
        }
    }
}
