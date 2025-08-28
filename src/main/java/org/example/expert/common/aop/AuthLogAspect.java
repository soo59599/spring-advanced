package org.example.expert.common.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.common.aop.annotation.AuthLog;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthLogAspect {

	private final ObjectMapper objectMapper;

	@Around("@annotation(authLog)")
	public Object aroundAuth(ProceedingJoinPoint joinPoint, AuthLog authLog) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

		LocalDateTime requestTime = LocalDateTime.now();
		Object userId = request.getAttribute("userId");
		String url = request.getRequestURI();
		Object requestBody = null;

		//요청 본문 찾기, JSON 변환
		for (Object arg : joinPoint.getArgs()) {
			if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
				requestBody = convertToJson(arg);
				break;
			}
		}

		log.info("=== [AOP API 요청 호출] ===");
		log.info("사용자 ID: {}", userId);
		log.info("요청 시각: {}", requestTime);
		log.info("요청 URL : {}", url);
		log.info("요청 본문: {}", requestBody);

		//요청 응답 찾기, JSON 변환
		Object response = joinPoint.proceed();
		String responseJson = convertToJson(response);

		log.info("응답 본문: {}", responseJson);
		log.info("=== [AOP API 요청 호출 끝] ===");

		return response;
	}

	private String convertToJson(Object o) {
		if (o == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			log.error("JSON 변환 실패: {}", e.getMessage());
			return null;
		}
	}
}