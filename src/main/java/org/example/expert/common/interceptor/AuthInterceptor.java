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
	public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler)
		{

		log.info("관리자 API 호출 - 사용자ID: {}, 시각: {}, URL: {}",
			request.getAttribute("userId"),
			LocalDateTime.now(),
			request.getRequestURI());

		return true;
	}

}
