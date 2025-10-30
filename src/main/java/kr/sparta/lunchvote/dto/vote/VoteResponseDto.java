package kr.sparta.lunchvote.dto.vote;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VoteResponseDto {
    private Long id;
    private Long menuId;
    private LocalDateTime createdAt;

    public VoteResponseDto(Long id, Long menuId, LocalDateTime createdAt) {
        this.id = id;
        this.menuId = menuId;
        this.createdAt = createdAt;
    }
}
