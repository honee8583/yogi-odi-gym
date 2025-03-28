package com.health.yogiodigym.board.service;

import com.health.yogiodigym.board.dto.CommentDto;

import com.health.yogiodigym.member.entity.Member;
import java.util.List;

public interface CommentService {
    List<CommentDto> getComments(Long boardId);

    void addComment(Long boardId, Member member, String content);

    void deleteComment(Long commentId, Member member);
}
