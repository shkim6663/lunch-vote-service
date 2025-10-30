package kr.sparta.lunchvote.controller;

import kr.sparta.lunchvote.dto.vote.VoteRequestDto;
import kr.sparta.lunchvote.dto.vote.VoteResponseDto;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponseDto> vote(
            @RequestBody VoteRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(voteService.vote(requestDto, user));
    }

    @GetMapping("/my")
    public ResponseEntity<List<VoteResponseDto>> getMyVotes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long roundId
    ) {
        return ResponseEntity.ok(voteService.getMyVotes(user, roundId));
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<Void> cancelVote(
            @PathVariable Long voteId,
            @AuthenticationPrincipal User user
    ) {
        voteService.cancelVote(voteId, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}



