package kr.sparta.lunchvote.dto.menu;

import kr.sparta.lunchvote.entity.Menu;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuResponseDto {

    private Long id;
    private String name;
    private String type;
    private int price;
    private String description;
    private int voteCount;
    private boolean votedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Menu 객체 + vote 정보 받는 생성자
    public MenuResponseDto(Menu menu, int voteCount, boolean votedByMe) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.type = menu.getType();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.createdAt = menu.getCreatedAt();
        this.updatedAt = menu.getUpdatedAt();
        this.voteCount = voteCount;
        this.votedByMe = votedByMe;
    }
}






