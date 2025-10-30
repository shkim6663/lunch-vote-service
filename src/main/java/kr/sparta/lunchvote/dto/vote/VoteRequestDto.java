package kr.sparta.lunchvote.dto.vote;

import lombok.Getter;

@Getter
public class VoteRequestDto {
    private Long userId;
    private Long menuId;
    private Long roundId;
}
