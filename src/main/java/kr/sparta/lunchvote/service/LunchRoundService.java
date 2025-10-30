package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.dto.round.LunchRoundRequestDto;
import kr.sparta.lunchvote.dto.round.LunchRoundResponseDto;
import kr.sparta.lunchvote.entity.LunchRound;
import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.repository.LunchRoundRepository;
import kr.sparta.lunchvote.repository.UserRepository;
import kr.sparta.lunchvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LunchRoundService {

    private final LunchRoundRepository lunchRoundRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    // 라운드 생성
    @Transactional
    public LunchRoundResponseDto createRound(LunchRoundRequestDto dto, User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User Id가 없습니다");
        }

        User savedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Menu> menus = dto.getMenus().stream()
                .map(m -> Menu.builder()
                        .name(m.getName())
                        .type(m.getType())
                        .price(m.getPrice())
                        .description(m.getDescription())
                        .build())
                .toList();

        LunchRound round = LunchRound.builder()
                .user(savedUser)
                .date(LocalDate.parse(dto.getDate()))
                .menus(menus)
                .closed(false)
                .build();

        menus.forEach(menu -> {
            menu.setRound(round);
            menu.setVoteCount(0); // 생성 시 초기화
            menu.setVotedByMe(false); // 생성 시 초기화
        });

        LunchRound savedRound = lunchRoundRepository.save(round);
        return new LunchRoundResponseDto(savedRound, savedUser, voteRepository);
    }

    // 오늘 라운드 조회
    public List<LunchRoundResponseDto> getTodayRounds(User user) {
        LocalDate today = LocalDate.now();
        List<LunchRound> rounds = lunchRoundRepository.findByDate(today);
        if (rounds.isEmpty()) {
            throw new IllegalArgumentException("오늘의 라운드가 없습니다.");
        }

        return rounds.stream()
                .map(round -> new LunchRoundResponseDto(round, user, voteRepository))
                .collect(Collectors.toList());
    }

    // 전체 라운드 조회
    @Transactional(readOnly = true)
    public List<LunchRoundResponseDto> getAllRounds(User user) {
        return lunchRoundRepository.findAll().stream()
                .map(round -> new LunchRoundResponseDto(round, user, voteRepository))
                .toList();
    }

    // 라운드 삭제
    @Transactional
    public void deleteRound(Long roundId, User user) {
        LunchRound round = lunchRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("라운드를 찾을 수 없습니다."));

        // 요청한 유저가  확인
        if (!round.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("라운드를 삭제할 권한이 없습니다.");
        }

        // 삭제
        lunchRoundRepository.delete(round);
    }




    // 라운드 종료
    @Transactional
    public void closeRound(Long roundId) {
        LunchRound round = lunchRoundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("라운드를 찾을 수 없습니다."));
        round.setClosed(true);
    }
}
