package org.example.expert.domain.comment.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentAdminService commentAdminService;

	@Test
	public void comment_삭제가_된다() {
		// given
		long commentId = 1L;
		Comment comment = mock(Comment.class);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when
		commentAdminService.deleteComment(commentId);

		// then
		verify(commentRepository).delete(any(Comment.class));
	}
}
