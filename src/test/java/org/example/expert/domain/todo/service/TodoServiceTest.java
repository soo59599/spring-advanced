package org.example.expert.domain.todo.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

	@Mock
	private TodoRepository todoRepository;

	@Mock
	private WeatherClient weatherClient;

	@InjectMocks
	private TodoService todoService;

	@Test
	void todo를_저장할_수_있다(){
		//given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);
		TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "content");
		Todo mockTodo = new Todo("title", "content", "맑음", User.fromAuthUser(authUser));
		given(todoRepository.save(any(Todo.class))).willReturn(mockTodo);

		//when
		TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getContents()).isEqualTo("content");
		then(todoRepository).should(times(1)).save(any(Todo.class));
	}

	@Test
	void todos를_페이지로_볼_수_있다(){
		//given
		int page = 1;
		int size = 2;
		Pageable pageable = PageRequest.of(page-1, size);

		User user = new User("test@example.com", "password", UserRole.USER);
		ReflectionTestUtils.setField(user, "id", 1L);
		Todo todo1 = new Todo("title", "contents", "맑음", user);
		ReflectionTestUtils.setField(todo1, "id", 1L);
		Todo todo2 = new Todo("title", "contents", "맑음", user);
		ReflectionTestUtils.setField(todo2, "id",2L);

		Page<Todo> todos = new PageImpl<>(List.of(todo1,todo2), pageable, 2);
		given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todos);

		//when
		Page<TodoResponse> responses = todoService.getTodos(page, size);

		//then
		assertThat(responses).isNotNull();
		assertThat(responses.getContent()).hasSize(2);

		TodoResponse todoResponse = responses.getContent().get(0);
		assertThat(todoResponse.getId()).isEqualTo(1L);
		assertThat(todoResponse.getTitle()).isEqualTo("title");
		assertThat(todoResponse.getContents()).isEqualTo("contents");
		assertThat(todoResponse.getUser().getEmail()).isEqualTo(user.getEmail());

	}

	@Test
	void 존재하지_않는_Todo를_조회_시_InvalidRequestException을_던진다(){
		//given
		long todoId = 1L;
		given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

		//when //then
		assertThrows(InvalidRequestException.class,
			()-> todoService.getTodo(todoId),
			"Todo not found");
	}

	@Test
	void Todo를_ID로_조회할_수_있다(){
		//given
		long todoId = 1L;
		User user = new User("asd@asd.com", "password", UserRole.USER);
		Todo todo = new Todo("title","contents","맑음", user);
		ReflectionTestUtils.setField(todo, "id", todoId);
		given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

		//when
		TodoResponse todoResponse = todoService.getTodo(todoId);

		//then
		assertThat(todoResponse).isNotNull();
		assertThat(todoResponse.getId()).isEqualTo(1L);
		assertThat(todoResponse.getTitle()).isEqualTo("title");
		assertThat(todoResponse.getContents()).isEqualTo("contents");
		assertThat(todoResponse.getUser().getEmail()).isEqualTo(user.getEmail());
		assertThat(todoResponse.getWeather()).isEqualTo("맑음");
	}
}
