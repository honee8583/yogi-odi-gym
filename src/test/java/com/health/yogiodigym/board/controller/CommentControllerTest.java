package com.health.yogiodigym.board.controller;

import static com.health.yogiodigym.common.message.ErrorMessage.BOARD_NOT_FOUND;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.health.yogiodigym.board.dto.CommentDto;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.CommentNotFoundException;
import com.health.yogiodigym.member.entity.Member;
import com.health.yogiodigym.util.ControllerTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommentControllerTest extends ControllerTest {

    @Nested
    @DisplayName("댓글 목록 조회 요청 테스트")
    class GetComments {

        @Test
        @DisplayName("댓글 목록 조회 성공")
        void testGetComments() throws Exception {
            // given
            List<CommentDto> comments = new ArrayList<>(List.of(
                    CommentDto.builder().build(),
                    CommentDto.builder().build(),
                    CommentDto.builder().build()
            ));
            when(commentService.getComments(1L)).thenReturn(comments);

            // when
            // then
            mvc.perform(get("/api/comment/{boardId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo("댓글 목록 조회에 성공하였습니다.")));
        }

        @Test
        @DisplayName("댓글 목록 조회 요청시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenGetComments() throws Exception {
            // given
            doThrow(new BoardNotFoundException(1L)).when(commentService).getComments(anyLong());

            // when
            // then
            mvc.perform(get("/api/comment/{boardId}", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage() + "-> " + 1L)));
        }
    }

    @Nested
    @DisplayName("댓글 추가 요청 테스트")
    class AddComment {

        @Test
        @DisplayName("댓글 추가 성공")
        void testAddComment() throws Exception {
            // given
            doNothing().when(commentService).addComment(anyLong(), any(Member.class), anyString());

            // when
            // then
            mvc.perform(post("/api/comment")
                            .param("boardId", "1")
                            .param("content", "comment"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo("댓글 추가에 성공하였습니다.")));
        }

        @Test
        @DisplayName("댓글 추가 요청시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenAddComment() throws Exception {
            // given
            doThrow(new BoardNotFoundException(1L)).when(commentService).addComment(anyLong(), any(Member.class), anyString());

            // when
            // then
            mvc.perform(post("/api/comment")
                            .param("boardId", "1")
                            .param("content", "comment"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo(BOARD_NOT_FOUND.getMessage() + "-> " + 1L)));
        }
    }

    @Nested
    @DisplayName("댓글 삭제 요청 테스트")
    class DeleteComment {

        @Test
        @DisplayName("댓글 삭제 성공")
        void testDeleteComment() throws Exception {
            // given
            doNothing().when(commentService).deleteComment(anyLong(), any(Member.class));

            // when
            // then
            mvc.perform(delete("/api/comment/{commentId}", 2L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo("댓글 삭제에 성공하였습니다.")));
        }

        @Test
        @DisplayName("댓글이 존재하지 않을 경우 예외 발생")
        void testCommentNotFoundWhenDeleteComment() throws Exception {
            // given
            doThrow(CommentNotFoundException.class).when(commentService).deleteComment(anyLong(), any(Member.class));

            // when
            // then
            mvc.perform(delete("/api/comment/{commentId}", 1L))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}