package kr.sparta.lunchvote.repository;

import kr.sparta.lunchvote.entity.Vote;
import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.entity.LunchRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByMenuAndUser(Menu menu, User user);

    Long countByMenu(Menu menu);

    Optional<Vote> findByMenuAndUser(Menu menu, User user);

    // 내가 투표한 내역 조회 (라운드 기준)
    List<Vote> findAllByUserAndMenu_Round(User user, LunchRound round);
}





