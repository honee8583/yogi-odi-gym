package com.health.yogiodigym.board.repository.impl;

import static com.health.yogiodigym.board.entity.QBoard.board;

import com.health.yogiodigym.board.dto.BoardDto.BoardSearchRequestDto;
import com.health.yogiodigym.board.dto.BoardSearchType;
import com.health.yogiodigym.board.entity.Board;
import com.health.yogiodigym.board.repository.CustomBoardRepository;
import com.health.yogiodigym.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CustomBoardRepositoryImpl implements CustomBoardRepository {
    private final JPAQueryFactory queryFactory;

    public CustomBoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Board> searchBoards(Member member, BoardSearchRequestDto searchRequest, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .where(
                        categoryEq(searchRequest.getCategoryId()),
                        typeEq(searchRequest.getType(), searchRequest.getBoardKeyword()),
                        memberEq(member)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createDateTime.desc(), board.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.category.count())
                .from(board)
                .where(
                        categoryEq(searchRequest.getCategoryId()),
                        typeEq(searchRequest.getType(), searchRequest.getBoardKeyword()),
                        memberEq(member)
                );

        return PageableExecutionUtils.getPage(boards, pageable, countQuery::fetchOne);
    }

    private BooleanExpression typeEq(BoardSearchType type, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        } else if (type == null || type.equals(BoardSearchType.TITLE)) {
            return board.title.contains(keyword);
        } else if (type.equals(BoardSearchType.CONTEXT)) {
            return board.context.contains(keyword);
        } else if (type.equals(BoardSearchType.NAME)) {
            return board.member.name.contains(keyword);
        }
        return null;
    }

    private BooleanExpression categoryEq(Long categoryId) {
        if (categoryId != null) {
            return board.category.id.eq(categoryId);
        }
        return null;
    }

    private BooleanExpression memberEq(Member member) {
        if (member != null) {
            return board.member.eq(member);
        }
        return null;
    }
}
