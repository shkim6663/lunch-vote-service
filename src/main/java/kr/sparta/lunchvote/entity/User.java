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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Column(nullable = false)
    private String role; // 추가

    @OneToMany(mappedBy = "user")
    private List<LunchRound> rounds;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void initTime() {
        this.createdAt = LocalDateTime.now();

        this.updatedAt = LocalDateTime.now();
    }
}

