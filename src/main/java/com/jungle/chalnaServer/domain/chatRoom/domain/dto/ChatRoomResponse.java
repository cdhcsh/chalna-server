package com.jungle.chalnaServer.domain.chatRoom.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.chat.domain.dto.ChatMessageResponse;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ChatRoomResponse {
    private Long id;
    private ChatRoom.ChatRoomType type;
    private Integer memberCount;
    private List<MemberInfo> members;
    private ChatMessageResponse recentMessage;
    private Integer unreadMessageCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime removedAt;


    public ChatRoomResponse(ChatRoom chatRoom, List<MemberInfo> memberInfos, ChatMessageResponse messageResponse, Integer unreadMessageCount) {
        this.id = chatRoom.getId();
        this.type = chatRoom.getType();
        this.memberCount = chatRoom.getMemberCount();
        this.members = memberInfos;
        this.recentMessage = messageResponse;
        this.unreadMessageCount = unreadMessageCount;
        this.createdAt = chatRoom.getCreatedAt();
        this.updatedAt = chatRoom.getUpdatedAt();
    }

    public LocalDateTime getRecentMessageCreatedAt() {
        return recentMessage != null ? recentMessage.getCreatedAt() : null;
    }

//    public static class MemberInfo {
//        private Long memberId;
//        private String username;
//
//        public MemberInfo(Long memberId, String username) {
//            this.memberId = memberId;
//            this.username = username;
//        }
//
//    }

}
