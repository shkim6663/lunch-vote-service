## 프로젝트 구조
    lunch-vote-service
    ├─ src
    │  ├─ main
    │  │  ├─ java
    │  │  │  └─ kr.sparta.lunchvote
    │  │  │      ├─config
    │  │  │      │   ├─ SecurityConfig
    │  │  │      │
    │  │  │      ├─ controller
    │  │  │      │   ├─ AuthController.java       // 회원가입, 로그인, 토큰 재발급
    │  │  │      │   ├─ LunchRoundController.java // 라운드 CRUD, 오늘의 라운드 조회
    │  │  │      │   ├─ VoteController.java       // 투표, 내 투표 조회, 투표 취소
    │  │  │      │   └─ TokenController.java
    │  │  │      │
    │  │  │      ├─ dto
    │  │  │      │   ├─ auth
    │  │  │      │   │   ├─ SignupRequestDto.java
    │  │  │      │   │   ├─ LoginRequestDto.java
    │  │  │      │   │   └─ TokenResponseDto.java
    │  │  │      │   ├─ menu
    │  │  │      │       ├─ MenuRequestDto.java
    │  │  │      │       └─ MenuResponseDto.java
    │  │  │      │   └─round
    │  │  │      │       ├─ LunchRoundRequestDto.java
    │  │  │      │       └─ LunchRoundResponseDto.java
    │  │  │      │   └─ vote
    │  │  │      │       ├─ VoteRequestDto.java
    │  │  │      │       └─ VoteResponseDto.java
    │  │  │      │
    │  │  │      ├─ entity
    │  │  │      │   ├─ User.java
    │  │  │      │   ├─ LunchRound.java
    │  │  │      │   ├─ Menu.java
    │  │  │      │   └─ Vote.java
    │  │  │      │
    │  │  │      ├─ repository
    │  │  │      │   ├─ UserRepository.java
    │  │  │      │   ├─ LunchRoundRepository.java
    │  │  │      │   ├─ MenuRepository.java
    │  │  │      │   └─ VoteRepository.java
    │  │  │      │
    │  │  │      ├─ service
    │  │  │      │   ├─ AuthService.java
    │  │  │      │   ├─ LunchRoundService.java
    │  │  │      │   ├─ JwtService.java
    │  │  │      │   ├─ MenuService
    │  │  │      │   └─ VoteService.java
    │  │  │      │
    │  │  │      └─ security
    │  │  │          ├─ JwtAuthenticationFilter.java
    │  │  │          └─ JwtTokenProvider.java
    │  │  │
    │  │  └─ resources
    │  │      └─  application.properties  // DB, JPA, JWT 설정





---
## **1. 회원가입 API**
### **✅ 핵심 포인트**
-이메일 중복 검증
-비밀번호 BCrypt 암호화 저장
-회원 정보 저장 시 createdAt 자동 관리

![](https://velog.velcdn.com/images/shkim6663/post/6a111bff-69bc-41f6-b2d7-3c062e9b6182/image.png)



**코드요약**

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody          SignupRequestDto requestDto) {
    if (userRepository.existsByEmail(requestDto.getEmail())) {
        throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

    User user = User.builder()
            .email(requestDto.getEmail())
            .password(encodedPassword)
            .name(requestDto.getName())
            .role("USER")
            .build();

    userRepository.save(user);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(new UserResponseDto(user));
    }
    
    
 **검증 규칙**
-이메일 중복 체크
-이메일 형식 검증 (@Valid)
-비밀번호 최소 8자 이상
-비밀번호는 BCrypt로 암호화하여 저장 </aside>
 
    public class SignupRequestDto {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;
 
  ---  

##  **2. 로그인 + JWT 발급**
### **✅ 핵심 포인트**
-로그인 성공 시 AccessToken(1시간) + RefreshToken(7일) 발급
-JWT에 id, email 포함
-이후 모든 요청은 Header의 Authorization으로 인증

![](https://velog.velcdn.com/images/shkim6663/post/ff62aede-44ff-4415-adb8-40776df76d32/image.png)



**코드요약**

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto dto) {
    User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    String accessToken = jwtUtil.createAccessToken(user);
    String refreshToken = jwtUtil.createRefreshToken(user);

    return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
    }
    
 ---

## **3. JWT 재발급**
### **✅ 핵심 포인트**
-Access Token이 만료되면 Refresh Token으로 새 토큰 발급
-Refresh Token이 만료된 경우 → 재로그인 필요 (401)
![](https://velog.velcdn.com/images/shkim6663/post/fbbddbc9-fac9-4840-8a72-fb876a535efa/image.png)


**코드요약**

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto dto) {
    if (!jwtUtil.validateToken(dto.getRefreshToken())) {
        throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
    }

    String email = jwtUtil.getEmailFromToken(dto.getRefreshToken());
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    String newAccessToken = jwtUtil.createAccessToken(user);
    String newRefreshToken = jwtUtil.createRefreshToken(user);

    return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefreshToken));
    }
    
   --- 

## **4. JWT 인증 필터**
### **✅ 핵심 포인트**
-모든 API 요청 시 토큰 검증
-인증 성공 시 SecurityContext에 사용자 정보 저장

