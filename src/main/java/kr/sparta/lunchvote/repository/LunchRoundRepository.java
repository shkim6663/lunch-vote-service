package kr.sparta.lunchvote.repository;

import kr.sparta.lunchvote.entity.LunchRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface LunchRoundRepository extends JpaRepository<LunchRound, Long> {
    List<LunchRound> findByDate(LocalDate date);

}

