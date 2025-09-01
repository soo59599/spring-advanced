package org.example.expert.domain.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	void User를_ID로_조회할_수_있다() throws Exception {
		//given
		long userId = 1L;
		String email = "test@example.com";
		UserResponse mockResponse = new UserResponse(userId, email);
		when(userService.getUser(userId)).thenReturn(mockResponse);

		//when //then
		mockMvc.perform(get("/users/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(userId))
			.andExpect(jsonPath("$.email").value(email));

		verify(userService, times(1)).getUser(userId);
	}

	@Test
	void 비밀번호를_바꿀_수_있다() throws Exception {
		//given
		given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
		given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new AuthUser(1L, "email", UserRole.USER));

		UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword","newPassword");

		String jsonRequest = objectMapper.writeValueAsString(request);

		//when //then
		mockMvc.perform(put("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk());

		verify(userService, times(1)).changePassword(eq(1L), any(UserChangePasswordRequest.class));
	}
}