**코드요약**

    String token = resolveToken(request);
    if (token != null && jwtUtil.validateToken(token)) {
    User user = jwtUtil.getUserFromToken(token);
    Authentication auth = 
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
    }

---

## **5. 라운드 CRUD**

### **✅ 5-1라운드 생성**
-하루 1개 라운드만 생성 가능
-라운드 생성 시 여러 메뉴 함께 등록
-오늘의 라운드 / 전체 라운드 조회 가능

![](https://velog.velcdn.com/images/shkim6663/post/4b2db8a9-5ce1-4022-8969-f61c2a1e62f3/image.png)

**코드요약**

    @PostMapping("/rounds")
    public ResponseEntity<LunchRoundResponseDto> createRound(
        @RequestBody LunchRoundRequestDto dto,
        @AuthenticationPrincipal User user) {

    LunchRound round = lunchRoundService.createRound(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(new LunchRoundResponseDto(round, user, voteRepository));
    }

---

### **✅ 5-2. 오늘의 라운드 조회**
-오늘의 라운드 조회 가능
![](https://velog.velcdn.com/images/shkim6663/post/dc37ea5d-9d1c-4ed7-ae99-62f1893f4aff/image.png)

**코드요약**
 
    @GetMapping("/today")
    public ResponseEntity<List<LunchRoundResponseDto>> getTodayRounds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lunchRoundService.getTodayRounds(user));
    }



---

### **✅ 5-3. 전체 라운드 조회**
-전체 라운드 조회 가능
![](https://velog.velcdn.com/images/shkim6663/post/c4aac4c5-2fdf-4bf8-9a65-f16b2abe382e/image.png)

**코드요약**

    @GetMapping
    public ResponseEntity<List<LunchRoundResponseDto>> getAllRounds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(lunchRoundService.getAllRounds(user));
    }
  
  
---
### **✅ 5-4. 라운드 삭제**

![](https://velog.velcdn.com/images/shkim6663/post/8771ca12-472a-4bcf-b8e5-8677a062883d/image.png)

**코드요약**
  
    @DeleteMapping("/{roundId}")
    public ResponseEntity<Void> deleteRound(
            @PathVariable Long roundId,
            @AuthenticationPrincipal User user
    ) 


---

## **6. 투표 기능**

### **✅ 6-1. 투표하기**
-같은 메뉴 중복 투표 방지
-메뉴별 투표 수 즉시 반영
![](https://velog.velcdn.com/images/shkim6663/post/d0518aa7-2be7-4969-8382-adde5ee83235/image.png)

**코드 요약**


    @PostMapping
    public ResponseEntity<VoteResponseDto> vote(
        @RequestBody VoteRequestDto dto,
        @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(voteService.vote(dto, user));
    }
    
  ---  

### **✅ 6-2. 내 투표 내역 조회**
-특정 라운드 or 오늘 날짜 기준으로 내가 투표한 메뉴 목록 반환
-투표 시간(createdAt) 포함

![](https://velog.velcdn.com/images/shkim6663/post/7bb69492-8fcd-4ebf-a7f2-c6ae4b9d43c8/image.png)

**코드요약**

    @GetMapping("/my")
    public ResponseEntity<List<VoteResponseDto>> getMyVotes(
        @AuthenticationPrincipal User user,
        @RequestParam(required = false) Long roundId) {
    return ResponseEntity.ok(voteService.getMyVotes(user, roundId));
    }
    
    
 ---   

### **✅ 6-3. 투표 취소**
-본인 투표만 삭제 가능
-성공 시 204 No Content 반환
![](https://velog.velcdn.com/images/shkim6663/post/b455670d-34c8-4114-8f25-16926b35999a/image.png)



**코드요약**

    @DeleteMapping("/{voteId}")
    public ResponseEntity<Void> cancelVote(
        @PathVariable Long voteId,
        @AuthenticationPrincipal User user) {
    voteService.cancelVote(voteId, user);
    return ResponseEntity.noContent().build();
    }
    
    
 ---
 
 ## 트러블슈팅 노트
|문제|원인|해결
|--|--|--|
회원가입시 User Id가 null| 인증되지 않은 상태에서 User전달|User 객체와 id 체크 후 존재하지 않으면 예외 발생|
|메뉴 DTO 충돌|LunchRoundResponseDto.MenuDto와 Menudto 혼용|DTO 통합하거나 Menu 엔티티 그대로 사용하여 충돌 제거
 
 
 
 
---
## 마무리 요약

|기능	|URL	|설명|
|--|--|--|
|회원가입|	POST /api/auth/signup|	이메일 중복, 비밀번호 암호화|
|로그인	|POST /api/auth/login|	JWT 발급 (Access + Refresh)|
|토큰 재발급	|POST /api/auth/refresh	Refresh| Token 검증 후 재발급|
|라운드 생성	|POST /api/rounds|	메뉴 포함 라운드 생성|
|오늘의 라운드 조회|	GET /api/rounds/today|	당일 라운드 및 투표 현황|
|내 투표 내역 조회|	GET /api/votes/my|	roundId 선택 가능|
|투표 취소|	DELETE /api/votes/{id}|	본인 투표만 가능|
