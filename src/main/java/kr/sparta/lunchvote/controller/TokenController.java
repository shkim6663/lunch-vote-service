package kr.sparta.lunchvote.controller;

import kr.sparta.lunchvote.dto.TokenRequestDto;
import kr.sparta.lunchvote.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto requestDto) {
        String newAccessToken = jwtService.refreshAccessToken(requestDto.getRefreshToken());
        return ResponseEntity.ok().body(newAccessToken);
    }
}

