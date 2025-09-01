package org.example.expert.domain.todo.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TodoService todoService;

	@MockBean
	private AuthUserArgumentResolver authUserArgumentResolver;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void Todo_목록_조회_빈리스트() throws Exception {
		//given
		int page = 1;
		int size = 2;
		given(todoService.getTodos(page, size)).willReturn(Page.empty());

		//when // then
		mockMvc.perform(get("/todos")
				.param("page", "1")
				.param("size", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isEmpty())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content", hasSize(0)));
	}

	@Test
	void Todo_목록_조회() throws Exception {
		//given
		int page = 1;
		int size = 2;

		UserResponse userResponse1 = new UserResponse(1L, "user1@test.com");
		UserResponse userResponse2 = new UserResponse(2L, "user2@test.com");

		TodoResponse todo1 = new TodoResponse(1L, "첫 번째 할일", "첫 번째 내용", "맑음", userResponse1,
			LocalDateTime.of(2024, 1, 1, 10, 0),
			LocalDateTime.of(2024, 1, 1, 11, 0));

		TodoResponse todo2 = new TodoResponse(2L, "두 번째 할일", "두 번째 내용", "흐림", userResponse2,
			LocalDateTime.of(2024, 1, 2, 14, 0),
			LocalDateTime.of(2024, 1, 2, 15, 0));

		List<TodoResponse> todos = Arrays.asList(todo1, todo2);
		PageImpl<TodoResponse> todoPage = new PageImpl<>(todos, PageRequest.of(0, size),2);

		given(todoService.getTodos(page, size)).willReturn(todoPage);

		//when //then
		mockMvc.perform(get("/todos")
			.param("page", "1")
			.param("size", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id", is(1)))
			.andExpect(jsonPath("$.content").isArray());

	}

	@Test
	void Todo_단건_조회() throws Exception {
		//given
		long todoId = 1L;
		UserResponse userResponse1 = new UserResponse(1L, "user1@test.com");
		TodoResponse todo1 = new TodoResponse(todoId, "첫 번째 할일", "첫 번째 내용", "맑음", userResponse1,
			LocalDateTime.of(2024, 1, 1, 10, 0),
			LocalDateTime.of(2024, 1, 1, 11, 0));

		given(todoService.getTodo(todoId)).willReturn(todo1);

		//when //then
		mockMvc.perform(get("/todos/{todoId}", todoId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(todoId))
			.andExpect(jsonPath("$.title").value("첫 번째 할일"));
	}

	@Test
	void Todo_저장_성공() throws Exception {
		//given
		given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
		given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
			.willReturn(new AuthUser(1L, "email", UserRole.USER));

		UserResponse userResponse = new UserResponse(1L, "email");
		TodoSaveRequest request = new  TodoSaveRequest("title", "content");
		TodoSaveResponse response = new TodoSaveResponse(1L, "새로운 할일", "할일 내용", "맑음", userResponse);


		given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class))).willReturn(response);

		//when //then
		mockMvc.perform(post("/todos")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.title").value("새로운 할일"))
			.andExpect(jsonPath("$.contents").value("할일 내용"));
	}

}
