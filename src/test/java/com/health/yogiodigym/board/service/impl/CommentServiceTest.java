package com.health.yogiodigym.board.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.health.yogiodigym.board.dto.CommentDto;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.entity.Comment;
import com.health.yogiodigym.board.repository.BoardRepository;
import com.health.yogiodigym.board.repository.CommentRepository;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.NoDeletePermissionException;
import com.health.yogiodigym.lesson.entity.Category;
import com.health.yogiodigym.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Member mockMember;
    private Category mockCategory;
    private Board mockBoard;
    private Comment mockComment;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        this.mockMember = Member.builder()
                .id(1L)
                .name("member")
                .build();

        this.mockCategory = Category.builder()
                .id(1L)
                .code("board")
                .name("health")
                .build();

        this.mockBoard = Board.builder()
                .id(1L)
                .member(mockMember)
                .category(mockCategory)
                .build();

        this.mockComment = Comment.builder()
                .id(1L)
                .member(mockMember)
                .board(mockBoard)
                .content("comment")
                .build();

        this.comments = new ArrayList<>(List.of(
                Comment.builder()
                        .member(mockMember)
                        .board(mockBoard)
                        .content("comment1")
                        .build(),
                Comment.builder()
                        .member(mockMember)
                        .board(mockBoard)
                        .content("comment2")
                        .build(),
                Comment.builder()
                        .member(mockMember)
                        .board(mockBoard)
                        .content("comment3")
                        .build()
        ));
    }

    @Test
    @DisplayName("게시글의 댓글 목록 조회 테스트")
    void testGetCommentsByBoardId() {
        // given
        when(commentRepository.findByBoardIdOrderByCreateDateTimeAsc(anyLong())).thenReturn(comments);

        // when
        List<CommentDto> commentDtos = commentService.getComments(1L);

        // then
        assertThat(commentDtos.size()).isEqualTo(3);
        assertThat(commentDtos.get(0).getMemberName()).isEqualTo("member");
        assertThat(commentDtos.get(0).getBoardId()).isEqualTo(1L);
        assertThat(commentDtos.get(0).getContent()).isEqualTo("comment1");
    }

    @Nested
    @DisplayName("댓글 추가 테스트")
    class AddComment {

        @Test
        @DisplayName("댓글 추가 성공")
        void testAddComment() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            // when
            commentService.addComment(mockBoard.getId(), mockMember, "comment");

            // then
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글 추가시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenAddComment() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> commentService.addComment(1L, mockMember, "comment"))
                    .isInstanceOf(BoardNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {

        @Test
        @DisplayName("댓글 삭제 성공")
        void testDeleteComment() {
            // given
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(mockComment));

            // when
            commentService.deleteComment(1L, Member.builder().id(1L).build());

            // then
            verify(commentRepository, times(1)).delete(mockComment);
        }

        @Test
        @DisplayName("댓글 삭제시 댓글의 사용자가 아닐 경우 예외 발생")
        void testNoDeletePermissionWhenDeleteComment() {
            // given
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(mockComment));

            // when
            // then
            assertThatThrownBy(() -> commentService.deleteComment(1L, Member.builder().id(2L).build()))
                    .isInstanceOf(NoDeletePermissionException.class);
        }
    }
}