package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.dto.vote.VoteRequestDto;
import kr.sparta.lunchvote.dto.vote.VoteResponseDto;
import kr.sparta.lunchvote.entity.LunchRound;
import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.entity.Vote;
import kr.sparta.lunchvote.repository.LunchRoundRepository;
import kr.sparta.lunchvote.repository.MenuRepository;
import kr.sparta.lunchvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final MenuRepository menuRepository;
    private final LunchRoundRepository lunchRoundRepository;

    // 1️⃣ 투표 기능
    @Transactional
    public VoteResponseDto vote(VoteRequestDto requestDto, User user) {
        Menu menu = menuRepository.findById(requestDto.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

        voteRepository.findByMenuAndUser(menu, user)
                .ifPresent(v -> { throw new IllegalArgumentException("이미 투표한 메뉴입니다."); });

        Vote vote = Vote.builder()
                .menu(menu)
                .user(user)
                .build();

        voteRepository.save(vote);

        long totalVotes = voteRepository.countByMenu(menu);

        return new VoteResponseDto(vote.getId(), menu.getId(), vote.getCreatedAt());
    }

    // 2️⃣ 내 투표 내역 조회
    @Transactional(readOnly = true)
    public List<VoteResponseDto> getMyVotes(User user, Long roundId) {
        List<LunchRound> rounds;

        if (roundId == null) {
            rounds = lunchRoundRepository.findByDate(LocalDate.now());
            if (rounds.isEmpty()) {
                throw new IllegalArgumentException("오늘의 라운드가 없습니다.");
            }
        } else {
            rounds = lunchRoundRepository.findById(roundId)
                    .map(List::of)
                    .orElseThrow(() -> new IllegalArgumentException("라운드를 찾을 수 없습니다."));
        }
        return rounds.stream()
                .flatMap(round -> voteRepository.findAllByUserAndMenu_Round(user, round).stream())
                .map(v -> new VoteResponseDto(v.getId(), v.getMenu().getId(), v.getCreatedAt()))
                .collect(Collectors.toList());
    }

        //투표 삭제

        @Transactional
        public void cancelVote(Long voteId, User user) {
            Vote vote = voteRepository.findById(voteId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 투표를 찾을 수 없습니다."));

            if (!vote.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("본인 투표만 취소할 수 있습니다.");
            }

            voteRepository.delete(vote);
        }

}


