package UniP_server_chat.Unip_party_chat.domain.chatLog.service;

import UniP_server_chat.Unip_party_chat.domain.chatLog.dto.ChatLogDto;
import UniP_server_chat.Unip_party_chat.domain.chatLog.repository.ChatLogRepository;
import UniP_server_chat.Unip_party_chat.domain.chatRoomParticipant.entity.ChatRoomParticipant;
import UniP_server_chat.Unip_party_chat.domain.chatRoomParticipant.repository.ChatRoomParticipantRepository;
import UniP_server_chat.Unip_party_chat.domain.chatRoomParticipant.service.ChatRoomParticipantService;
import UniP_server_chat.Unip_party_chat.domain.member.entity.Member;
import UniP_server_chat.Unip_party_chat.domain.member.service.CustomMemberService;
import UniP_server_chat.Unip_party_chat.global.exception.custom.CustomException;
import UniP_server_chat.Unip_party_chat.global.exception.errorCode.ChatLogErrorCode;
import UniP_server_chat.Unip_party_chat.global.memberinfo.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final ChatLogRepository chatLogRepository;
    private final CustomMemberService customMemberService;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final MemberInfo memberInfo;

    public List<ChatLogDto> findById(UUID roomId, Pageable pageable) {
        Member member = customMemberService.loadUserByUsername(memberInfo.getUsername());

        Long startChatLogId = chatRoomParticipantRepository.findChatRoomParticipantByMemberId(member.getId());


        return chatLogRepository.findById(roomId, pageable,startChatLogId)
                .orElseThrow(() -> new CustomException(ChatLogErrorCode.CHAT_LOG_NOT_FOUND));
    }
}
