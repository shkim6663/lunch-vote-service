package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.entity.Vote;
import kr.sparta.lunchvote.repository.MenuRepository;
import kr.sparta.lunchvote.repository.UserRepository;
import kr.sparta.lunchvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Transactional
    public void vote(Long menuId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 이미 투표했으면 중복 투표 방지
        if(voteRepository.existsByMenuAndUser(menu, user)) {
            throw new IllegalStateException("이미 투표한 메뉴입니다.");
        }

        Vote vote = Vote.builder()
                .menu(menu)
                .user(user)
                .build();

        voteRepository.save(vote);
    }
}


