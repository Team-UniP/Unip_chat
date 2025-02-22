package UniP_server_chat.Unip_party_chat.domain.chatLog.controller;

import UniP_server_chat.Unip_party_chat.domain.chatLog.dto.ChatLogBroadCastQueueResponse;
import UniP_server_chat.Unip_party_chat.domain.chatLog.dto.ChatMessage;
import UniP_server_chat.Unip_party_chat.domain.chatLog.dto.ChatMessageQueueFormat;
import UniP_server_chat.Unip_party_chat.domain.chatLog.service.ChatLogService;
import UniP_server_chat.Unip_party_chat.domain.chatLog.service.MessageProducer;
import UniP_server_chat.Unip_party_chat.domain.member.entity.Member;
import UniP_server_chat.Unip_party_chat.domain.member.service.CustomMemberService;
import UniP_server_chat.Unip_party_chat.global.baseResponse.ResponseDto;
import UniP_server_chat.Unip_party_chat.global.memberinfo.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅기록", description = "채팅 기록 관련 API")
public class ChatLogController {
    private final MessageProducer messageProducer;
    private final ChatLogService chatLogService;
    private final MemberInfo memberInfo;
    private final CustomMemberService customMemberService;

    //메시지 송신은 http로 진행한다.
    @Operation(summary = "채팅 전송", description = "특정 채팅방에 채팅을 전송한다.")
    @PostMapping("/topic/room/{roomId}")
    public ResponseEntity<ResponseDto<?>> sendChat(@PathVariable(name = "roomId") UUID roomId, @RequestBody ChatMessage chatMessage) {
        Member threadLocalMember = memberInfo.getThreadLocalMember();

        ChatLogBroadCastQueueResponse chatLogBroadCastResponse= ChatLogBroadCastQueueResponse.builder()
                .sender(threadLocalMember.getName())
                .content(chatMessage.getContent())
                .roomId(roomId)
                .senderImageUrl(customMemberService.getImageUrl(threadLocalMember))
                .build();

        messageProducer.sendMessageToServer(chatLogBroadCastResponse);//RabbitMQ에 메시지 전송(로드 밸런서가 리버스 프록시로 사용중인 다른 서버에도 요청을 보냄)

        ChatMessageQueueFormat chatMessageQueueFormat = ChatMessageQueueFormat.builder()
                .content(chatMessage.getContent())
                .sender(threadLocalMember.getName())
                .senderOauthName(threadLocalMember.getUsername())
                .roomId(roomId)
                .build();

        messageProducer.sendMessage(chatMessageQueueFormat); // RabbitMQ에 메시지 전송(데이터베이스 저장을 비동기로 수행)

        return ResponseEntity.ok().body(ResponseDto.of("메시지 전송 성공", null));
    }

    @GetMapping("/chat/logs/{roomId}")
    @Operation(summary = "채팅 기록 조회", description = "특정 채팅방의 채팅 기록을 가져온다.")
    public ResponseEntity<ResponseDto<?>> getChatLogs(@PathVariable(name = "roomId") UUID roomId,
                                                      @PageableDefault Pageable pageable) {

        return ResponseEntity.ok().body(ResponseDto.of("채팅 기록 조회 성공.", chatLogService.findById(roomId, pageable)));
    }

}
