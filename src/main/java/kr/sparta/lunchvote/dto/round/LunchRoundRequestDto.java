package kr.sparta.lunchvote.dto.round;

import kr.sparta.lunchvote.dto.menu.MenuRequestDto;
import lombok.Getter;

import java.util.List;

@Getter
public class LunchRoundRequestDto {
    private String date; // yyyy-MM-dd
    private List<MenuRequestDto> menus;
}



