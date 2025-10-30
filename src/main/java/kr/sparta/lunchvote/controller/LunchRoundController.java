package kr.sparta.lunchvote.controller;

import kr.sparta.lunchvote.dto.round.LunchRoundRequestDto;
import kr.sparta.lunchvote.dto.round.LunchRoundResponseDto;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.service.LunchRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rounds")
@RequiredArgsConstructor
public class LunchRoundController {

    private final LunchRoundService lunchRoundService;

    @PostMapping
    public ResponseEntity<LunchRoundResponseDto> createRound(
            @RequestBody LunchRoundRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        System.out.println("유저:"+user);

        return ResponseEntity.ok(lunchRoundService.createRound(requestDto,user));
    }

    @GetMapping("/today")
    public ResponseEntity<List<LunchRoundResponseDto>> getTodayRounds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lunchRoundService.getTodayRounds(user));
    }

    @GetMapping
    public ResponseEntity<List<LunchRoundResponseDto>> getAllRounds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lunchRoundService.getAllRounds(user));
    }



    @PostMapping("/{roundId}/close")
    public ResponseEntity<String> closeRound(@PathVariable Long roundId) {
        lunchRoundService.closeRound(roundId);
        return ResponseEntity.ok("라운드가 마감되었습니다.");
    }
}
