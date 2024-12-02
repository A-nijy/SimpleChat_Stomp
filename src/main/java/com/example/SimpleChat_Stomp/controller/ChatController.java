package com.example.SimpleChat_Stomp.controller;


import com.example.SimpleChat_Stomp.dto.MessageDto;
import com.example.SimpleChat_Stomp.dto.RoomNumberResponseDto;
import com.example.SimpleChat_Stomp.dto.SenderRequestDto;
import com.example.SimpleChat_Stomp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/room/create")         // 클라이언트에서 /api/room/create 요청을 보내면 실행(호출)되는 메서드
    @SendToUser("/queue/game")              // /user/queue/game을 구독한 클라이언트에게 return값 응답 (요청한 사용자에게만 응답)
    public RoomNumberResponseDto roomCreate(@Payload SenderRequestDto request, SimpMessageHeaderAccessor headerAccessor){

        String roomNumber = chatService.roomCreate(request.getSender());

        RoomNumberResponseDto response = new RoomNumberResponseDto(roomNumber);

        // 현재 사용자의 세션 (웹소켓 연결된 세션)에 참여 중인 방 코드, 사용자 닉네임을 담아서 관리
        headerAccessor.getSessionAttributes().put("roomNumber", roomNumber);
        headerAccessor.getSessionAttributes().put("sender", request.getSender());

        return response;
    }

    @MessageMapping("/room/join/{roomNumber}")
    @SendTo("/topic/room/{roomNumber}")         // /topic/room/{roomNumber}를 구독한 클라이언트들에게 return 값(메시지) 전달
    public RoomNumberResponseDto roomJoin(@DestinationVariable String roomNumber, @Payload SenderRequestDto request, SimpMessageHeaderAccessor headerAccessor){

        // 고유한 값으로 한다면 아래처럼 닉네임이 아닌 headerAccessor.getSessionId()로 세션ID를 사용하는 것이 좋다.
        // 다만 여기서는 그냥 닉네임은 고유하다고 가정하고 닉네임으로 사용했다.
        // 닉네임과 세션모두 사용해도 된다.
        chatService.roomJoin(roomNumber, request.getSender());

        headerAccessor.getSessionAttributes().put("roomNumber", roomNumber);
        headerAccessor.getSessionAttributes().put("sender", request.getSender());

        RoomNumberResponseDto response = new RoomNumberResponseDto(roomNumber);

        return response;
    }

    @MessageMapping("/room/remove/{roomNumber}")
    @SendTo("/topic/room/{roomNumber}")
    public RoomNumberResponseDto roomRemove(@DestinationVariable String roomNumber, @Payload SenderRequestDto request, SimpMessageHeaderAccessor headerAccessor){

        chatService.roomRemove(roomNumber, request.getSender());

        headerAccessor.getSessionAttributes().remove("roomNumber");
        headerAccessor.getSessionAttributes().put("sender", request.getSender());

        RoomNumberResponseDto response = new RoomNumberResponseDto(roomNumber);

        return response;
    }

    @MessageMapping("/room/chat/{roomNumber}")
    @SendTo("/topic/room/{roomNumber}")
    public MessageDto roomChat(@DestinationVariable String roomNumber, @Payload MessageDto request, SimpMessageHeaderAccessor headerAccessor){

        return request;
    }
}
