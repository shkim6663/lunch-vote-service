package kr.sparta.lunchvote.dto.menu;

import lombok.Getter;

@Getter
public class MenuRequestDto {
    private String name;
    private String type;
    private int price;
    private String description;
}

