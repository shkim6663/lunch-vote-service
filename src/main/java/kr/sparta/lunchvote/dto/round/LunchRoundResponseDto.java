package kr.sparta.lunchvote.dto.round;

import kr.sparta.lunchvote.dto.menu.MenuResponseDto;
import kr.sparta.lunchvote.entity.LunchRound;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.repository.VoteRepository;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LunchRoundResponseDto {

    private Long id;
    private String userName;
    private String date;
    private List<MenuResponseDto> menus;
    private int totalVotes;

    public LunchRoundResponseDto(LunchRound round, User user, VoteRepository voteRepository) {
        this.id = round.getId();
        this.userName = round.getUser().getName();
        this.date = round.getDate() != null ? round.getDate().toString() : null;


        this.menus = round.getMenus().stream()
                .map(menu -> {
                    int voteCount = Math.toIntExact(voteRepository.countByMenu(menu)); // Long -> int 변환
                    boolean votedByMe = user != null && voteRepository.existsByMenuAndUser(menu, user);
                    return new MenuResponseDto(menu, voteCount, votedByMe);
                })
                .collect(Collectors.toList());


        this.totalVotes = this.menus.stream().mapToInt(MenuResponseDto::getVoteCount).sum();
    }
}

