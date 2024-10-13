package UniP_server_chat.Unip_party_chat.domain.chatRoom.controller;

import UniP_server_chat.Unip_party_chat.domain.chatRoom.dto.MakeChatRooms;
import UniP_server_chat.Unip_party_chat.domain.chatRoom.service.ChatRoomService;
import UniP_server_chat.Unip_party_chat.global.baseResponse.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 전체 채팅방을 조회
     * @return jwt 회원의 전체채팅방 조회 응답
     */
    @GetMapping("/chat/rooms")
    public ResponseEntity<ResponseDto<?>> getChatRooms() {
        return ResponseEntity.ok().body(ResponseDto.of("전체 채팅방 조회 성공", chatRoomService.getChatRooms()));
    }

    /**
     * 채팅방 생성
     * @param makeChatRooms 요청 데이터
     * @return jwt 회원의 채팅방 생성응답
     */
    @PostMapping("/chat/rooms")
    public ResponseEntity<ResponseDto<?>> makeChatRoom(@RequestBody @Valid MakeChatRooms makeChatRooms) {
        chatRoomService.makeChatRoomInit(makeChatRooms);
        return ResponseEntity.ok().body(ResponseDto.of("채팅방 생성 성공", null));
    }
}
