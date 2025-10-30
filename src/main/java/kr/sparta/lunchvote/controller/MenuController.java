package kr.sparta.lunchvote.controller;

import jakarta.validation.Valid;
import kr.sparta.lunchvote.dto.menu.MenuRequestDto;
import kr.sparta.lunchvote.dto.menu.MenuResponseDto;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponseDto> createMenu(
            @Valid @RequestBody MenuRequestDto requestDto,
            @AuthenticationPrincipal User user // JWT 인증 후 SecurityContext에서 가져오기
    ) {
        return ResponseEntity.ok(menuService.createMenu(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(
            @AuthenticationPrincipal User user // 로그인 사용자 정보 전달
    ) {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

}