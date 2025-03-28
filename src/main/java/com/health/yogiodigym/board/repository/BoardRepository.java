package com.health.yogiodigym.board.repository;

import com.health.yogiodigym.board.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, CustomBoardRepository {
    List<Board> findAllByOrderByIdDesc();

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:boardKeyword% OR b.member.name LIKE %:boardKeyword% ORDER BY b.createDateTime DESC")
    List<Board> findByTitleOrName(@Param("boardKeyword") String boardKeyword);

    List<Board> findTop5ByOrderByViewDescIdDesc();
}
