package com.health.yogiodigym.board.controller;

import com.health.yogiodigym.board.dto.BoardDto.BoardDetailDto;
import com.health.yogiodigym.board.service.BoardService;
import com.health.yogiodigym.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardViewController {

    private final LessonService lessonService;
    private final BoardService boardService;

    @GetMapping
    public String showBoard(Model model) {
        model.addAttribute("categories", lessonService.getCategoriesByCode("board"));
        return "board/board";
    }

    @GetMapping("/register")
    public String showBoardRegister(Model model) {
        model.addAttribute("categories", lessonService.getCategoriesByCode("board"));
        return "board/register";
    }

    @GetMapping("/{id}")
    public String showBoardDetail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getBoardDetail(id));
        return "board/detail";
    }

    @GetMapping("/{boardId}/edit")
    public String showBoardUpdate(@PathVariable Long boardId, Model model) {
        BoardDetailDto boardDetailDto = boardService.getBoardDetail(boardId);

        model.addAttribute("categories", lessonService.getCategoriesByCode("board"));
        model.addAttribute("board", boardDetailDto);
        return "board/edit";
    }
}
