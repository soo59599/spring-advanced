package org.example.expert.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.example.expert.domain.user.enums.UserRole.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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
public class UserAdminServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserAdminService userAdminService;

	@Test
	void User의_Role을_바꿀_수_있다(){
		//given
		String email = "a@a.com";
		long userId = 1L;
		User user = new User(email, "password", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", userId);

		given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

		//when
		UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
		userAdminService.changeUserRole(userId, request);

		//then
		assertThat(userRepository.findById(userId).get().getUserRole()).isEqualTo(ADMIN);
	}
}
