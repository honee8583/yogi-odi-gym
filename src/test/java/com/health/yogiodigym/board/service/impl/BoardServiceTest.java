package com.health.yogiodigym.board.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardWriteRequestDto;
import com.health.yogiodigym.board.dto.BoardSearchType;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.repository.BoardRepository;
import com.health.yogiodigym.board.repository.CommentRepository;
import com.health.yogiodigym.common.dto.PageResponseDto;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.CategoryNotFoundException;
import com.health.yogiodigym.lesson.entity.Category;
import com.health.yogiodigym.lesson.repository.CategoryRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Member mockMember;
    private Category mockCategory;
    private Board mockBoard;
    private List<Board> boards;

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

        this.boards = new ArrayList<>(List.of(
                Board.builder()
                        .id(1L)
                        .member(mockMember)
                        .category(mockCategory)
                        .build(),
                Board.builder()
                        .id(2L)
                        .member(mockMember)
                        .category(mockCategory)
                        .build(),
                Board.builder()
                        .id(3L)
                        .member(mockMember)
                        .category(mockCategory)
                        .build()
        ));
    }

    @Nested
    @DisplayName("게시글 검색 테스트")
    class SearchBoards {

        private final BoardSearchRequestDto searchRequest = BoardSearchRequestDto.builder()
                .type(BoardSearchType.TITLE)
                .boardKeyword("board")
                .categoryId(1L)
                .build();

        @Test
        @DisplayName("게시글 검색 테스트 성공")
        void testSearchBoards() {
            // given
            Pageable pageable = PageRequest.of(1, 10);
            Page<Board> pages = new PageImpl<>(boards, pageable, 3);

            when(boardRepository.searchBoards(isNull(), any(BoardSearchRequestDto.class), any(Pageable.class)))
                    .thenReturn(pages);

            // when
            PageResponseDto<BoardDetailDto> boardDetails = boardService.searchBoards(searchRequest, pageable);

            // then
            assertThat(boardDetails.getContents().size()).isEqualTo(3);
        }

        @Test
        @DisplayName("내 게시글 검색 테스트")
        void testSearchMyBoards() {
            // given
            Pageable pageable = PageRequest.of(1, 10);
            Page<Board> pages = new PageImpl<>(boards, pageable, 3);

            when(boardRepository.searchBoards(any(Member.class), any(BoardSearchRequestDto.class), any(Pageable.class)))
                    .thenReturn(pages);

            // when
            PageResponseDto<BoardDetailDto> boardDetails = boardService.searchMyBoards(mockMember, searchRequest, pageable);

            // then
            assertThat(boardDetails.getContents().size()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("게시글 작성 테스트")
    class RegisterBoard {

        private final BoardWriteRequestDto writeRequest = BoardWriteRequestDto.builder()
                .id(1L)
                .categoryId(1L)
                .title("title")
                .context("context")
                .build();

        @Test
        @DisplayName("게시글 작성 테스트 성공")
        void testRegisterBoard() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            boardService.registerBoard(writeRequest, mockMember);

            // then
            verify(boardRepository, times(1)).save(any(Board.class));
        }

        @Test
        @DisplayName("게시글 작성시 카테고리 존재하지 않을 경우 예외 발생")
        void testCategoryNotFoundWhenRegisterBoard() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.registerBoard(writeRequest, mockMember))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("게시글 상세조회 테스트")
    class GetBoardDetail {

        @Test
        @DisplayName("게시글 상세조회 테스트 성공")
        void testGetBoardDetail() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));

            // when
            BoardDetailDto boardDetail = boardService.getBoardDetail(1L);

            // then
            assertThat(boardDetail.getCategoryId()).isEqualTo(1L);
            assertThat(boardDetail.getMemberName()).isEqualTo("member");
        }

        @Test
        @DisplayName("게시글 조회시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenGetBoardDetail() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.getBoardDetail(1L))
                    .isInstanceOf(BoardNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class EditBoard {

        private final BoardWriteRequestDto writeRequest = BoardWriteRequestDto.builder()
                .id(1L)
                .categoryId(1L)
                .title("modified title")
                .context("modified context")
                .build();

        @Test
        @DisplayName("게시글 수정 테스트")
        void testEditBoard() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));

            // when
            boardService.editBoard(mockMember, writeRequest);

            // then
            assertThat(mockBoard.getContext()).isEqualTo("modified context");
            assertThat(mockBoard.getTitle()).isEqualTo("modified title");
        }

        @Test
        @DisplayName("게시글 수정시 게시글이 존재하지 않을 경우 예외 발생")
        void testBoardNotFoundWhenEditBoard() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.editBoard(mockMember, writeRequest))
                    .isInstanceOf(BoardNotFoundException.class);
        }

        @Test
        @DisplayName("게시글 수정시 카테고리가 존재하지 않을 경우 예외 발생")
        void testCategoryNotFoundWhenEditBoard() {
            // given
            when(boardRepository.findById(anyLong())).thenReturn(Optional.of(mockBoard));
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> boardService.editBoard(mockMember, writeRequest))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    @Test
    @DisplayName("상위 5 인기게시글 조회 테스트")
    void testGetBoardsTop5() {
        // given
        when(boardRepository.findTop5ByOrderByViewDescIdDesc()).thenReturn(boards);

        // when
        List<BoardDetailDto> boardsTop5 = boardService.getBoardsTop5();

        // then
        assertThat(boardsTop5.size()).isEqualTo(3);
    }
}