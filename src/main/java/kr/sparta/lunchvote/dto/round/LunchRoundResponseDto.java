package kr.sparta.lunchvote.dto.round;

import kr.sparta.lunchvote.dto.menu.MenuResponseDto;
import kr.sparta.lunchvote.entity.LunchRound;
import kr.sparta.lunchvote.entity.Menu;
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

    // 생성자: LunchRound + 로그인 사용자 + VoteRepository
    public LunchRoundResponseDto(LunchRound round, User user, VoteRepository voteRepository) {
        this.id = round.getId();
        this.userName = round.getUser().getName();
        this.date = round.getDate() != null ? round.getDate().toString() : null;

        // MenuResponseDto 변환 시 voteCount와 votedByMe 계산
        this.menus = round.getMenus().stream()
                .map(menu -> {
                    int voteCount = voteRepository.countByMenu(menu);
                    boolean votedByMe = user != null && voteRepository.existsByMenuAndUser(menu, user);
                    return new MenuResponseDto(menu, voteCount, votedByMe);
                })
                .collect(Collectors.toList());

        this.totalVotes = this.menus.stream().mapToInt(MenuResponseDto::getVoteCount).sum();
    }
}

