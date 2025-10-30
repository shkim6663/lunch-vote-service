package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.dto.menu.MenuRequestDto;
import kr.sparta.lunchvote.dto.menu.MenuResponseDto;
import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuResponseDto createMenu(MenuRequestDto dto) {
        Menu menu = Menu.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        Menu saveMenu = menuRepository.save(menu);
        return new MenuResponseDto (saveMenu,0,false);
    }

    public List<MenuResponseDto> getAllMenus() {
        return menuRepository.findAll()
                .stream()
                .map(menu -> new MenuResponseDto(menu,0,false))
                .collect(Collectors.toList());
    }
}

