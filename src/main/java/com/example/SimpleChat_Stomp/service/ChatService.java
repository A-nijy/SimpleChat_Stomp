package com.example.SimpleChat_Stomp.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChatService {

    private final Map<String, Set<String>> rooms = new HashMap<>();

    @Transactional
    public String roomCreate(String sender) {

        String roomNumber;

        // 랜덤 방 코드 생성 및 중복 검사
        do {
            roomNumber = randomRoomNumber();
        } while (rooms.containsKey(roomNumber));

        // Map에서 해당 방 코드와 Set<String>(사용자)을 매핑하여 추가
        Set<String> players = new HashSet<>();
        players.add(sender);
        rooms.put(roomNumber, players);

        return roomNumber;
    }


    @Transactional
    public void roomJoin(String roomNumber, String sender) {

        // 해당 방이 존재하는지 확인 후 해당 방에 유저 담기
        if (rooms.containsKey(roomNumber)){
            Set<String> players = rooms.get(roomNumber);
            players.add(sender);
        }
    }

    @Transactional
    public void roomRemove(String roomNumber, String sender) {

        Set<String> players = rooms.get(roomNumber);

        if(players != null){
            players.remove(sender);
        }
    }

    //------------------------------------------

    // 랜덤 방코드 발급
    private String randomRoomNumber() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
