package com.health.yogiodigym.board.repository;

import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {
    Page<Board> searchBoards(Member member, BoardSearchRequestDto searchRequest, Pageable pageable);
}
