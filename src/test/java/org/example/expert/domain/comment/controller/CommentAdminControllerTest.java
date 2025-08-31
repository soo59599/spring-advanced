package org.example.expert.domain.comment.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.expert.common.interceptor.AuthInterceptor;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentAdminController.class)
public class CommentAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentAdminService commentAdminService;

	@MockBean
	private AuthInterceptor authInterceptor;


	@Test
	public void comment_삭제_성공() throws Exception {
		//given
		long commentId = 1L;

		when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);

		//when //then
		mockMvc.perform(delete("/admin/comments/{commentId}", commentId)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		verify(commentAdminService).deleteComment(commentId);
	}
}
