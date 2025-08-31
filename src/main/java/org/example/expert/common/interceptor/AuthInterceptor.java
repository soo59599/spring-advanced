package org.example.expert.common.interceptor;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler){

		String userRole = (String) request.getAttribute("userRole");

		if (!"ADMIN".equals(userRole)) {
			log.error("허용된 사용자가 아닙니다.");
			return false;
		}

		log.info("=== [INTERCEPTOR API 호출] ===");
		log.info("사용자 ID : {}", request.getAttribute("userId"));
		log.info("요청 시각 : {}", LocalDateTime.now());
		log.info("요청 URL  : {}", request.getRequestURI());
		log.info("=== [INTERCEPTOR API 호출 끝] ===");

		return true;
	}

}
