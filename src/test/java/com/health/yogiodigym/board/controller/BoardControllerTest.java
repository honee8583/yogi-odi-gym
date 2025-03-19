package com.health.yogiodigym.board.controller;

import static com.health.yogiodigym.common.message.ErrorMessage.*;
import static com.health.yogiodigym.common.message.SuccessMessage.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardWriteRequestDto;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.common.dto.PageResponseDto;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.CategoryNotFoundException;
import com.health.yogiodigym.member.entity.Member;
import com.health.yogiodigym.util.ControllerTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

class BoardControllerTest extends ControllerTest {

    @Test
    @DisplayName("게시글 검색 요청 테스트")
    void testSearchBoards() throws Exception {
        // given
        Pageable pageable = PageRequest.of(1, 10);
        List<Board> boards = new ArrayList<>(List.of(
           Board.builder().build(),
           Board.builder().build(),
           Board.builder().build()
        ));
        List<BoardDetailDto> boardDetails = new ArrayList<>(List.of(
            BoardDetailDto.builder().build(),
            BoardDetailDto.builder().build(),
            BoardDetailDto.builder().build()
        ));
        PageImpl<Board> boardPage = new PageImpl<>(boards, pageable, 3);
        PageResponseDto<BoardDetailDto> response = new PageResponseDto<>(boardPage, boardDetails);

        when(boardService.searchBoards(any(BoardSearchRequestDto.class), any(Pageable.class))).thenReturn(response);

        // when
        // then
        mvc.perform(get("/api/board/search")
                .param("page", "0")
                .param("boardKeyword", "title")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(SEARCH_BOARD_SUCCESS.getMessage())));
    }

    @Nested
    @DisplayName("게시글 작성 요청 테스트")
    class RegisterBoard {

        @Test
        @DisplayName("게시글 작성 요청 성공")
        void testRegisterBoard() throws Exception {
            // given
            BoardWriteRequestDto registerRequest = BoardWriteRequestDto.builder()
                    .title("title")
                    .context("context")
                    .categoryId(1L)
                    .build();

            doNothing().when(boardService).registerBoard(any(BoardWriteRequestDto.class), any(Member.class));

            // when
            // then
            mvc.perform(post("/api/board")
                            .content(new ObjectMapper().writeValueAsString(registerRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo(REGISTER_BOARD_SUCCESS.getMessage())));
        }

        @Test
        @DisplayName("게시글 작성시 카테고리가 존재하지 않을 경우 예외 발생")
        void testCategoryNotFoundWhenRegisterBoard() throws Exception {
            // given
            BoardWriteRequestDto registerRequest = BoardWriteRequestDto.builder()
                    .title("title")
                    .context("context")
                    .categoryId(1L)
                    .build();

            doThrow(new CategoryNotFoundException(registerRequest.getCategoryId()))
                    .when(boardService)
                    .registerBoard(any(BoardWriteRequestDto.class), any(Member.class));

            // when
            // then
            mvc.perform(post("/api/board")
                            .content(new ObjectMapper().writeValueAsString(registerRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo(CATEGORY_NOT_FOUND.getMessage() + "-> " + registerRequest.getCategoryId())));
        }
    }

    @Nested
    @DisplayName("게시글 수정 요청 테스트")
    class EditBoard {

        @Test
        @DisplayName("게시글 수정 요청 성공")
        void testEditBoard() throws Exception {
            // given
            BoardWriteRequestDto updateRequest = BoardWriteRequestDto.builder()
                    .id(1L)
                    .title("title")
                    .context("context")
                    .categoryId(1L)
                    .build();

            doNothing().when(boardService).editBoard(any(Member.class), any(BoardWriteRequestDto.class));

            // when
            // then
            mvc.perform(put("/api/board")
                            .content(new ObjectMapper().writeValueAsString(updateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo(UPDATE_BOARD_SUCCESS.getMessage())));
        }

        @Test
        @DisplayName("게시글 수정시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenEditBoard() throws Exception {
            // given
            BoardWriteRequestDto updateRequest = BoardWriteRequestDto.builder()
                    .id(1L)
                    .title("title")
                    .context("context")
                    .categoryId(1L)
                    .build();

            doThrow(new BoardNotFoundException(updateRequest.getId()))
                    .when(boardService)
                    .editBoard(any(Member.class), any(BoardWriteRequestDto.class));

            // when
            // then
            mvc.perform(put("/api/board")
                            .content(new ObjectMapper().writeValueAsString(updateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage() + "-> " + updateRequest.getId())));
        }

        @Test
        @DisplayName("게시글 수정시 카테고리가 존재하지 않을 경우 예외 발생")
        void testCategoryNotFoundWhenEditBoard() throws Exception {
            // given
            BoardWriteRequestDto updateRequest = BoardWriteRequestDto.builder()
                    .id(1L)
                    .title("title")
                    .context("context")
                    .categoryId(1L)
                    .build();

            doThrow(new CategoryNotFoundException(updateRequest.getCategoryId()))
                    .when(boardService)
                    .editBoard(any(Member.class), any(BoardWriteRequestDto.class));

            // when
            // then
            mvc.perform(put("/api/board")
                            .content(new ObjectMapper().writeValueAsString(updateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo(CATEGORY_NOT_FOUND.getMessage() + "-> " + updateRequest.getCategoryId())));
        }
    }
}