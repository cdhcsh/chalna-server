package com.jungle.chalnaServer.domain.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private ChatRoom.ChatRoomType chatRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private boolean isJoined = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLeaveAt;

    public ChatRoomMember(Member member, ChatRoom chatRoom) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.chatRoomType = chatRoom.getType();
    }
    public void updateDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void updateChatRoomType(ChatRoom.ChatRoomType chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public void updateLastLeaveAt(LocalDateTime lastLeaveAt) {
        this.lastLeaveAt = lastLeaveAt;
    }

    public void updateIsJoined(boolean isJoined) {
        this.isJoined = isJoined;
        if (isJoined)
            this.joinedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public String getUserName(){
        if(this.chatRoomType != ChatRoom.ChatRoomType.FRIEND)
            return displayName;
        else
            return member.getUsername();
    }
}
