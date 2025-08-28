package org.example.expert.common.interceptor;

import java.time.LocalDateTime;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response,
		@Nullable Object handler) {

		log.info("=== [INTERCEPTOR API 호출] ===");
		log.info("사용자 ID : {}", request.getAttribute("userId"));
		log.info("요청 시각 : {}", LocalDateTime.now());
		log.info("요청 URL  : {}", request.getRequestURI());
		log.info("=== [INTERCEPTOR API 호출 끝] ===");

		return true;
	}

}
