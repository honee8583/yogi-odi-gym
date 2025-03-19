package com.health.yogiodigym.board.service.impl;

import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardWriteRequestDto;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.repository.BoardRepository;
import com.health.yogiodigym.board.repository.CommentRepository;
import com.health.yogiodigym.board.service.BoardService;
import com.health.yogiodigym.common.dto.PageResponseDto;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.CategoryNotFoundException;
import com.health.yogiodigym.common.exception.NotWriterOfBoardException;
import com.health.yogiodigym.lesson.entity.Category;
import com.health.yogiodigym.lesson.repository.CategoryRepository;
import com.health.yogiodigym.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardDetailDto> searchBoards(BoardSearchRequestDto searchRequest, Pageable pageable) {
        return searchBoardPages(null, searchRequest, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BoardDetailDto> searchMyBoards(Member member, BoardSearchRequestDto searchRequest, Pageable pageable) {
        return searchBoardPages(member, searchRequest, pageable);
    }

    private PageResponseDto<BoardDetailDto> searchBoardPages(Member member, BoardSearchRequestDto searchRequest, Pageable pageable) {
        Page<Board> boards = boardRepository.searchBoards(member, searchRequest, pageable);
        List<BoardDetailDto> boardDetails = boards.getContent().stream()
                .map(BoardDetailDto::new)
                .toList();
        return new PageResponseDto<>(boards, boardDetails);
    }

    @Override
    public void registerBoard(BoardWriteRequestDto boardRequest, Member member) {
        Category category = categoryRepository.findById(boardRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(boardRequest.getCategoryId()));

        Board board = Board.builder()
                .member(member)
                .category(category)
                .title(boardRequest.getTitle())
                .context(boardRequest.getContext())
                .createDateTime(LocalDateTime.now())
                .view(0)
                .edit(false)
                .build();
        boardRepository.save(board);
    }

    @Override
    public BoardDetailDto getBoardDetail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
        board.incrementView();
        return new BoardDetailDto(board);
    }

    @Override
    public void editBoard(Member member, BoardWriteRequestDto updateRequest) {
        Board board = boardRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new BoardNotFoundException(updateRequest.getId()));

        if (!board.getMember().getId().equals(member.getId())) {
            throw new NotWriterOfBoardException();
        }

        Category category = categoryRepository.findById(updateRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(updateRequest.getCategoryId()));

        board.updateBoard(updateRequest, category);
    }

    @Transactional(readOnly = true)
    public List<BoardDetailDto> getBoardsTop5() {
        return boardRepository.findTop5ByOrderByViewDescIdDesc()
                .stream()
                .map(board -> new BoardDetailDto(board, Math.toIntExact(commentRepository.countByBoard(board))))
                .collect(Collectors.toList());
    }
}
