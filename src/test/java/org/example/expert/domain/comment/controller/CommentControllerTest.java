package org.example.expert.domain.comment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentService commentService;

	@MockBean
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void comment_등록() throws Exception {
		// given
		long todoId = 1L;
		given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
		given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new AuthUser(1L, "email", UserRole.USER));
		given(commentService.saveComment(any(AuthUser.class), eq(1L), any(CommentSaveRequest.class)))
			.willReturn(new CommentSaveResponse(1L, "test", new UserResponse(1L, "test@example.com")));

		// when & then - HTTP 요청이 제대로 처리되는지 확인
		mockMvc.perform(post("/todos/{todoId}/comments", todoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"contents\":\"test\"}"))
			.andExpect(status().isOk())
			.andExpect(header().string("Content-Type", "application/json"));

		// Service가 올바른 파라미터로 호출되었는지 확인
		verify(commentService).saveComment(any(AuthUser.class), eq(1L), any(CommentSaveRequest.class));
	}

	@Test
	void comments_목록_조회_빈리스트() throws Exception {
		//given
		long todoId = 1L;
		given(commentService.getComments(todoId)).willReturn(List.of());

		//when //then
		mockMvc.perform(get("/todos/{todoId}/comments", todoId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());

	}

	@Test
	void comments_목록_조회() throws Exception {
		//given
		long todoId = 1L;
		AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
		User user = User.fromAuthUser(authUser);
		Todo todo = new Todo("title", "title", "contents", user);
		ReflectionTestUtils.setField(todo, "id", todoId);
		String content1 = "contents1";
		String content2 = "contents2";

		List<Comment> commentList = List.of(
			new Comment(content1, user, todo),
			new Comment(content2, user, todo)
		);

		List<CommentResponse> dtoList = new ArrayList<>();
		for (Comment comment : commentList) {
			CommentResponse dto = new CommentResponse(
				comment.getId(),
				comment.getContents(),
				new UserResponse(user.getId(), user.getEmail())
			);
			dtoList.add(dto);
		}

		given(commentService.getComments(todoId)).willReturn(dtoList);

		//when & then

		mockMvc.perform(get("/todos/{todoId}/comments", todoId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].user.id").value(1L))
			.andExpect(jsonPath("$[0].contents").value(content1))
			.andExpect(jsonPath("$[1].user.id").value(1L))
			.andExpect(jsonPath("$[1].contents").value(content2));
	}

}
