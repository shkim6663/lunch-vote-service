package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.dto.menu.MenuRequestDto;
import kr.sparta.lunchvote.dto.menu.MenuResponseDto;
import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.repository.MenuRepository;
import kr.sparta.lunchvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final VoteRepository voteRepository;  // 투표 수, 로그인 사용자 체크용

    // 메뉴 생성
    public MenuResponseDto createMenu(MenuRequestDto dto, User user) {
        Menu menu = Menu.builder()
                .name(dto.getName())
                .type(dto.getType())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .build();

        Menu saveMenu = menuRepository.save(menu);

        int voteCount = voteRepository.countByMenu(saveMenu);
        boolean isVotedByMe = user != null && voteRepository.existsByMenuAndUser(saveMenu, user);

        return new MenuResponseDto(saveMenu, voteCount, isVotedByMe);
    }

    // 모든 메뉴 조회
    public List<MenuResponseDto> getAllMenus(User user) {
        return menuRepository.findAll()
                .stream()
                .map(menu -> {
                    int voteCount = voteRepository.countByMenu(menu);
                    boolean isVotedByMe = user != null && voteRepository.existsByMenuAndUser(menu, user);
                    return new MenuResponseDto(menu, voteCount, isVotedByMe);
                })
                .collect(Collectors.toList());
    }
}


