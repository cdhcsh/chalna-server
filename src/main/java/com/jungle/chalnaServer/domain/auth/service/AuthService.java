package com.jungle.chalnaServer.domain.auth.service;

import com.jungle.chalnaServer.domain.auth.domain.dto.KakaoUserInfo;
import com.jungle.chalnaServer.domain.auth.exception.InvalidKakaoTokenException;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthRequest;
import com.jungle.chalnaServer.domain.auth.domain.dto.AuthResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.global.auth.jwt.dto.Tokens;
import com.jungle.chalnaServer.global.util.JwtService;
import com.jungle.chalnaServer.global.util.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final KakaoTokenService kakaoTokenService;


    public AuthResponse signup(AuthRequest.SIGNUP dto) {
        // db에서 아이디 중복 여부 확인
        String loginToken;
        String nickname;
        Long kakaoId;
        Optional<Member> findMember = memberRepository.findByKakaoId(dto.kakaoId());
        if (findMember.isPresent()) {
            loginToken = findMember.get().getLoginToken();
            nickname = dto.username();
            kakaoId = dto.kakaoId();
        } else {
            // kakao access token 검증
            boolean isValid = kakaoTokenService.verifyToken(dto.accessToken());
            log.info("kakao 엑세스 토큰 = {}",isValid);
            if (!isValid ) {
                // 유효성 검증 실패 시 토큰 갱신
                String newAccessToken = kakaoTokenService.refreshToken(dto.refreshToken());

                if (newAccessToken == null || !kakaoTokenService.verifyToken(newAccessToken)) {
                    log.info("new = {}",newAccessToken);
                    throw new InvalidKakaoTokenException();
                } else {
                    dto = new AuthRequest.SIGNUP(dto.kakaoId(), dto.username(), newAccessToken, dto.refreshToken());
                }
            }
            // 검증이 되면 kakaoid, username db에 넣기
            // kakao user 정보 가져오기
            KakaoUserInfo kakaoInfo = kakaoTokenService.getUserInfo(dto.accessToken());

            nickname = kakaoInfo.getProperties().getNickname();
            kakaoId = kakaoInfo.getKakaoId();

            loginToken = tokenService.generateToken();
            Member member= Member.builder()
                    .profileImageUrl("/images/default_image.png")
                    .loginToken(loginToken)
                    .username(nickname)
                    .kakaoId(kakaoId)
                    .build();
            log.info("member={}",member);

            memberRepository.save(member);

        }
        return AuthResponse.of(loginToken, kakaoId, nickname);
    }

    @Transactional
    public Tokens login(AuthRequest.LOGIN dto) {
        // 해당 loginToken db랑 검사
        Member member = memberRepository.findByLoginToken(dto.loginToken())
                .orElseThrow(MemberNotFoundException::new);

        member.updateInfo(dto.loginToken(),dto.deviceId(),dto.fcmToken());

        String accessToken = jwtService.createAccessToken(member);
        String refreshToken = jwtService.createRefreshToken(member);

        member.updateRefreshToken(refreshToken);

        return new Tokens(accessToken, refreshToken);
    }


}