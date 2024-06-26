package shop.jtoon.member.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import shop.jtoon.exception.InvalidRequestException;
import shop.jtoon.type.ErrorStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LoginType {

	LOCAL,
	NAVER,
	KAKAO;

	private static final Map<String, LoginType> LOGIN_TYPE_MAP;

	static {
		LOGIN_TYPE_MAP = Collections.unmodifiableMap(
			Arrays.stream(LoginType.values())
				.collect(Collectors.toMap(LoginType::name, Function.identity()))
		);
	}

	public static LoginType from(String loginType) {
		return Optional.ofNullable(LOGIN_TYPE_MAP.get(loginType.toUpperCase()))
			.orElseThrow(() -> new InvalidRequestException(ErrorStatus.MEMBER_LOGIN_TYPE_INVALID_FORMAT));
	}
}
