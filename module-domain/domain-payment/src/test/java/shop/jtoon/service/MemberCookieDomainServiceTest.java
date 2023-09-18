package shop.jtoon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.jtoon.dto.MemberDto;
import shop.jtoon.entity.CookieItem;
import shop.jtoon.entity.Member;
import shop.jtoon.entity.MemberCookie;
import shop.jtoon.exception.NotFoundException;
import shop.jtoon.factory.CreatorFactory;
import shop.jtoon.repository.MemberCookieRepository;
import shop.jtoon.repository.MemberRepository;
import shop.jtoon.type.ErrorStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberCookieDomainServiceTest {

    @InjectMocks
    private MemberCookieDomainService memberCookieDomainService;

    @Mock
    private MemberCookieRepository memberCookieRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void beforeEach() {
        member = CreatorFactory.createMember("example123@naver.com");
        memberDto = MemberDto.toDto(member);
    }

    @DisplayName("createMemberCookie - 한 회원의 쿠키 정보가 성공적으로 저장될 때, - Void")
    @Test
    void createMemberCookie_Void() {
        // Given
        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

        // When
        memberCookieDomainService.createMemberCookie(CookieItem.COOKIE_ONE, memberDto);

        // Then
        verify(memberCookieRepository).save(any(MemberCookie.class));
    }

    @DisplayName("createMemberCookie - 해당 이메일에 대한 회원이 존재하지 않을 때, - NotFoundException")
    @Test
    void createMemberCookie_NotFoundException() {
        // Given
        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> memberCookieDomainService.createMemberCookie(CookieItem.COOKIE_ONE, memberDto))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(ErrorStatus.MEMBER_EMAIL_NOT_FOUND.getMessage());
    }
}
