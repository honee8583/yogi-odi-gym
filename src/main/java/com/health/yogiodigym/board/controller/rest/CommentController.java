package com.health.yogiodigym.board.controller.rest;

import com.health.yogiodigym.board.dto.CommentDto;
import com.health.yogiodigym.board.service.CommentService;
import com.health.yogiodigym.common.response.HttpResponse;
import com.health.yogiodigym.member.entity.MemberOAuth2User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getComments(@PathVariable("boardId") Long boardId) {
        List<CommentDto> comments = commentService.getComments(boardId);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, "댓글 목록 조회에 성공하였습니다.", comments));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestParam Long boardId,
                                        @RequestParam String content,
                                        @AuthenticationPrincipal MemberOAuth2User loginUser) {
        commentService.addComment(boardId, loginUser.getMember(), content);
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, "댓글 추가에 성공하였습니다.", null));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
                                           @AuthenticationPrincipal MemberOAuth2User loginUser) {
        commentService.deleteComment(commentId, loginUser.getMember());
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, "댓글 삭제에 성공하였습니다.", null));
    }
}
