package com.health.yogiodigym.board.service.impl;

import com.health.yogiodigym.board.dto.CommentDto;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.entity.Comment;
import com.health.yogiodigym.board.repository.BoardRepository;
import com.health.yogiodigym.board.repository.CommentRepository;
import com.health.yogiodigym.board.service.CommentService;
import com.health.yogiodigym.common.exception.BoardNotFoundException;
import com.health.yogiodigym.common.exception.CommentNotFoundException;
import com.health.yogiodigym.common.exception.NoDeletePermissionException;
import com.health.yogiodigym.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreateDateTimeAsc(boardId)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void addComment(Long boardId, Member member, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));

        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .content(content)
                .createDateTime(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new NoDeletePermissionException();
        }

        commentRepository.delete(comment);
    }
}
