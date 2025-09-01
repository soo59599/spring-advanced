package org.example.expert.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.common.interceptor.AuthInterceptor;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserAdminService userAdminService;

	@MockBean
	private AuthInterceptor authInterceptor;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void User의_Role을_바꿀_수_있다() throws Exception{
		//given
		given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
		long userId = 1L;
		UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
		String requestBody = objectMapper.writeValueAsString(userRoleChangeRequest);

		//when //then
		mockMvc.perform(patch("/admin/users/{userId}", userId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(status().isOk());

	}
}
