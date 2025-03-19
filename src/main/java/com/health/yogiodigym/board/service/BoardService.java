package com.health.yogiodigym.board.service;

import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardWriteRequestDto;
import com.health.yogiodigym.common.dto.PageResponseDto;
import com.health.yogiodigym.member.entity.Member;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    PageResponseDto<BoardDetailDto> searchBoards(BoardSearchRequestDto searchRequest, Pageable pageable);

    void registerBoard(BoardWriteRequestDto registerRequest, Member member);

    BoardDetailDto getBoardDetail(Long id);

    void editBoard(Member member, BoardWriteRequestDto dto);

    PageResponseDto<BoardDetailDto> searchMyBoards(Member member, BoardSearchRequestDto searchRequest, Pageable pageable);

    List<BoardDetailDto> getBoardsTop5();
}
