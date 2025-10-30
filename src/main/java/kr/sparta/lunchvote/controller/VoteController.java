package kr.sparta.lunchvote.controller;

import kr.sparta.lunchvote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{menuId}")
    public ResponseEntity<String> voteMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal String userEmail
    ) {
        voteService.vote(menuId, userEmail);
        return ResponseEntity.ok("투표 완료!");
    }
}

