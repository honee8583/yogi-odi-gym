package com.health.yogiodigym.util;


import com.health.yogiodigym.board.controller.rest.BoardController;
import com.health.yogiodigym.board.controller.rest.CommentController;
import com.health.yogiodigym.board.service.impl.BoardServiceImpl;
import com.health.yogiodigym.board.service.impl.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BoardController.class,
    CommentController.class
})
@WithCustomMockUser
@Import({TestSecurityConfig.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mvc;

    @MockitoBean
    protected BoardServiceImpl boardService;

    @MockitoBean
    protected CommentServiceImpl commentService;
}
