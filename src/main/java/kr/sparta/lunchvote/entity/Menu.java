package kr.sparta.lunchvote.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private LunchRound round;

    private String name;
    private String type;
    private int price;
    private String description;

    @Transient
    private int voteCount; // DB에는 저장되지 않음

    @Transient
    private boolean votedByMe;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<Vote> votes;

    @PrePersist
    public void initTime() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTime() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setRound(LunchRound round) {
        this.round = round;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setVotedByMe(boolean votedByMe) {
        this.votedByMe = votedByMe;
    }
}
