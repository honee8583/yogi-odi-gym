package com.health.yogiodigym.board.controller.rest;

import static com.health.yogiodigym.common.message.SuccessMessage.REGISTER_BOARD_SUCCESS;
import static com.health.yogiodigym.common.message.SuccessMessage.SEARCH_BOARD_SUCCESS;
import static com.health.yogiodigym.common.message.SuccessMessage.UPDATE_BOARD_SUCCESS;

import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardDto.BoardWriteRequestDto;
import com.health.yogiodigym.board.service.BoardService;
import com.health.yogiodigym.common.dto.PageResponseDto;
import com.health.yogiodigym.common.response.HttpResponse;
import com.health.yogiodigym.member.entity.MemberOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/search")
    public ResponseEntity<?> searchBoard(BoardSearchRequestDto searchRequest,
                                         @PageableDefault Pageable pageable) {
        log.info("게시글 검색 : {}", searchRequest);
        PageResponseDto<BoardDetailDto> boards = boardService.searchBoards(searchRequest, pageable);
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, SEARCH_BOARD_SUCCESS.getMessage(), boards));
    }

    @PostMapping
    public ResponseEntity<?> registerBoard(@AuthenticationPrincipal MemberOAuth2User loginUser,
                                           @RequestBody BoardWriteRequestDto registerRequest) {
        log.info("게시글 작성 : {}", registerRequest);
        boardService.registerBoard(registerRequest, loginUser.getMember());
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, REGISTER_BOARD_SUCCESS.getMessage(), null));
    }

    @PutMapping
    public ResponseEntity<?> editBoard(@AuthenticationPrincipal MemberOAuth2User loginUser,
                                       @RequestBody BoardWriteRequestDto updateRequest) {
        log.info("게시글 수정 : {}", updateRequest);
        boardService.editBoard(loginUser.getMember(), updateRequest);
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, UPDATE_BOARD_SUCCESS.getMessage(), null));
    }
}
