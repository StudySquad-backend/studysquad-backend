package com.studysquad.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studysquad.board.domain.Board;
import com.studysquad.board.repository.BoardRepository;
import com.studysquad.boardcomment.domain.BoardComment;
import com.studysquad.boardcomment.repository.BoardCommentRepository;
import com.studysquad.user.domain.User;
import com.studysquad.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BoardCommentControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	BoardCommentRepository boardCommentRepository;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("게시글 댓글 전체 조회 성공")
	void successGetBoardComments() throws Exception {
		User user = userRepository.save(User.builder()
			.email("aaa@aaa.com")
			.nickname("userA")
			.build());

		Board board = boardRepository.save(Board.builder()
			.user(user)
			.build());

		List<BoardComment> boardComments = LongStream.range(1, 31)
			.mapToObj(i -> BoardComment.builder()
				.user(user)
				.board(board)
				.boardCommentContent("content" + i)
				.build())
			.collect(Collectors.toList());

		boardCommentRepository.saveAll(boardComments);

		mockMvc.perform(get("/api/board/{boardId}/boardcomments", board.getId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.message").value("게시글 댓글 조회 성공"))
			.andExpect(jsonPath("$.data.length()").value(boardComments.size()))
			.andDo(print());
	}
}
