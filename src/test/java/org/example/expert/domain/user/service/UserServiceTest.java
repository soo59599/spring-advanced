package org.example.expert.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void User를_ID로_조회할_수_있다(){
		//given
		String email = "a@a.com";
		long userId = 1L;
		User user = new User(email, "password", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		//when
		UserResponse userResponse = userService.getUser(userId);

		//then
		assertThat(userResponse).isNotNull();
		assertThat(userResponse.getEmail()).isEqualTo(email);
		assertThat(userResponse.getId()).isEqualTo(userId);
	}

	@Test
	void 존재하지_않는_User를_조회_시_InvalidRequestException을_던진다(){
		//given
		long userId = 1L;
		given(userRepository.findById(anyLong())).willReturn(Optional.empty());

		//when//then
		assertThrows(InvalidRequestException.class,
			()->userService.getUser(userId),
			"User not found");
	}

	@Test
	void 같은_비밀번호로_바꿀_수_없다(){
		//given
		long userId = 1L;
		String email = "a@a.com";
		User user = new User(email, "Password1", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		UserChangePasswordRequest request = new UserChangePasswordRequest("Password1", "Password1");
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).willReturn(true);

		//when // then
		assertThrows(InvalidRequestException.class,
			()->userService.changePassword(userId, request),
			"새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
	}

	@Test
	void 잘못된_기존_비밀번호() {
		// given
		long userId = 1L;
		String email = "a@a.com";
		User user = new User(email, "Password1", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);
		UserChangePasswordRequest request = new UserChangePasswordRequest("Password11", "Password1");
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		// when // then
		assertThrows(InvalidRequestException.class,
			() -> userService.changePassword(userId, request),
			"잘못된 비밀번호입니다.");
	}

	@Test
	void 비밀번호를_바꿀_수_있다(){
		//given
		long userId = 1L;
		String email = "a@a.com";
		User user = new User(email, "Password1", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);
		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		UserChangePasswordRequest request = new UserChangePasswordRequest("Password1", "NewPassword1");
		given(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).willReturn(true);
		given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(false);

		//when
		userService.changePassword(userId, request);
		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

		//then
		assertThat(user.getPassword()).isEqualTo(encodedNewPassword);

	}
}
