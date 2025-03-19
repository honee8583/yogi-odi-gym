package com.health.yogiodigym.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.lesson.entity.Category;
import com.health.yogiodigym.lesson.repository.CategoryRepository;
import com.health.yogiodigym.member.entity.Member;
import com.health.yogiodigym.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 상위5개 역순으로 조회")
    void testFindTop5ByOrderByViewDescIdDesc() {
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

        Board board1 = Board.builder()
                .view(0)
                .title("title1")
                .context("context1")
                .category(category)
                .member(member)
                .build();
        Board board2 = Board.builder()
                .view(1)
                .title("title2")
                .context("context2")
                .category(category)
                .member(member)
                .build();
        Board board3 = Board.builder()
                .view(2)
                .title("title3")
                .context("context3")
                .category(category)
                .member(member)
                .build();
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        // when
        List<Board> boards = boardRepository.findTop5ByOrderByViewDescIdDesc();

        // then
        assertThat(boards.get(0).getTitle()).isEqualTo("title3");
        assertThat(boards.get(0).getContext()).isEqualTo("context3");
    }
}