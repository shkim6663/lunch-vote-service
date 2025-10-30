package kr.sparta.lunchvote.repository;

import kr.sparta.lunchvote.entity.Menu;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    int countByMenu(Menu menu);

    boolean existsByMenuAndUser(Menu menu, User user);
}




