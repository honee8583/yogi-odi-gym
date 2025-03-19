package com.health.yogiodigym.util;


import com.health.yogiodigym.board.controller.BoardController;
import com.health.yogiodigym.board.service.impl.BoardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BoardController.class
})
@WithCustomMockUser
@Import({TestSecurityConfig.class})
public class ControllerTest {

    @Autowired
    protected MockMvc mvc;

    @MockitoBean
    protected BoardServiceImpl boardService;
}
