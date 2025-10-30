package kr.sparta.lunchvote.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LunchRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    private boolean closed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<Menu> menus;


    @PrePersist
    public void initTime() {
        this.createdAt = LocalDateTime.now();
        this.closed = false;
    }

    @PreUpdate
    public void updateTime() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}

