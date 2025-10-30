package kr.sparta.lunchvote.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteResultDto {
    private String menuName;
    private Long voteCount;
}

