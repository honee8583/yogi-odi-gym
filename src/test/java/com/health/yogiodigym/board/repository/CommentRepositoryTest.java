package com.health.yogiodigym.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.entity.Comment;
import com.health.yogiodigym.lesson.entity.Category;
import com.health.yogiodigym.lesson.repository.CategoryRepository;
import com.health.yogiodigym.member.entity.Member;
import com.health.yogiodigym.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("게시글의 댓글 목록 조회")
    void testFindByBoardIdOrderByCreateDateTimeAsc() {
        // given
        Member member = Member.builder()
                .name("member")
                .email("user@gmail.com")
                .build();
        memberRepository.save(member);

        Category category = Category.builder()
                .code("board")
                .name("health")
                .build();
        categoryRepository.save(category);

        Board board = Board.builder()
                .view(0)
                .title("title1")
                .context("context1")
                .category(category)
                .member(member)
                .build();
        boardRepository.save(board);

        Comment comment1 = Comment.builder()
                .board(board)
                .member(member)
                .content("comment1")
                .build();
        Comment comment2 = Comment.builder()
                .board(board)
                .member(member)
                .content("comment2")
                .build();
        ReflectionTestUtils.setField(comment1, "createDateTime", LocalDateTime.now());
        ReflectionTestUtils.setField(comment2, "createDateTime", LocalDateTime.now().minusMinutes(3));
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // when
        List<Comment> comments = commentRepository.findByBoardIdOrderByCreateDateTimeAsc(board.getId());

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getContent()).isEqualTo("comment2");
        assertThat(comments.get(1).getContent()).isEqualTo("comment1");
    }

    @Test
    @DisplayName("게시글의 댓글 수 조회")
    void testCountByBoard() {
        // given
        Member member = Member.builder()
                .name("member")
                .email("user@gmail.com")
                .build();
        memberRepository.save(member);

        Category category = Category.builder()
                .code("board")
                .name("health")
                .build();
        categoryRepository.save(category);

        Board board = Board.builder()
                .view(0)
                .title("title1")
                .context("context1")
                .category(category)
                .member(member)
                .build();
        boardRepository.save(board);

        Comment comment1 = Comment.builder()
                .board(board)
                .member(member)
                .content("comment1")
                .build();
        Comment comment2 = Comment.builder()
                .board(board)
                .member(member)
                .content("comment2")
                .build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // when
        Long commentCount = commentRepository.countByBoard(board);

        // then
        assertThat(commentCount).isEqualTo(2);
    }
}