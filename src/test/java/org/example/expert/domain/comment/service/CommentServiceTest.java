package org.example.expert.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            commentService.saveComment(authUser, todoId, request);
        });

        // then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "title", "contents", user);
        Comment comment = new Comment(request.getContents(), user, todo);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }

    @Test
    public void Comment의_객체_생성됐는지_확인한다() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);
        long todoId = 1L;
        CommentSaveRequest request = new CommentSaveRequest("테스트 댓글");

        User user = User.fromAuthUser(authUser);

        Todo todo = new Todo("title", "title", "contents", user);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        // When
        commentService.saveComment(authUser, todoId, request);

        // Then
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void comments_조회가_된다(){
        // given
        long todoId = 1L;

        User user = new User("test@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "title", "contents", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        List<Comment> comments = Arrays.asList(
            new Comment("첫번째 댓글", user, todo),
            new Comment("두번째 댓글", user, todo)
        );
        ReflectionTestUtils.setField(comments.get(0), "id", 1L);
        ReflectionTestUtils.setField(comments.get(1), "id", 2L);

        when(commentRepository.findByTodoIdWithUser(todoId)).thenReturn(comments);

        // when
        List<CommentResponse> actualComments = commentService.getComments(todoId);
        // than
        assertNotNull(actualComments);
        assertEquals(2, actualComments.size());
        assertEquals("첫번째 댓글", actualComments.get(0).getContents());
        assertEquals("두번째 댓글", actualComments.get(1).getContents());
        assertEquals("test@example.com", actualComments.get(0).getUser().getEmail());

    }

}
