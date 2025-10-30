package kr.sparta.lunchvote.service;

import kr.sparta.lunchvote.dto.auth.LoginRequestDto;
import kr.sparta.lunchvote.dto.auth.SignupRequestDto;
import kr.sparta.lunchvote.dto.auth.TokenResponseDto;
import kr.sparta.lunchvote.entity.User;
import kr.sparta.lunchvote.repository.UserRepository;
import kr.sparta.lunchvote.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
    }

    public TokenResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail ())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String access = jwtUtil.createAccessToken(user.getEmail());
        String refresh = jwtUtil.createRefreshToken(user.getEmail());
        return new TokenResponseDto(access, refresh);
    }

    public TokenResponseDto refreshToken(String refreshToken) {
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String access = jwtUtil.createAccessToken(username);
        return new TokenResponseDto(access, refreshToken);
    }
}

